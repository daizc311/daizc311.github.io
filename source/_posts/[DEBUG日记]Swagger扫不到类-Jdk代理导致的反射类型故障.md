---
title: '[DEBUG日记]Swagger扫不到类-Jdk代理导致的反射类型故障'
date: 2021-09-02 18:07:29
tags:
---

# Swagger扫不到类-Jdk代理导致的反射类型故障
- 可能controller实现了某个接口导致Spring使用了jdkProxy</br>在`springfox.documentation.builders.RequestHandlerSelectors#withClassAnnotation`处检查input的class，检查input的declaringClass的类型，改用cglib以修复此行为（代理类相较jdkProxy将承受10-15%性能损失）
    ```java
    // swagger的配置
    new Docket(DocumentationType.SWAGGER_2).apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
    // RequestHandlerSelectors#withClassAnnotation可用的断点表达式
    input.getName().equals("${methodName}")
    // 找个配置类加注解
    @EnableAspectJAutoProxy(
        // 示代理应由 AOP 框架公开为ThreadLocal以通过org.springframework.aop.framework.AopContext类进行检索
        exposeProxy = true,
        // 指示是否要创建基于子类 (CGLIB) 的代理，而不是基于标准 Java 接口的代理
        proxyTargetClass = true
        )
    ```
- 
