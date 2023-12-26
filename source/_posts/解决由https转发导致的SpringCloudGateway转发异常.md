---
title: 解决由https转发导致的SpringCloudGateway转发异常
date: 2021-07-26 11:06:50
comments: true
categories:
    - 我永远爱学习
tags:
    - Spring 
    - Java 
    - Debug
---

错误日志如下:

```log
2021-07-27 15:51:31.302  WARN [TID:TID-1419928421240946688][PID:1][or-http-epoll-2] r.netty.http.client.HttpClientConnect    [299]: [id: 0xe9859259, L:/10.42.8.227:41228 ! R:10.42.4.120/10.42.4.120:8080] The connection observed an error

io.netty.handler.codec.DecoderException: io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record: 485454502f312e31203430302042616420526571756573740d0a436f6e74656e742d4c656e6774683a20300d0a436f6e6e656374696f6e3a20636c6f73650d0a0d0a
        at io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:477)
        at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
        at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
        at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
        at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
        at io.netty.channel.epoll.AbstractEpollStreamChannel$EpollStreamUnsafe.epollInReady(AbstractEpollStreamChannel.java:795)
        at io.netty.channel.epoll.EpollEventLoop.processReady(EpollEventLoop.java:480)
        at io.netty.channel.epoll.EpollEventLoop.run(EpollEventLoop.java:378)
        at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
        at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.lang.Thread.run(Thread.java:748)
Caused by: io.netty.handler.ssl.NotSslRecordException: not an SSL/TLS record: 485454502f312e31203430302042616420526571756573740d0a436f6e74656e742d4c656e6774683a20300d0a436f6e6e656374696f6e3a20636c6f73650d0a0d0a
        at io.netty.handler.ssl.SslHandler.decodeJdkCompatible(SslHandler.java:1213)
        Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException:
Error has been observed at the following site(s):
        |_ checkpoint â‡¢ org.springframework.cloud.gateway.filter.WeightCalculatorWebFilter [DefaultWebFilterChain]
        |_ checkpoint â‡¢ com.alibaba.csp.sentinel.adapter.spring.webflux.SentinelWebFluxFilter [DefaultWebFilterChain]
        |_ checkpoint â‡¢ org.springframework.boot.actuate.metrics.web.reactive.server.MetricsWebFilter [DefaultWebFilterChain]
        |_ checkpoint â‡¢ HTTP GET "/某个service/v2/api-docs" [ExceptionHandlingWebHandler]
Stack trace:
                at io.netty.handler.ssl.SslHandler.decodeJdkCompatible(SslHandler.java:1213)
                at io.netty.handler.ssl.SslHandler.decode(SslHandler.java:1280)
                at io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:507)
                at io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:446)
                at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
                at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)
                at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)
                at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)
                at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)
                at io.netty.channel.epoll.AbstractEpollStreamChannel$EpollStreamUnsafe.epollInReady(AbstractEpollStreamChannel.java:795)
                at io.netty.channel.epoll.EpollEventLoop.processReady(EpollEventLoop.java:480)
                at io.netty.channel.epoll.EpollEventLoop.run(EpollEventLoop.java:378)
                at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
                at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
                at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
                at java.lang.Thread.run(Thread.java:748)
```

<!--more-->

### 问题描述

这是一个很奇怪的只在测试环境存在的异常。</br>
生产环境一切正常，测试环境访问以http访问正常，以https访问出现出现500错误。

#### 开发架构

- spring-boot:2.3.12.RELEASE
- spring-cloud:Hoxton.SR8
- spring-cloud-alibaba:2.2.5.RELEASE

#### 生产环境链路：

```
访问域名 =公网dns=> 云服务商slb =lb&https转http=> ingress-nginx =lb=> service-gateway =lb=> service
```

#### 测试环境链路：

```
访问域名 =内网dns=> ingress-nginx =lb&https转http=> service-gateway =lb=> service
```

## Debug思路

1. 查看应用日志发现service并未收到该请求，请求在service-gateway已经失败了，判断是网关问题。
2. 查看网关日志发现报错（见文章一开始处的日志）。
    1. 请求在nettyClient处的SSLHandler报错，说明网关的filter流程已经走完
    2. 最终异常是NotSslRecordException，直译为`非SSL记录异常`，不知道为啥会抛出这个异常
