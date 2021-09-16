---
title: '[DEBUG日记]Swagger扫不到类-Jdk代理导致的反射类型故障' date: 2021-09-02 18:07:29

categories:

- [我永远爱学习]

tags:

- debug

---

## 先说结论

jdk代理的问题，换用cglib代理就好了。随便找个配置类，加上` @EnableAspectJAutoProxy`注解即可解决。
<!--more-->

## 整理接口后突然暴毙

整理了一下项目中的Controller，然后有几个接口就扫描不到了。无论怎么更换扫描注解都找不到这些接口，甚至使用包路径扫描都找不到，很是魔幻。

## 哪痛治哪

俗话说哪里痛治哪里，既然swagger扫不到接口，就先看看swagger到底是怎么去扫接口的

1. 先看swagger的配置
   ```java 
    @Bean("docket")
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描带Api注解的类
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .globalOperationParameters(globalOperationParameters())
                ;
    }
   ```
2. 再看上文中`.apis()`这个接口的源码
   ```java
   public ApiSelectorBuilder apis(Predicate<RequestHandler> selector) {
       requestHandlerSelector = requestHandlerSelector.and(selector);
       return this;
   }
   ```
   看这方法签名，返回值是this没啥用,直接忽略。入参是个将`RequestHandler`处理成布尔值的函数， 根据其变量名`selector`可以猜测其作用是传入一个RequestHandler判断其是否满足条件,这里的条件是指啥？
   当然是指我们从外部传进来的`RequestHandlerSelectors.withClassAnnotation(Api.class)`这个玩意儿啦！
3. 在深入我们传入的`RequestHandlerSelectors.withClassAnnotation(Api.class)`中，看他的源码：
   ```java
   /**
    * Predicate that matches RequestHandler with given annotation on the declaring class of the handler method
    *
    * @param annotation - annotation to check
    * @return this
    */
   public static Predicate<RequestHandler> withClassAnnotation(final Class<? extends Annotation> annotation) {
      return input -> declaringClass(input).map(annotationPresent(annotation)).orElse(false);
   }   
   ```
   这里是判断是否满足条件的关键位置，每一个RequestHandler(就是上面的input)都会通过上文的表达式判断得到一个布尔值
4. 再继续往前找到调用这个函数的地方
   ```java
   // 上面第二点的位置是在ApiSelectorBuilder中，要想找到第三点的函数真正在哪里被使用了，需要反向找回去
   // 在springfox.documentation.spi.service.contexts.ApiSelector中找到了我们传入的`private final Predicate<RequestHandler> requestHandlerSelector;`
   // 通过其getter反向找回去
   // 找到了springfox.documentation.spring.web.scanners.ApiListingReferenceScanner#scan
    public ApiListingReferenceScanResult scan(DocumentationContext context) {
       LOG.info("Scanning for api listing references");
   
       Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings
           = new HashMap<>();
   
       int requestMappingContextId = 0;
   
       ApiSelector selector = context.getApiSelector();
    // 看这里，要找的东西就在这里
       Iterable<RequestHandler> matchingHandlers = context.getRequestHandlers().stream()
           .filter(selector.getRequestHandlerSelector()).collect(toList());
       for (RequestHandler handler : matchingHandlers) {
         ResourceGroup resourceGroup = new ResourceGroup(
             handler.groupName(),
             handler.declaringClass(),
             0);
   
         RequestMappingContext requestMappingContext
             = new RequestMappingContext(
             String.valueOf(requestMappingContextId),
             context,
             handler);
   
         resourceGroupRequestMappings.putIfAbsent(
             resourceGroup,
             new ArrayList<>());
         resourceGroupRequestMappings.get(resourceGroup).add(requestMappingContext);
   
         ++requestMappingContextId;
       }
       return new ApiListingReferenceScanResult(resourceGroupRequestMappings);
   }
   ```
