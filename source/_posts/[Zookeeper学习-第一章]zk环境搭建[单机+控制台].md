---
title: '[Zookeeper学习-第一章]zk环境搭建[单机+控制台]'
date: 2019/7/1 15:53:24
updated: 2020/2/20 15:41:36
comments: true
categories: 
    - [我永远爱学习]
tags: 
    - java
    - zookeeper
---

系统环境为 Centos ，由于项目需要，准备搭建 zookeeper-3.5.5 + 用于方便展示节点的zookeeperAdmin用作公司开发之用
<!--more-->

## zookeeper-3.5.5

### 下载

前往开源项目[下载页面](http://zookeeper.apache.org/releases.html#download)根据需要下载对应的版本（注意要下载文件名后面带"-bin"的包）<br/>
[懒人链接](https://archive.apache.org/dist/zookeeper/)

```bash
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# tar -zxvf ./apache-zookeeper-3.5.5-bin.tar.gz
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# cd ./apache-zookeeper-3.5.5-bin.tar.gz
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# ll
total 44
drwxr-xr-x 2 2002 2002  4096 Apr  9 19:13 bin
drwxr-xr-x 2 2002 2002  4096 Sep 20 14:00 conf
drwxr-xr-x 5 2002 2002  4096 May  3 20:07 docs
drwxr-xr-x 2 root root  4096 Sep 20 13:40 lib
-rw-r--r-- 1 2002 2002 11358 Feb 15  2019 LICENSE.txt
drwxr-xr-x 2 root root  4096 Sep 20 13:43 logs
-rw-r--r-- 1 2002 2002   432 Apr  9 19:13 NOTICE.txt
-rw-r--r-- 1 2002 2002  1560 May  3 19:41 README.md
-rw-r--r-- 1 2002 2002  1347 Apr  2 21:05 README_packaging.txt
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# ll
[root@iZuf6imeqt5e7fo9jw7918Z apache-zookeeper-3.5.5-bin]# cd conf
[root@iZuf6imeqt5e7fo9jw7918Z conf]# ll
total 16
-rw-r--r-- 1 2002 2002  535 Feb 15  2019 configuration.xsl
-rw-r--r-- 1 2002 2002 2712 Apr  2 21:05 log4j.properties
-rw-r--r-- 1 2002 2002  922 Feb 15  2019 zoo_sample.cfg
[root@iZuf6imeqt5e7fo9jw7918Z conf]# cp ./zoo_sample.cfg ./zoo.cfg
[root@iZuf6imeqt5e7fo9jw7918Z conf]# cd ../bin/
[root@iZuf6imeqt5e7fo9jw7918Z conf]# 

```

### 需要留意的
如果在这里直接启动的话可能会启动不成功，因为apache-zookeeper默认监听了8080端口作为自己的`zookeeper-server`的服务端。最好是自己在./conf/zoo.cfg中配置一下`admin.serverPort=[自己随便改个端口]`，对的，配置文件里默认是没有这行的，这个比较坑。

### 启动
```bash
[root@iZuf6imeqt5e7fo9jw7918Z bin]# ./zkServer.sh start
/usr/bin/java
ZooKeeper JMX enabled by default
Using config: /app/zookeeper/apache-zookeeper-3.5.5-bin/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
[root@iZuf6imeqt5e7fo9jw7918Z bin]# ./zkServer.sh start
/usr/bin/java
ZooKeeper JMX enabled by default
Using config: /app/zookeeper/apache-zookeeper-3.5.5-bin/bin/../conf/zoo.cfg
Starting zookeeper ... already running as process 29414.
[root@iZuf6imeqt5e7fo9jw7918Z bin]# ./zkServer.sh status
/usr/bin/java
ZooKeeper JMX enabled by default
Using config: /app/zookeeper/apache-zookeeper-3.5.5-bin/bin/../conf/zoo.cfg
Client port found: 2181. Client address: localhost.
Mode: standalone
```



##  zookeeper-admin
这是一个github上的小[项目](https://github.com/Ahoo-Wang/ZooKeeper-Admin)<br/>
功能不多，刚刚够用就行，不整那些花里胡哨的，支持直接使用docker构建，非常方便

```bash
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# docker run --name zookeeper-admin -p 80:2182 docker.io/ahoowang/zookeeper.admin
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# docker container ls
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                  NAMES
56bf5522f4fe        4232addbc345        "dotnet ZooKeeper...."   About an hour ago   Up 2 minutes        0.0.0.0:2182-&gt;80/tcp   zookeeper-admin

```
![zookeeper-admin](/source/images/posts/[Zookeeper学习-第一章]zk环境搭建[单机+控制台]/zookeeper-config.png)

界面看上去还不错。

然而好景不长，跑了一阵发现无法访问了。
```bash
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# docker container ls
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```
??? 怎么停止了，看看日志怎么说
```
[root@iZuf6imeqt5e7fo9jw7918Z zookeeper]# docker logs 56bf5522f4fe
Hosting environment: Production
Content root path: /app
Now listening on: http://+:80
Application started. Press Ctrl+C to shut down.
Application is shutting down...
Hosting environment: Production
Content root path: /app
Now listening on: http://+:80
Application started. Press Ctrl+C to shut down.
Unhandled Exception: System.InvalidOperationException: Collection was modified; enumeration operation may not execute.
   at System.ThrowHelper.ThrowInvalidOperationException(ExceptionResource resource)
   at System.Collections.Generic.Dictionary`2.ValueCollection.Enumerator.MoveNext()
   at ZooKeeper.Admin.ZooKeeperManager.<dispose>d__9.MoveNext() in E:\ZooKeeper-Admin\ZooKeeper.Admin\ZooKeeperManager.cs:line 55
--- End of stack trace from previous location where exception was thrown ---
   at System.Runtime.ExceptionServices.ExceptionDispatchInfo.Throw()
   at System.Threading.ExecutionContext.Run(ExecutionContext executionContext, ContextCallback callback, Object state)
   at System.Threading.QueueUserWorkItemCallback.System.Threading.IThreadPoolWorkItem.ExecuteWorkItem()
   at System.Threading.ThreadPoolWorkQueue.Dispatch()

```



