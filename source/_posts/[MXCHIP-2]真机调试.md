---
title: "[MXCHIP-2]真机调试"
date: 2019/5/21 11:11:53
categories: 
    - [我永远爱学习]
tags: 
    - 单片机
    - IOT
    - C
cover: https://oss.note.dreamccc.cn/note/images/posts/[MXCHIP-2]真机调试/title.jpg?x-oss-process=style/blog_title
---

离上一篇文章发布已经时隔很久了，整理了一下最近学习的操作做个记录免得忘掉了。

<!--more-->

### 真机调试

#### 本次的主角 X讯TC1插排

![拆机后的TC1](/source/images/posts/[MXCHIP-2]真机调试/拆机后的TC1.jpg)
<!-- ![拆机后的TC1]() -->

通过仔细观察可以发现，在PCB上预留了2组(8个)触点，一组上面标有RX/TX字样，那肯定就是UART接口了。

![UART接口定义](/source/images/posts/[MXCHIP-2]真机调试/UART接口定义.jpg)
<!-- ![UART接口定义](./UART接口定义.jpg) -->

另一组标有CLK、BIO字样，应该是用于刷机的SWD接口。

![SWD接口定义](/source/images/posts/[MXCHIP-2]真机调试/SWD接口定义.jpg)
<!-- ![SWD接口定义](./SWD接口定义.jpg) -->

- [引用的资料](https://iot-security.wiki/hardware-security/debug/jtag.html)

#### 模块文档

从官网翻到了模块的参数信息
[DS0021CN_EMW3031_V1.4](/source/images/posts/[MXCHIP-2]真机调试/DS0021CN_EMW3031_V1.4.pdf)