5. 打上断点开始Debug，看看controller有没有被swagger扫到
   ![ApiListingReferenceScanner](https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/note/%5BDEBUG%E6%97%A5%E8%AE%B0%5DSwagger%E6%89%AB%E4%B8%8D%E5%88%B0%E7%B1%BB-Jdk%E4%BB%A3%E7%90%86%E5%AF%BC%E8%87%B4%E7%9A%84%E5%8F%8D%E5%B0%84%E7%B1%BB%E5%9E%8B%E6%95%85%E9%9A%9C/ApiListingReferenceScanner.png "ApiListingReferenceScanner")
   发现swagger是直接从spring中取出的RequestHandler，而我们的Controller是被Spring正常扫描的(废话，没被Spring扫到都不能用了好吧)
6. 前往步骤3处打上条件断点
   ![RequestHandlerSelectors#1](https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/note/%5BDEBUG%E6%97%A5%E8%AE%B0%5DSwagger%E6%89%AB%E4%B8%8D%E5%88%B0%E7%B1%BB-Jdk%E4%BB%A3%E7%90%86%E5%AF%BC%E8%87%B4%E7%9A%84%E5%8F%8D%E5%B0%84%E7%B1%BB%E5%9E%8B%E6%95%85%E9%9A%9C/RequestHandlerSelectors%231.png "RequestHandlerSelectors#1")
   ？？？这是咋回事
7. 继续排查
   ![RequestHandlerSelectors#2](https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/note/%5BDEBUG%E6%97%A5%E8%AE%B0%5DSwagger%E6%89%AB%E4%B8%8D%E5%88%B0%E7%B1%BB-Jdk%E4%BB%A3%E7%90%86%E5%AF%BC%E8%87%B4%E7%9A%84%E5%8F%8D%E5%B0%84%E7%B1%BB%E5%9E%8B%E6%95%85%E9%9A%9C/RequestHandlerSelectors%232.png "RequestHandlerSelectors#2")
   草，这个类被jdk代理了，这直接导致反射获取类型的时候获取到了jdk的`Proxy$?.class`这个类型，从而导致检查注解失败
   `declaringClass()`方法原本应该获取到声明类，我们预计他应该会返回我们Controller的真实类型，但是由于Controller实现了一个接口，
   导致Spring使用了jdk代理这个对象，从而导致获取声明类时获取到了jdk代理所用的类型，最终导致获取注解失败
8. 问题清楚了，都是jdk代理的锅，那么我们有多个方法解决这个问题：
    1. 使用继承代替实现
    2. 使用cglib代替jdkProxy进行代理

## 最终解决

- 由于这里定义在Controller上的的接口是由其他模块的spi，为了保证调用安全，因此采用方式2来解决
  ```java
  // 随便找个配置类加注解
  @EnableAspectJAutoProxy(
      // 示代理应由 AOP 框架公开为ThreadLocal以通过org.springframework.aop.framework.AopContext类进行检索
      exposeProxy = true,
      // 指示是否要创建基于子类 (CGLIB) 的代理，而不是基于标准 Java 接口的代理
      proxyTargetClass = true
      )
  ```
  采用jdk的方式代理会更快，相较于cglib会有一定的性能提升，但应该仅在启动时有提升，类加载完毕Bean注册完成后应该都是一样的，随口BB一句没有验证过是否正确。
- 最后上个图对比一下
  ![RequestHandlerSelectors#3](https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/note/%5BDEBUG%E6%97%A5%E8%AE%B0%5DSwagger%E6%89%AB%E4%B8%8D%E5%88%B0%E7%B1%BB-Jdk%E4%BB%A3%E7%90%86%E5%AF%BC%E8%87%B4%E7%9A%84%E5%8F%8D%E5%B0%84%E7%B1%BB%E5%9E%8B%E6%95%85%E9%9A%9C/RequestHandlerSelectors%233.png "RequestHandlerSelectors#3")



