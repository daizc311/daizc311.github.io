---
title: Angular初学排坑日记（一）cnpm安装惨案
date: 2022-01-03 02:02:31
tags:
---

# Angular初学排坑日记（一）cnpm安装惨案

拿了台新电脑来玩，直接拿scoop配环境，配好了node环境和npm

- node:16.13.1
- npm:8.1.2

直连npm源会走代理，下载略慢，于是随手装个cnpm
```shell
PS C:\Users\Link\WebstormProjects> npm install -g cnpm
```
接着装angular-cli
```shell
PS C:\Users\Link\WebstormProjects> cnpm install -g @angular/cli
```
淘宝源装着就是快，不到10s装好了，接着就是跟着官方文档初始化项目
````shell
PS C:\Users\Link\WebstormProjects> ng new angulardemo
? Would you like to add Angular routing? Yes
? Which stylesheet format would you like to use? SCSS   [ https://sass-lang.com/documentation/syntax#scss
 ]
setTimeout is not defined
````
? 不知什么情况，报了个setTimeout的错误，本菜鸡不是很懂，遂Google一下，说是cnpm的问题（*不知为何会有这问题，有待查验）。总之先把这俩卸载掉。
```shell
PS C:\Users\Link\WebstormProjects> cnpm uninstall -g @angular/cli
PS C:\Users\Link\WebstormProjects> npm uninstall -g cnpm
PS C:\Users\Link\WebstormProjects> npm cache clean --force
```
然后只好用npm重新安装cli。但是npm安装的确是慢，而且个人喜欢用yarn做依赖管理，于是安装yarn，在龟速安装的同时，顺便找到个换源的工具。
```shell
PS C:\Users\Link\WebstormProjects> scoop install -g yarn
PS C:\Users\Link\WebstormProjects> yarn global add yrm
PS C:\Users\Link\WebstormProjects> yrm ls
* npm ---- https://registry.npmjs.org/
  cnpm --- http://r.cnpmjs.org/
  taobao - https://registry.npm.taobao.org/
  nj ----- https://registry.nodejitsu.com/
  rednpm - http://registry.mirror.cqupt.edu.cn/
  npmMirror  https://skimdb.npmjs.com/registry/
  edunpm - http://registry.enpmjs.org/
  yarn --- https://registry.yarnpkg.com
PS C:\Users\Link\WebstormProjects> yrm use taobao
```
成功解决问题
```shell
PS C:\Users\Link\WebstormProjects> yarn global add @angular/cli
PS C:\Users\Link\WebstormProjects> ng new angulardemo1
```
