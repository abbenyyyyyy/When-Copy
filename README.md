#Android 项目————When Copy

## 简介

主要是为了在手机上实现复制后自动打开以复制内容为关键字的百度搜索。之后拓展出翻译，创建日历事件备忘录。

[APK下载](http://yun.baidu.com/s/1oKPuY)

## APP截图
![ben.jpg](http://img1.ph.126.net/SuZKFbYqHS3HCwmJiIBPBw==/6619384355143056568.jpg)

下面是复制`test`后选择翻译的效果截图
![ben.jpg](http://img1.ph.126.net/witg4rP94K_QCBa9PXXV8w==/6630227738816930758.jpg)
![ben.jpg](http://img2.ph.126.net/2TXmjUQtWJi8g1-gUFNVaQ==/6631355837746480769.jpg)
![ben.jpg](http://img0.ph.126.net/8V3mDsgjQxXz4jtDKUMn2A==/6630611468375722941.jpg)

## 开发文档

### 主要功能以及实现

1. 监控剪切板的内容变化

通过启动`Service`利用`ClipboardManager.addPrimaryClipChangedListener()`进行监控，当剪切板内容变化时进行弹出底部悬浮窗；

2. 底部悬浮窗选择利用复制内容进行的功能：以复制内容进行百度搜索，进行翻译，或者创建日历事件备忘录

悬浮窗的实现是利用`WindowManager.addView()`实现；

3. 以复制内容为关键字的百度搜索是通过输入Uri启动第三方浏览器；

4. 进行翻译是通过异步线程向申请的百度翻译WEB的API进行GET请求，回传JSON数据，分析后得出翻译结果并显示；

5. 创建日历事件备忘录是`Intent(Intent.ACTION_INSERT)`,传送复制内容数据到系统日历，系统日历弹出具体窗口让用户操作。

### 项目结构
这次尝试基于‘MVP模式’进行设计，主要通过`BaseAdapter`适配主页面的`ListView`实现view与数据Model的交互分离
* app——顶级父类
    
	1.MyService——后台服务，APP的主功能实现都在里面。
	
* modle——数据层,数据模型
    
	1.MainModel——存储ListView的一个Item具体信息的模型；
	
	2.PreferencesController——利用`SharedPreferences`存储APP要功能的实现与否还有获得ListView的Item的数据。
	
* view——视图层
	
	1.MainActivity——主Activity里面只有一个ListView。
	
* presenter——主导器，操作model和view
	
	1.ListViewAdapter——这里利用`BaseAdapter`，activity可以把所有逻辑给presenter处理，这样业务逻辑就从手机的activity中分离出来。
	


### 联系方式

Email:407523391@qq.com