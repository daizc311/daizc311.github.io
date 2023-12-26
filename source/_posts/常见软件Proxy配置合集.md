---

title: 常见软件Proxy配置合集
date: 2022-09-08 10:50:00
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/clash.png?x-oss-process=style/blog_title
categories:
- 就是一些笔记
tags:
- Linux
  
---

 - System
 - APT
 - Docker

<!--more-->

## System
```
export HTTP_PROXY=http://localhost:1080
export HTTPS_PROXY=http://localhost:1080
```

## APT
`/etc/apt/apt.conf.d/proxy.conf`
```conf
 Acquire::http::Proxy "http://localhost:1080";
 Acquire::https::Proxy "http://localhost:1080";
```

## Docker
`/etc/systemd/system/docker.service.d/http-proxy.conf`
```conf
[Service]
Environment="HTTP_PROXY=http://192.167.20.38:1080"
Environment="HTTPS_PROXY=http://192.167.20.38:1080"
Environment="NO_PROXY=localhost,127.0.0.1,192.168.0.0/16"
```