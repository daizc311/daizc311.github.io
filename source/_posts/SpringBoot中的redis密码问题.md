---
title: 'SpringBoot中的redis密码问题'
date: 2019/05/13 12:03:01
updated: 2020/2/20 15:41:36
comments: true
categories: 
    - [<del>我永远爱学习</del>]
tags: 
    - spring
    - java
    - dubug
---

很简单的一个问题，花了大力气去解决。重要的是解决问题的方式和思路。
<!--more-->

## 出现问题
 
出问题的配置如下


---

application.yml

```yml
spring:
  redis:
    # REDIS_URL
    url: redis://192.168.1.233:6379
    # 如果有密码
    password: testpassword123
    # 存储到分区1
    database: 1
    # 默认超时2000
    timeout: 3000


```


报错的日志

```log
[20190827 20:42:53] ERROR 7900 --- [nio-8086-exec-2] c.z.v.d.a.c.mvc.GlobalExceptionHandler   : Unable to connect to Redis; nested exception is io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required.

org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis; nested exception is io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required.
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getNativeConnection(LettuceConnectionFactory.java:1092)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getConnection(LettuceConnectionFactory.java:1065)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getSharedConnection(LettuceConnectionFactory.java:865)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getConnection(LettuceConnectionFactory.java:340)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.execute(DefaultRedisCacheWriter.java:238)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.get(DefaultRedisCacheWriter.java:109)
	at org.springframework.data.redis.cache.RedisCache.lookup(RedisCache.java:82)
	at org.springframework.cache.support.AbstractValueAdaptingCache.get(AbstractValueAdaptingCache.java:58)
	at org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:73)
	at org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:554)
	at org.springframework.cache.interceptor.CacheAspectSupport.findCachedItem(CacheAspectSupport.java:519)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:401)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:345)
	at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:61)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at com.zunchen.video.dmp.service.impl.onvif.OnvifDeviceCacheServiceImpl$EnhancerBySpringCGLIB$913f47a3.connectOnvifDevice(<generated>)
	at com.zunchen.video.dmp.admin.api.ConnectDeviceApiController.connect(ConnectDeviceApiController.java:36)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:215)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:142)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:800)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:998)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:890)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:875)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter.doFilterInternal(HttpTraceFilter.java:90)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.filterAndRecordMetrics(WebMvcMetricsFilter.java:154)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.filterAndRecordMetrics(WebMvcMetricsFilter.java:122)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:107)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:199)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:770)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1415)
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)
Caused by: io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required.
	at io.lettuce.core.ExceptionFactory.createExecutionException(ExceptionFactory.java:135)
	at io.lettuce.core.LettuceFutures.awaitOrCancel(LettuceFutures.java:122)
	at io.lettuce.core.AbstractRedisAsyncCommands.select(AbstractRedisAsyncCommands.java:1194)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at io.lettuce.core.FutureSyncInvocationHandler.handleInvocation(FutureSyncInvocationHandler.java:57)
	at io.lettuce.core.internal.AbstractInvocationHandler.invoke(AbstractInvocationHandler.java:80)
	at com.sun.proxy.$Proxy206.select(Unknown Source)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getNativeConnection(LettuceConnectionFactory.java:1087)
	... 69 common frames omitted
```

