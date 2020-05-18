---
title: '[WSL2]子系统ubuntu安装jdk8'
date: 2019/1/4 15:42:01
updated: 2020/2/20 15:41:36
comments: true
categories: 
    - [搞点骚操作]
tags: 
    - linux
---

这是个大坑 平时装个JDK这么简单的事 在WSL上问题还真不少
<!--more-->

### 安装环境
```
适用于 Linux 的 Windows 子系统:
Ubuntu-18.04 (默认)
```



### 

以前都是用的centos,直接`yum localinstall ./jdk-???-linux-x64.rpm`就可以安装本地的RPM包了。 
但是现在使用的Ubuntu，只能安装DEB包，查询了一下有三种安装方式：

- RPM包转制为DEB包
- 使用开箱即用的tar.gz并配置环境变量
- 使用ppa源安装
 
### 开始安装

很明显使用ppa源安装更稳定不容易遇到问题，参考了此文章 
[Install Oracle Java 8 (JDK8 and JRE8) in Ubuntu or Linux Mint](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)

```
sudo add-apt-repository ppa:webupd8team/java    //添加PPA源
sudo apt-get update                             //更新本地包
sudo apt-get install oracle-java8-installer     //安装JDK

// 很遗憾报错了

root@DESKTOP-UFOJIL7:/wsl_share# apt-get install oracle-java8-installer
Reading package lists... Done
Building dependency tree
Reading state information... Done
Package oracle-java8-installer is not available, but is referred to by another package.
This may mean that the package is missing, has been obsoleted, or
is only available from another source

E: Package 'oracle-java8-installer' has no installation candidate
```

### 

在这里找到了答案
[Oracle-Java8-Installer: No installation candidate](https://askubuntu.com/questions/790671/oracle-java8-installer-no-installation-candidate)

&gt; NOTE: This answer no longer works, as the WebUpd8 PPA has been deprecated since Oracle has changed licensing and access restrictions to the Oracle Java codebase. Details at http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html

翻译一下

&gt;注意：此答案不再有效，因为WebUpd8 PPA已被弃用，因为Oracle已将许可和访问限制更改为Oracle Java代码库。 有关详细信息， 请访问http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html

既然如此，那没啥办法只能手动安装一个了

### 手动配置

首先下载一个`jdk-8u221-linux-x64.tar.gz`,拷贝到wsl_share中

```
root@DESKTOP-UFOJIL7:/wsl_share# tar -zxvf ./jdk-8u221-linux-x64.tar.gz      //首先解压
root@DESKTOP-UFOJIL7:/wsl_share# vim ~/.bashrc                              //配置环境变量
```

```
// bashrc中追加以下内容
export JAVA_HOME=/app/jdk1.8.0_221
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH
```

```
root@DESKTOP-UFOJIL7:/wsl_share# source ~/.bashrc    //重新加载环境变量

root@DESKTOP-UFOJIL7:/wsl_share# java -version
java version "1.8.0_221"
Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)


```
