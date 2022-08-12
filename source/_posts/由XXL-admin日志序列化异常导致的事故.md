---
title: 由XXL-admin日志序列化异常导致的事故
date: 2022-08-12 17:03:00

categories:
- [我永远爱学习]
tags:
- Java
- Debug
---

1. 客户端回调日志序列化出现问题
2. 服务端收到callback后反序列化失败抛出500
3. 客户端收到500错误码无限重试
4. 由于配置错误多个客户端扫描了NFS上同一个日志目录
5. 多个客户端重试同一条日志，**错误被放大**
6. 服务端错误日志中打印了出入参，由于大量客户端重发callback，日志狂刷
7. xxl服务端日志滚动规则仅仅配置了日期滚动，未配置按大小滚动，**错误兜底失败**
8. 服务器用于存储日志的磁盘爆满，所有服务都爆炸啦！！（高兴

<!--more-->

关键堆栈信息，详情之后补充

```
com.xxl.job.core.thread.TriggerCallbackThread#start
com.xxl.job.core.thread.TriggerCallbackThread#retryFailCallbackFile
com.xxl.job.core.thread.TriggerCallbackThread#doCallback
com.xxl.job.core.thread.TriggerCallbackThread#doCallback[166]
```

- [反序列化失败的日志示例：xxl-job-callback-1660189083244.log](https://oss.note.dreamccc.cn/note/images/posts/由XXL-admin日志序列化异常导致的事故/xxl-job-callback-1660189083244.log)

