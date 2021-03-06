package com.zftlive.android.library.third.ormlite.misc;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.zftlive.android.library.third.ormlite.db.DatabaseType;
import com.zftlive.android.library.third.ormlite.logger.Logger;
import com.zftlive.android.library.third.ormlite.logger.LoggerFactory;
import com.zftlive.android.library.third.ormlite.support.ConnectionSource;
import com.zftlive.android.library.third.ormlite.support.DatabaseConnection;

/**
 * Provides basic transaction support for a {@link ConnectionSource}.
 * 
 * <p>
 * <b>NOTE:</b> For transactions to work, the database being used must support the functionality.
 * </p>
 * 
 * <p>
 * <b> NOTE: </b> If you are using the Spring type wiring in Java, {@link #initialize} should be called after all of the
 * set methods. In Spring XML, init-method="initialize" should be used.
 * </p>
 * 
 * <p>
 * You can call this as an instance with a new TransactionManager(dataSource); or you can call it as a static like the
 * below example:
 * 
 * <blockquote>
 * 
 * <pre>
 * TransactionManager.callInTransaction(dataSource, new Callable&lt;Void&gt;() {
 * 	public Void call() throws Exception {
 * 		// delete both objects but make sure that if either one fails, the transaction is rolled back
 * 		// and both objects are &quot;restored&quot; to the database
 * 		fooDao.delete(foo);
 * 		barDao.delete(bar);
 * 		return null;
 * 	}
 * });
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * For Spring wiring of a Transaction Manager bean, we would do something like the following:
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * &lt;bean id="transactionManager" class="com.j256.ormlite.misc.TransactionManager" init-method="initialize"&gt;
 * 	&lt;property name="dataSource" ref="dataSource" /&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * </blockquote>
 * 
 * @author graywatson
 */
public class TransactionManager {

	private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
	private static final String SAVE_POINT_PREFIX = "ORMLITE";

	private ConnectionSource connectionSource;
	private static AtomicInteger savePointCounter = new AtomicInteger();

	/**
	 * Constructor for Spring type wiring if you are using the set methods.
	 */
	public TransactionManager() {
		// for spring wiring -- must call setDataSource()
	}

	/**
	 * Constructor for direct java code wiring.
	 */
	public TransactionManager(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
		initialize();
	}

	/**
	 * If you are using the Spring type wiring, this should be called after all of the set methods.
	 */
	public void initialize() {
		if (connectionSource == null) {
			throw new IllegalStateException("dataSource was not set on " + getClass().getSimpleName());
		}
	}

	/**
	 * Execute the {@link Callable} class inside of a transaction. If the callable returns then the transaction is
	 * committed. If the callable throws an exception then the transaction is rolled back and a {@link SQLException} is
	 * thrown by this method.
	 * 
	 * <p>
	 * <b> NOTE: </b> If your callable block really doesn't have a return object then use the Void class and return null
	 * from the call method.
	 * </p>
	 * 
	 * @param callable
	 *            Callable to execute inside of the transaction.
	 * @return The object returned by the callable.
	 * @throws SQLException
	 *             If the callable threw an exception then the transaction is rolled back and a SQLException wraps the
	 *             callable exception and is thrown by this method.
	 */
	public <T> T callInTransaction(final Callable<T> callable) throws SQLException {
		return callInTransaction(connectionSource, callable);
	}

	/**
	 * Same as {@link #callInTransaction(Callable)} except as a static method with a connection source.
	 */
	public static <T> T callInTransaction(final ConnectionSource connectionSource, final Callable<T> callable)
			throws SQLException {

		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		try {
			boolean saved = connectionSource.saveSpecialConnection(connection);
			return callInTransaction(connection, saved, connectionSource.getDatabaseType(), callable);
		} finally {
			// we should clear aggressively
			connectionSource.clearSpecialConnection(connection);
			connectionSource.releaseConnection(connection);
		}
	}

	/**
	 * Same as {@link #callInTransaction(Callable)} except as a static method on a connection.
	 */
	public static <T> T callInTransaction(final DatabaseConnection connection, final DatabaseType databaseType,
			final Callable<T> callable) throws SQLException {
		return callInTransaction(connection, false, databaseType, callable);
	}

	/**
	 * Same as {@link #callInTransaction(Callable)} except as a static method on a connection.
	 */
	public static <T> T callInTransaction(final DatabaseConnection connection, boolean saved,
			final DatabaseType databaseType, final Callable<T> callable) throws SQLException {

		boolean autoCommitAtStart = false;
		try {
			boolean hasSavePoint = false;
			Savepoint savePoint = null;
			if (saved || databaseType.isNestedSavePointsSupported()) {
				if (connection.isAutoCommitSupported()) {
					autoCommitAtStart = connection.isAutoCommit();
					if (autoCommitAtStart) {
						// disable auto-commit mode if supported and enabled at start
						connection.setAutoCommit(false);
						logger.debug("had to set auto-commit to false");
					}
				}
				savePoint = connection.setSavePoint(SAVE_POINT_PREFIX + savePointCounter.incrementAndGet());
				if (savePoint == null) {
					logger.debug("started savePoint transaction");
				} else {
					logger.debug("started savePoint transaction {}", savePoint.getSavepointName());
				}
				hasSavePoint = true;
			}
			try {
				T result = callable.call();
				if (hasSavePoint) {
					commit(connection, savePoint);
				}
				return result;
			} catch (SQLException e) {
				if (hasSavePoint) {
					try {
						rollBack(connection, savePoint);
					} catch (SQLException e2) {
						logger.error(e, "after commit exception, rolling back to save-point also threw exception");
						// we continue to throw the commit exception
					}
				}
				throw e;
			} catch (Exception e) {
				if (hasSavePoint) {
					try {
						rollBack(connection, savePoint);
					} catch (SQLException e2) {
						logger.error(e, "after commit exception, rolling back to save-point also threw exception");
						// we continue to throw the commit exception
					}
				}
				throw SqlExceptionUtil.create("Transaction callable threw non-SQL exception", e);
			}
		} finally {
			if (autoCommitAtStart) {
				// try to restore if we are in auto-commit mode
				connection.setAutoCommit(true);
				logger.debug("restored auto-commit to true");
			}
		}
	}

	public void setConnectionSource(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

	private static void commit(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
		String name = (savePoint == null ? null : savePoint.getSavepointName());
		connection.commit(savePoint);
		if (name == null) {
			logger.debug("committed savePoint transaction");
		} else {
			logger.debug("committed savePoint transaction {}", name);
		}
	}

	private static void rollBack(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
		String name = (savePoint == null ? null : savePoint.getSavepointName());
		connection.rollback(savePoint);
		if (name == null) {
			logger.debug("rolled back savePoint transaction");
		} else {
			logger.debug("rolled back savePoint transaction {}", name);
		}
	}
}
