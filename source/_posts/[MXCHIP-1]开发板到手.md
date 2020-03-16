---
title: "[MXCHIP-1]开发板到手"
date: 2020/3/10 10:16:56
categories: 
    - [<del>我永远爱学习</del>]
tags: 
    - 单片机
    - IOT
    - C
cover: /2020/03/10/%5BMXCHIP-1%5D开发板到手/title.jpg
coverWidth: 1200
coverHeight: 750
---

这是一个从没玩过单片机开发的菜鸡闲暇时间瞎折腾的故事

起因是18年斐讯出事的时候炒了一波底，买回来一堆斐讯的设备，以tc1插线盒居多，但是随着斐讯线上服务的关闭，这些原本的智能插线盒变得不智能了，正好网上有网友开发了一波TC1的固件，只是不开源还需要邀请码，遂作罢，但是萌生出了自己折腾一下的念头。这次因为2019-NoCv的关系被关在家里，便开始折腾起了这些玩意儿。

<!--more-->


 #### 作案工具

{% asset_img 1.jpg [作案工具] %}

 - 庆科EMW3031开发板
 - 庆科EMW3165开发板
 - 庆科MICOKIT-EXT上层扩展板
 - USB转TTL工具
 - JLINK仿真器

 PS：3165和扩展板是在闲鱼直接50包邮收的，感觉赚得飞起。usb-ttl一开始不知道需要这个，反复看文档才发现差这玩意儿，5块的东西发了20的顺丰才送到。

 #### 开发生态

 以前从来没玩过如此高大上的玩意儿，这次就算拿到手也是一脸蒙蔽，不知从何下手，入手自闲鱼也没有任何资料，只好先翻一翻庆科的官网看看。
 
 ###### 收集的部分有用的链接

 - [庆科官网](https://www.mxchip.com/)
 - [开发者中心](http://developer.mxchip.com/) 
 - [官方论坛](http://mico.io/)
 - [开发者支持社区](http://bbs.mxchip.com/)

 #### 启动到bootloader

 ##### 物理接线

{% asset_img 4.jpg [物理接线] %}

```
第一条接线 [用户串口] 对应驱动是USB-TTL的驱动 USB-SERIAL CH340
  PC USB接口 =======> USB-TTL =======> 开发板UART接口
                        |—— GND            |——  GND         
                        |—— RXD            |——  TXD    
                        |—— TXD            |——  RXD    
                        |—— 5V0            |——  5V0    

第二条接线 [调试串口] 对应驱动是开发板的驱动 USB Serial Port
  PC USB接口 =======> 开发板microUsb接口 
 ```

PS：这里也被卡了不少时间，原因就是不知道RXD和TXD需要反着接。官网文档里没有任何说明，我在USB-TTL的淘宝页面里找到的资料才提到这一点

 ##### 驱动管理

首先，我完全是个门外汉，我猜测这两驱动的作用可能是一样的，以前的外围设备都是通过COM口和PC相连接的，现在的电脑都没有COM口，于是需要一个将COM口转为USB口的设备。
USB-TTL应该就是其中之一，所以才需要安装一个驱动来将USB口映射为虚拟的COM口供软件连接。至于开发板为何可以用microUsb直连电脑我猜测是开发板中内置了芯片来转换，因为我看了新平台`MXKIT`的说明文档，新平台中只需要一根线就可以同时接上两个接口，所以我做出了这个猜测。

{% asset_img 3.jpg [驱动管理] %}


 ##### 软件连接

[官网教程](http://developer.mxchip.com/handbooks/109)


1. 先将开发版上的`MODULE SELECT`拨动到BootLoader启动模式，即BOOT=ON,STATUS=OFF。

{% asset_img 5.jpg [软件连接] %}

2. 在其[官网新文档的角落](https://mxchip.yuque.com/books/share/8ac5e519-671d-4444-a93d-20e0aadfc793/ombbz2)翻到了这个表格，根据表格内容在SecureCRT中建立连接

| 型号  | Bootloader</br>MFG产测信息 | AT指令及透传      | 正常工作log</br>CLI调试命令 |
| ------- | ---------------------- | ---------------------- | ----------------------- |
| EMW3031 | Pin 9,Pin10,921600bps   | Pin 9,Pin10,115200bps | Pin21,Pin22,115200bps    |
| EMW3060 | Pin 9,Pin10,921600bps   | Pin 9,Pin10,115200bps | Pin21,Pin22,115200bps    |
| EMW3080 | Pin 9,Pin10,921600bps   | Pin 9,Pin10,115200bps | Pin21,Pin22,115200bps    |
| EMW3162 | Pin22,Pin23,921600bps  | Pin22,Pin23,115200bps  | Pin14,Pin4,115200bps     |
| EMW3165 | Pin29,Pin30,921600bps  | Pin29,Pin30,115200bps  | Pin8,Pin12,115200bps     |
| EMW3166 | Pin29,Pin30,921600bps  | Pin29,Pin30,115200bps  | Pin8,Pin12,115200bps     |
| EMW3239 | Pin29,Pin30,921600bps  | Pin29,Pin30,115200bps  | Pin8,Pin12,115200bps     |

3. 建立好连接后点一下开发板上的restart重启一下就能能看到日志了,如下图左边是用户日志，来自于microUsb(COM4)，右边是调试日志，来自于usb-ttl(COM9)，平时的debug时的日志也都输出到这里。

 {% asset_img 6.jpg [Bootloader日志] %}