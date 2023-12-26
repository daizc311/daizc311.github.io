---
title: 修复Ubuntu下ADB设备无权限的问题
date: 2022-09-05 09:00:00
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/修复Ubuntu下ADB设备无权限的问题/title.png?x-oss-process=style/blog_title
categories:
- 我永远爱学习
tags:
- Android
- Linux
---
# 修复ADB设备无权限的问题

接手同事的项目，在服务器(生产)上装了点依赖，重启了下系统，使用ADB连接的所有设备全都断连了。慌得一比，输出大概是下面这个样子。

```shell
data@data:~$ /home/data/.local/share/virtualenvs/script-schedule-exec-w4db_Qrl/lib/python3.10/site-packages/airtest/core/android/static/adb/linux/adb devices
List of devices attached
7DK7ZLVG99999999        device
8144d0f2        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
9486e0be        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
9584d066        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
9LIN4SSC99999999        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
I7VSE6DIRWLZ6T5H        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
JTK5T19909001733        device
a48ab864        device
bf7ddca0        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
e232d448        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
ec30a657        unauthorized
ef68a8c6        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
```

<!--more-->

查询资料后发现是udev权限配置的问题，进入`/etc/udev/rules.d/`目录后发现配置文件全部不见了（不知道为啥），于是去github上下载了个配置文件，塞到配置目录后，重启udev和ADB-server后恢复了大半。

剩下没恢复的是Oppo和Vivo手机，仍然提示无权限。经检查，是规则文件中缺少他们的配置，于是自己加上配置后再次重启服务，问题得到解决。




## 参考资料
- [snowdream/51-android](https://github.com/snowdream/51-android)
- [在硬件设备上运行应用](https://developer.android.com/studio/run/device)
- [获取原始设备制造商 (OEM) 驱动程序](https://developer.android.com/studio/run/oem-usb#Drivers)


**下载规则文件并重启**

```shell
sudo curl --create-dirs -L -o /etc/udev/rules.d/51-android.rules https://raw.githubusercontent.com/snowdream/51-android/master/51-android.rules
wget https://raw.githubusercontent.com/snowdream/51-android/master/51-android.rules
mv ./51-android.rules /etc/udev/rules.d/51-android.rules
sudo mv ./51-android.rules /etc/udev/rules.d/51-android.rules
sudo chmod a+r /etc/udev/rules.d/51-android.rules
sudo service udev restart
/home/data/.local/share/virtualenvs/script-schedule-exec-w4db_Qrl/lib/python3.10/site-packages/airtest/core/android/static/adb/linux/adb kill-server
/home/data/.local/share/virtualenvs/script-schedule-exec-w4db_Qrl/lib/python3.10/site-packages/airtest/core/android/static/adb/linux/adb devices
```

**新增规则**

```conf
--- /etc/udev/rules.d/51-android.rules 
SUBSYSTEM=="usb", ATTR{idVendor}=="12d1", ATTR{idProduct}=="107e",MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTR{idVendor}=="18d1", ATTR{idProduct}=="4ee7",MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTR{idVendor}=="2d95", ATTR{idProduct}=="6003",MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTR{idVendor}=="22d9", ATTR{idProduct}=="2774",MODE="0666", GROUP="plugdev"
```

**修好了**

```shell
data@data:~/workspace$ /home/data/.local/share/virtualenvs/script-schedule-exec-w4db_Qrl/lib/python3.10/site-packages/airtest/core/android/static/adb/linux/adb devices
List of devices attached
(这个不知道咋回事，id重复就算了，还在爆无权限，但是能用)↓↓↓↓
32615022        no permissions (user in plugdev group; are your udev rules wrong?); see [http://developer.android.com/tools/device.html]
32615022        device
7DK7ZLVG99999999        device
8144d0f2        device
9486e0be        device
9LIN4SSC99999999        device
I7VSE6DIRWLZ6T5H        device
JTK5T19909001733        device
a48ab864        device
bf7ddca0        device
e232d448        device
ec30a657        device
ef68a8c6        device
```