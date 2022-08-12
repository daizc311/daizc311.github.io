---
title: 由XXL-admin日志序列化异常导致的事故
date: 2022-08-12 17:03:00
cover: https://oss.note.dreamccc.cn/note/images/posts/由XXL-admin日志序列化异常导致的事故/title.png?x-oss-process=style/blog_title
categories:
- [我永远爱学习]
tags:
- Java
- Debug
---

## 原因分析

1. 客户端回调日志序列化出现问题
2. 服务端收到callback后反序列化失败抛出500
3. 客户端收到500错误码无限重试
4. 由于配置错误多个客户端扫描了NFS上同一个日志目录
5. 多个客户端重试同一条日志，**错误被放大**
6. 服务端错误日志中打印了出入参，由于大量客户端重发callback，日志狂刷
7. xxl服务端日志滚动规则仅仅配置了日期滚动，未配置按大小滚动，**错误兜底失败**
8. 服务器用于存储日志的磁盘爆满，所有服务都爆炸啦！！（高兴

<!--more-->

## 关键堆栈信息，DEBUG详情之后复现时补充

```
com.xxl.job.core.thread.TriggerCallbackThread#start
com.xxl.job.core.thread.TriggerCallbackThread#retryFailCallbackFile
com.xxl.job.core.thread.TriggerCallbackThread#doCallback
com.xxl.job.core.thread.TriggerCallbackThread#doCallback[166]
```

### 反序列化失败的callback日志示例
- [xxl-job-callback-1660189083244.log](https://oss.note.dreamccc.cn/note/images/posts/由XXL-admin日志序列化异常导致的事故/xxl-job-callback-1660189083244.log)

### 服务器反序列化造成的错误日志
```log
Caused by: com.fasterxml.jackson.core.JsonParseException: Unrecognized character escape 'I' (code 73)
 at [Source: [{"logId":457389,"logDateTim":1659750000001,"executeResult":"**此处内容为上述callback.log内容**","content":null}}]; line: 1, column: 898]
        at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:1702)
        at com.fasterxml.jackson.core.base.ParserMinimalBase._reportError(ParserMinimalBase.java:558)
        at com.fasterxml.jackson.core.base.ParserMinimalBase._handleUnrecognizedCharacterEscape(ParserMinimalBase.java:535)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._decodeEscaped(ReaderBasedJsonParser.java:2536)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._finishString2(ReaderBasedJsonParser.java:2057)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser._finishString(ReaderBasedJsonParser.java:2030)
        at com.fasterxml.jackson.core.json.ReaderBasedJsonParser.getText(ReaderBasedJsonParser.java:276)
        at com.fasterxml.jackson.databind.deser.std.StringDeserializer.deserialize(StringDeserializer.java:36)
        at com.fasterxml.jackson.databind.deser.std.StringDeserializer.deserialize(StringDeserializer.java:11)
        at com.fasterxml.jackson.databind.deser.SettableBeanProperty.deserialize(SettableBeanProperty.java:504)
        at com.fasterxml.jackson.databind.deser.impl.MethodProperty.deserializeAndSet(MethodProperty.java:104)
        at com.fasterxml.jackson.databind.deser.BeanDeserializer.vanillaDeserialize(BeanDeserializer.java:276)
        ... 78 common frames omitted
```
