# 简介

   本文来自zftlive(本人只是暂存,再次表明,其中也加入了其他的一些工具类,只是暂存本人使用过和见过有用的工具类,实力不足非我原创)，顾名思义“曾繁添的生活”，我是一个追求技术的代码狂，我的生活很单纯，占据我大部分生活的是代码，在工作之余整理/沉淀自己所学、所能，一方面可以记录一下，方便日后使用、开发；另一方面开源共享可以帮助一些需要这方面资源的同学，两全其美的事情何乐而不为.


#集成组件

 - [√] 网络异步请求[android-async-http][1]
 - [√] 图片异步加载[universal-image-loader][2]
 - [√] 本地数据库操作[ormlite-android][3]
 - [√] 消息推送-[腾讯信鸽][4]
 - [x] 即时通讯-[环信][5]
 - [√] 地图基础+定位+LBS （LBS暂未完成）[百度地图][6]
 - [√] App埋点/崩溃异常跟踪/日活统计[腾讯MTA][7]
 - [√] 社会化分享/第三方账号联合登录/短信验证码[ShareSDK][8]
 - [√] 快速生成渠道包[MultiChannelBuildTool][9]
 - [√] 二维码/条形码扫描和生成[Zxing二维码库][10]
 - [√] 低版本动画兼容库[nineoldandroids][11]
 - [√] IOC注入布局layout/控件/事件/Android四大组件[RoboGuice][12]
 - [√] WebService异步请求接口调用库[ksoap2][13]
 - [√] JSON/Bean互转组件 [gson][14]
 - [√] 折线图/饼图/柱状图/图表控件AChartEngine

#样例锦集

 - 共通界面-引导界面
 - 共通界面-意见反馈
 - 共通界面-本地/服务器WEB页加载界面（带进度显示+获取网页标题+分享）
 - 共通机能-版本检测升级
 - 共通机能-消息推送
 - 共通机能-MTA埋点/异常崩溃监控
 - 共通机能-全程网络状态监听Service
 - 共通机能-手势解锁
 - 共通机能-可缩放图片查看器（支持项目工程assets/drawable/sd卡/网络类型的图片）
 - 样例-ActionBar标题栏
 - 样例-底部弹出泡泡菜单
 - 样例-Achartengine带泡泡提示的曲线图
 - 样例-异步加载图片+防错位DEMO
 - 样例-直接启动摄像头拍照
 - 样例-Sqlite增删改查操作
 - 样例-生成二维码/验证码（可带LOGO图片）
 - 样例-竖屏扫描二维码/条形码
 - 样例-调用WebService示例
 - 样例-发送手机短信验证码
 - 样例-垂直+水平+圆形进度条
 - 样例-自动获取表单数据示例
 - 样例-Activity切换过场动画示例
 - 样例-发送短信/拦截示例
 - 样例-基本常用操作-跳转至WIFI设置、联系人、相册选择等界面
 - 样例-DrawerLayout侧滑菜单
 - 样例-水平/垂直滚动+首行首列固定+翻页Listview
 - 样例-下拉背景回弹效果的ScrollView
 - 样例-FadingActionBar官方DEMO（随着滑动ActionBar背景渐变）
 - 样例-PullToReresh官方DEMO
 - 样例-任何设备正方形Item的网格
 - 样例-带进度显示文件上传/下载
 - 样例-JNI/Java实现图片高斯模糊
 - 样例-dialog各种特效DEMO
 - 样例-带数字的进度条
 - 样例-百度地图定位演示
 - 样例-控制系统音量
 - 样例-九宫格解锁
 - 样例-图片缩放查看器
 - 样例-拍照/相册选择+裁剪图片
 - 未完/待续，敬请关注...

#封装工具类

 - HTTP网络通信工具类（ToolHTTP.java）,get/post请求，支持多种接口回调
 - SOAP协议通信工具类（ToolSOAP.java）,基于异步回调请求WebService接口
 - Sqlite数据库操作工具类（ToolDatabase.java），获取DAO、创建表、删除表等API
 - 提示信息对话框工具类（ToolAlert.java）,已集成泡泡、土司、对话框三种提示
 - 文件操作工具类（ToolFile.java）,assets/raw/xml/shrePerface/等文件读写API
 - 地图定位工具类（ToolLocation.java）,读取GPS状态、请求定位、获取经纬度等方法
 - 社会化分享工具类（ToolShareSDK.java）,各大开发平台分享API操作
 - 短信验证码工具类（ToolMSM.java）,移动/联通/电信三网发送手机短信验证码、异步回调验证结果
 - 字符串操作工具类（ToolString.java）,生成UUID、非空非NULL逻辑判断、生成MD5等常用共通方法
 - 数据操作工具类（ToolData.java）,自动递归获取表单数据封装成Map、本地数据分页共通方法等
 - 图片操作工具类（ToolPicture.java）,生成二维码、验证码、灰度、合成、圆角、水印等操作
 - 读取本地资源工具类（ToolResource.java）,反射本地资源文件API，避免依赖R文件，方便jar形式发布
 - Android单位转换工具类（ToolUnit.java）,sp/dp/px互转
 - 自定义Toast工具类（ToolToast.java）,自定义背景色、字体颜色、大小、边框等
 - Properties操作工具类（ToolProperties.java）,读写Properties文件操作
 - 网络操作工具类（ToolNetwork.java）,获取网络信息、更改切换网络等相关操作
 - 日期操作工具类（ToolDateTime.java）,获取日期、日期加减、格式化日期、日期转换等操作
 - XML操作工具类（ToolXml.java）,基于DOM/XMLPullPaser模式解析、生成XML操作
 - 适配字体工具类（ToolAutoFit.java）,代码根据设备密度自动缩放View的字体大小
 - LOG相关工具类（ToolLog.java） 
 - 未完/待续，敬请关注...