3. 在网关中加日志走一波，打印这个请求的所有HttpHeader，对比问题请求与正常请求的特征
    1. 查看问题请求的Header
        ```
        header:
            Host: "aaa-bbb-test.xxx.com",
            X-Scheme: "https"
            X-Request-ID: "37cd98713db6b101d813171e0be609a",
            X-Forwarded-For: "aaa-bbb-test.xxx.com",
            X-Forwarded-Port: "443",
            X-Forwarded-Proto: "https",
            referer: "https://aaa-bbb-test.xxx.com/doc.html",
            ....此处略过ContentType、Accept
        ```
    2. Header中有转发来源的部分头部，先尝试在Filter中除掉这些头部试试
        ```java
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      
              ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                      .headers(h -> {
                          h.remove("X-Forwarded-For");
                          h.remove("X-Forwarded-Port");
                          h.remove("X-Forwarded-Proto");
                      })
                      .build();
              return chain.filter(exchange.mutate().request(serverHttpRequest).build());
          }
        ```
       然而并没有什么卵用，异常依旧。
    3. 根据异常堆栈可知，是在发起请求时出现问题，尝试找到请求发起类
        - 由于网关使用webflux导致通过日志查看的堆栈的信息不够全面，通过本地调试正常请求查看异步堆栈跟踪到请求发起类`NettyRoutingFilter`
        - 110行处发现一个协议判断的代码
            ```java
                public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                   URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
            
                   String scheme = requestUrl.getScheme();
                   if (isAlreadyRouted(exchange)
                   || (!"http".equals(scheme) && !"https".equals(scheme))) {
                   return chain.filter(exchange);
                   }
                   setAlreadyRouted(exchange);
                   ...
            ```     
        - 这个`scheme`控制了网关向其他服务的转发协议，决定这个变量的是`requestUrl`
        - `requestUrl`是从exchange取出来的，通过查看`GATEWAY_REQUEST_URL_ATTR`这个常量的引用，找到了`requestUrl`
          的来源`ReactiveLoadBalancerClientFilter#filter[113]`
        - 发现源码注释
            ```
                  // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
                  // if the loadbalancer doesn't provide one.
            ```
        - 根据源码注释修改网关的配置
            ```
              修改前:
              [
                {
                "id": "service-a",
                "name": "A服务",
                "predicates": ["Path=/service-a/**"],
                "filters": [],
                "uri": "lb://service-a"
                }
              ]
              修改后:
              [
                {
                "id": "service-a",
                "name": "A服务",
                "predicates": ["Path=/service-a/**"],
                "filters": [],
                "uri": "lb:http://service-a"
                }
              ]
            ```
    4. 服务恢复正常了


## 找到根本原因
    
#### 整理出SpringCloudGateway的请求处理流程的关键类：

 1. 由LoadBalancerUriTools计算scheme、host、port。其中scheme由`overrideScheme`和``
    1. `org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter#filter[108]`
    2. `org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools#doReconstructURI[100]`

 2. 共享 GATEWAY_REQUEST_URL_ATTR 变量给其他的Filter
    1. `org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter#filter[113]`
    2. `org.springframework.cloud.gateway.filter.NettyRoutingFilter#filter[108]`

 3. 交由Netty发起请求
    1. `org.springframework.cloud.gateway.filter.NettyRoutingFilter#filter[108]`

#### 问题发生在第一步：由LoadBalancerUriTools计算scheme、host、port
 1. 在ReactiveLoadBalancerClientFilter中，主要逻辑如下
    ```java
        return choose(exchange).doOnNext(response -> {
    
            if (!response.hasServer()) {
                throw NotFoundException.create(properties.isUse404(),
                        "Unable to find instance for " + url.getHost());
            }
    
            URI uri = exchange.getRequest().getURI();
    
            // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
            // if the loadbalancer doesn't provide one.
            String overrideScheme = null;
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }
    
            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(
                    response.getServer(), overrideScheme);
    
            URI requestUrl = reconstructURI(serviceInstance, uri);
    
            if (log.isTraceEnabled()) {
                log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
            }
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }).then(chain.filter(exchange));
    ```
 2. 此处的`overrideScheme`就是刚才配置中添加的`http:`,这是优先级最高的配置
 3. 然后`response.getServer()`和`overrideScheme`被送入委派类DelegatingServiceInstance中，项目中ServiceInstance的实现类是NacosServiceInstance
 4. 紧接着`reconstructURI(serviceInstance, uri);`被执行，重建后的URI的scheme、host、port都是从`ServiceInstance`中获取的
 5. 由于NacosServiceInstance没实现getScheme()，因此进入接口的default方法返回null值
    - `org.springframework.cloud.gateway.support.DelegatingServiceInstance#getScheme[76]`
    - `org.springframework.cloud.client.ServiceInstance#getScheme[71]`
 6. 由于路由配置和服务实例中都没有取到scheme最终选取了原始URI中的scheme
    - `org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools#computeScheme`
 7. NettyRoutingFilter将Response原样送出，协议是https内容是http，因此导致在建立TLS链接时发生了异常
    - `org.springframework.cloud.gateway.filter.NettyRoutingFilter#filter[147]`
