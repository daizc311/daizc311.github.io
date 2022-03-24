---
title: 改造Spring-data-redis 优雅设置过期时间
comments: true
categories:
  - [我永远爱学习]
tags:
  - Java
date: 2020-08-07 12:00:55
updated: 2020-08-07 12:00:55
---


Spring-data-redis是一个比较优雅的缓存解决方案，只需要在对应的方法上打上注解就可以便捷的将数据放入redis。

但Spring-data-redis中配置TTL只能按照`cacheName`的维度进行配置，并不能精确到具体的接口上。

比如以下需求就不能优雅的实现：
```java
public interface CacheDemo{

    // 这个接口只缓存30min
    @Cacheable(value = "user", keyGenerator = "xxxx")
    List<User> listByQuery(UserQuery query);
    
    // 这个接口只缓存1min
    @Cacheable(value = "user", keyGenerator = "xxxx")
    List<User> takeUserFromObj(Object obj);
    
    // 这个接口缓存180min
    @Cacheable(value = "user", keyGenerator = "xxxx")
    List<User> getByUserId(String userId);
}
```

这篇文章就将以定制Cache具体实现类的方式优雅的实现这个功能.
<!--more-->
