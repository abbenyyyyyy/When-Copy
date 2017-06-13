#Android 项目————When Copy

## 简介

在手机上阅读技术或科普文章时候，遇上技术名词，有时候需要搜索或翻译了解，这时候需要复制技术名词，再打开浏览器打开搜索引擎剪切搜索，步骤繁琐，然后就有了开发这个APP的想法。  

功能主要是为了在手机上实现复制后自动打开以复制内容为关键字的百度搜索。之后拓展出翻译，创建日历事件备忘录。

[APK下载](http://fir.im/nfrq)

## APP展示
![first.gif](http://ww4.sinaimg.cn/mw690/71a00955gw1f6pkpmn7d0g209q0h51l0.gif)

![second.gif](http://ww4.sinaimg.cn/mw690/71a00955gw1f6pkq9oj9fg209q0h5u12.gif)

##技术要点
* 通过另开进程的`Service`利用`ClipboardManager.addPrimaryClipChangedListener()`进行剪切板监控
* IPC方式(主进程和另一进程的`Service`通信)：<del>主进程利用`BroadcastReceiver`通知`Service`</del>，2.3.3已更换IPC方式为AIDL
* 使用`SharedPreferences`持久化用户设置

##更新说明

v2.3.3
* 引入APP更新检查下载功能
* IPC(跨进程通讯)方式从BroadcastReceiver更换为AIDL，以修复开启APP时候初始化白屏问题
* 更新RxJava2,移除RxBinding和RxShareperence

v2.2.0
* 剪切板监控的后台服务与主进程分离，以便持久化后台服务
* 加入显示选择和翻译弹框的过度动画

v2.1.0
* 改进UI，使用`Material Design`风格 
* 修复在某些机型下自动触发显示选择和翻译弹框的BUG

v2.0.0
*  改进UI，全面更换图标
*  架构改进，抽离选择和翻译弹框
*  引入`RxShareperence`管理`sharedpreferences`
*  不再需要用户设置悬浮权限显示选择和翻译弹框

v1.0.0
* 实现基本的功能，包括复制后自动打开以复制内容为关键字的百度搜索。之后拓展出翻译，创建日历事件备忘录。初步架构为自己思考实现的‘MVP模式’。[v1.0.0开发文档](https://abbenyyyyyy.github.io/WhenCopy-DevelopmentDocumentation.html)

##计划改进
* 增加用户设置搜索引擎，翻译引擎功能
* 增加代码混淆
	
## 联系方式

Email:abbenyyyyyy@qq.com

##License
The MIT License (MIT)

Copyright (c) 2016 abbenyyyyyy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.