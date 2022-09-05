---

title: Kubernetes集群中拉取镜像走代理
date: 2022-09-05 09:00:00
cover: https://oss.note.dreamccc.cn/note/images/posts/Kubernetes集群中拉取镜像走代理/title.png?x-oss-process=style/blog_title
categories:
- [我永远爱学习]
tags:
- Kubernetes
  
---

**三种方式**
- systemd中配置docker进程环境变量
- docker守护线程用户代理配置
- 配置全局环境变量（大概没效果）

<!--more-->

## systemd中配置docker进程环境变量

### 方式一

- 一般来说直接改`/usr/lib/systemd/system/docker.service`就行，在其中的`[Service]`块中加上环境变量
    - `Environment="HTTP_PROXY=http://192.167.20.38:1080"`
    - `Environment="HTTPS_PROXY=http://192.167.20.38:1080"`
- 最后再重启docker就行
  
```shell
[root@vmw253 ~]# systemctl daemon-reload
[root@vmw253 ~]# systemctl restart docker
```

### 方式二 （稍微优雅一点）

- vim /etc/systemd/system/docker.service.d/http-proxy.conf

```conf
[Service]
Environment="HTTP_PROXY=http://192.167.20.38:1080"
Environment="HTTPS_PROXY=http://192.167.20.38:1080"
Environment="NO_PROXY=localhost,127.0.0.1,*.myhuaweicloud.com,192.167.0.0/16,192.168.0.0/16"
```

- 然后与方式一一样重启docker,两种方式没啥区别都是一样的


## 重启好之后随便测试下
```shell
[root@vmw253 ~]# docker pull k8s.gcr.io/pause:3.0
3.0: Pulling from pause
a3ed95caeb02: Pull complete 
f11233434377: Pull complete 
Digest: sha256:0d093c962a6c2dd8bb8727b661e2b5f13e9df884af9945b4cc7088d9350cd3ee
Status: Downloaded newer image for k8s.gcr.io/pause:3.0
k8s.gcr.io/pause:3.0

```