## 查询资料

 - [spring boot 配置redis 报错 NOAUTH Authentication required](https://bbs.csdn.net/topics/392009425)
 - [springboot整合redis报错NOAUTH Authentication required.解决方案](https://www.twblogs.net/a/5b7e13012b717768385523f9/zh-cn)
 
### 归纳
  大多解决方案都是通过配置注入的RedisConfig来setPassword解决的，SpringBoot未注入密码的原因仍然不明。

## 原因探究

### 原因分析

  由报错信息可以看出，最终抛出的异常是io.lettuce.core.RedisCommandExecutionException，其中lettuce不知道是什么。

  通过查阅资料 [高级的 Redis Java 客户端 Lettuce](https://toutiao.io/posts/pknpcs/preview) 得知lettuce是springboot2.0中用来代替jedis的新的redis客户端。

  那为何lettuce为何会报错呢？这是我最大的疑惑，我决定去spring.redis的自动配置类中看看到底发生了什么

### 源码跟踪

&gt; spring.redis的自动配置包：org.springframework.boot.autoconfigure.data.redis

![](leanote://file/getImage?fileId=5d847766e1488a763c000011)

看着这个目录结构，第一反应直奔LettuceConnectionConfiguration中去看源码。

![](leanote://file/getImage?fileId=5d84778be1488a763c000012)

在其构造方法处打断点发现是拿到了密码的，发现同类下有一方法createLettuceConnectionFactory();明显是创建连接工厂的方法，断点套上去。

![](leanote://file/getImage?fileId=5d8477a7e1488a763c000013)

这个方法中干了啥，它看了看配置文件中有没有包含${spring.redis.sentinel}和${spring.redis.cluster}的配置，如果有就拿对应的配置去创建连接工厂。但是yml中既没有配置哨兵模式也没有配置集群模式，所以进入独立模式getStandaloneConfig();

![](leanote://file/getImage?fileId=5d8478d0e1488a763c000014)

顺手计算一下，看看standaloneConfig长什么样子…..嗯???!!!……密码呢？……发生了啥？

![](leanote://file/getImage?fileId=5d8478e8e1488a763c000015)

问题找到了，感觉自己好蠢。

## 解决问题

### 修改配置

```yml
spring:
  redis:
    # REDIS_URL 中自己带上密码
    # Connection URL. Overrides host, port, and password. User is ignored. Example:
    # redis://user:password@example.com:6379
    # url: redis://UserIsIgnored:testpassword123@192.168.1.233:6379
    # HOST 和 PASSWORD 是一套配置
    host: 192.168.1.233
    # 如果有密码 （用于RedisTemplate ，Lettuce不会拿这个password）
    password: testpassword123
    # 存储到分区1
    database: 1
    # 默认超时2000
    timeout: 3000
```

修改后启动：

```log
[20190828 03:15:04] ERROR 8564 --- [nio-8086-exec-2] c.z.v.d.a.c.mvc.GlobalExceptionHandler   : Unable to connect to Redis; nested exception is io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required.

org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis; nested exception is io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required.
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getNativeConnection(LettuceConnectionFactory.java:1092)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getConnection(LettuceConnectionFactory.java:1065)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getSharedConnection(LettuceConnectionFactory.java:865)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getConnection(LettuceConnectionFactory.java:340)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.execute(DefaultRedisCacheWriter.java:238)
	at org.springframework.data.redis.cache.DefaultRedisCacheWriter.get(DefaultRedisCacheWriter.java:109)
	at org.springframework.data.redis.cache.RedisCache.lookup(RedisCache.java:82)
	at org.springframework.cache.support.AbstractValueAdaptingCache.get(AbstractValueAdaptingCache.java:58)
	at org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:73)
	at org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:554)
	at org.springframework.cache.interceptor.CacheAspectSupport.findCachedItem(CacheAspectSupport.java:519)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:401)
	at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:345)
	at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:61)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:688)
	at com.zunchen.video.dmp.service.impl.onvif.OnvifDeviceCacheServiceImpl$EnhancerBySpringCGLIB$b001f767.connectOnvifDevice(<generated>)
	at com.zunchen.video.dmp.admin.api.ConnectDeviceApiController.connect(ConnectDeviceApiController.java:36)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:215)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:142)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:800)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:998)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:890)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:875)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter.doFilterInternal(HttpTraceFilter.java:90)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.filterAndRecordMetrics(WebMvcMetricsFilter.java:154)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.filterAndRecordMetrics(WebMvcMetricsFilter.java:122)
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:107)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:199)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:770)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1415)
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)
```

仍然在报错，这下尴尬了。


### 再次解决问题

![](leanote://file/getImage?fileId=5d847965e1488a763c000016)

径直来到报错信息提示的地方，估计是红框出出现了异常。此处调用的是接口方法，不知道进了那个方法，没关系，打个断点强制步入。

![](leanote://file/getImage?fileId=5d847984e1488a763c000017)

有点懒，不想解释了，直接画图，发现是redisURISupplier中的password仍然是空的。不想知道为什么了，我只想解决问题。源码点过去看下。

![](leanote://file/getImage?fileId=5d847996e1488a763c000018)

 查看该类的构造方法，这个东西正在解析RedisURI，那RedisURI哪儿来的呢？配置里配的呗。
 
## 成功解决问题

```yml
spring:
  redis:
    # REDIS_URL 中自己带上密码
    # Connection URL. Overrides host, port, and password. User is ignored. Example:
    # redis://user:password@example.com:6379
    url: redis://UserIsIgnored:testpassword123@192.168.1.233:6379
    # HOST 和 PASSWORD 是一套配置
    # host: 192.168.1.233
    # 如果有密码 （用于RedisTemplate ，Lettuce不会拿这个password）
    # password: testpassword123
    # 存储到分区1
    database: 1
    # 默认超时2000
    timeout: 3000
```

这次好了，redis成功连接。大功告成。






