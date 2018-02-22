# 学习项目之小商城 2.0-version

### 1. 所用技术
主流框架: Springmvc + Spring + Mybatis <br/>
构建工具: maven <br/>
文件服务: vsftpd <br/>
服务器: tomcat + nginx <br/>
数据库: mysql, redis <br/>
日志框架: logback <br/>
开发平台工具: centos 7.0 + Intellij Idea <br/>

### 2. 改进
与1.0版本比较主要有以下几点变化<br/>
1.**演进为tomcat集群, 使用nginx配置负载均衡**.<br/> 
带来的问题是用户维持登录状态出现问题, sessionId失效, 因为用户访问不同的tomcat时sessionId不同, 就会出现有时是登录状态，有时是
未登录状态。<br/>
解决方案：<br/>
a. 利用Nosql redis做单点登录, 把登录用户信息序列化到redis缓存中。登录时用浏览器回写cookie, 将用户的key值保存到此cookie中。同时将<key, user>存进redis中。这样用户每次都会携带cookie过来, 服务端就能够拿到key值, 进而从redis中拿到用户信息。<br/>
b. 设置用户信息有效期为30分钟, 此时还需做一个过滤器进行拦截, 当用户访问数据时, 应该把用户信息的有效期重置为30分钟<br/>

**2.处理项目全局异常, 给用户友好错误显示** <br/>
使用springmvc全局异常处理机制, 自定义类ExceptionResolver实现HandlerExceptionResolver接口, 返回友好错误显示。

**3.对后台使用拦截器进行权限登录验证, 消除冗余复制代码**

**4.使用spring schedule做定时任务, 用来关闭用户下单一小时后还未支付的订单.**
解决办法: <br/>
为了达到每一次任务调度时, 只有一个tomcat进行关单操作, 利用redis做一个分布式锁。<br/>
原理如图所示:<br/>
![](https://github.com/waston1997/imageServer/blob/master/lock.png)


**注: 前后端分离, JDK1.8**
