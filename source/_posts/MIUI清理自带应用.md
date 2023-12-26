---
title: MIUI清理自带应用
date: 2022-02-01 17:44:26
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/MIUI清理自带应用/laji-miui.jpg?x-oss-process=style/blog_title

categories:
- 就是一些笔记
tags:
- MIUI
- Android
- Shell
---
## 系统版本

- Redmi K30S Ultra
- MIUI 12.5.5.RJDCNXM 稳定版

<!--more-->
## 注意的坑

- 禁用/删除完后需要重启手机看看是否正常，如果出现卡mi，桌面加载不出来等情况，需要立即进入recovery中连接adb修复。
- 小米浏览器只禁用会出问题，比如通过QQ等打开链接跳转时会无响应，建议直接卸载。
- 通过adb删除的应用再进行大版本更新时会被重新安装，届时需要再次操作。

## 删除/禁用MIUI系统自带应用

1. 手机连接电脑，同时开发者模式中打开adb调试选项
2. 电脑侧安装ADB
    ```shell
    sudo scoop install adb -g 
    ```
3. 电脑确认手机adb连接
    ```shell
    adb devices -l
    ```
   此时手机会弹出adb连接确认弹窗
4. (可选)打印手机程序包列表
    ```shell
    adb shell pm list packages > miuipackage.txt
    ```
5. 删除/禁用自带应用
    ```shell
    # 进入交互式adb-shell
    adb shell

    # 禁用系统广告
    apollo:/ $ pm diable-user com.miui.systemAdSolution
    # 禁用行为分析
    apollo:/ $ pm disable-user com.miui.analytics
    # 禁用游戏中心服务
    apollo:/ $ pm disable-user com.xiaomi.gamecenter.sdk.service 
    # 删除小米浏览器
    apollo:/ $ pm uninstall --user 0 com.android.browser

    # 重启手机测试是否正常
    apollo:/ $ reboot
    ```

## 参考资料

- [MIUI 强制使用自带浏览器](https://www.v2ex.com/t/640913)
- [使用ADB不root删除小米MIUI系统自带应用](https://fengooge.blogspot.com/2019/03/taking-ADB-to-uninstall-system-applications-in-MIUI-without-root.html)
