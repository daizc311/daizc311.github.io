---
title: 清理linux磁盘空间
date: 2022-01-10 17:22:24
cover: https://oss.note.dreamccc.cn/note/images/posts/清理linux磁盘空间/title.png?x-oss-process=style/blog_title
categories:
- [我永远爱学习]
tags:
- Linux
---

<!--more-->

- 首先查看是哪块挂载的磁盘满了

```shell
df -h

daizc@DAIZC:~/.local/share/Trash/files$ df -h
文件系统        容量  已用  可用 已用% 挂载点
udev             12G     0   12G    0% /dev
tmpfs           2.4G  189M  2.2G    8% /run
/dev/sdb4        92G   69G   18G   80% /
tmpfs            12G  290M   12G    3% /dev/shm
tmpfs           5.0M  4.0K  5.0M    1% /run/lock
tmpfs           4.0M     0  4.0M    0% /sys/fs/cgroup
/dev/loop0      128K  128K     0  100% /snap/bare/5
/dev/loop1      255M  255M     0  100% /snap/gnome-3-38-2004/106
/dev/loop2       62M   62M     0  100% /snap/core20/1518
/dev/loop4       92M   92M     0  100% /snap/gtk-common-themes/1535
/dev/loop3      401M  401M     0  100% /snap/gnome-3-38-2004/112
/dev/loop5       82M   82M     0  100% /snap/gtk-common-themes/1534
/dev/sdb2       9.8G   37M  9.3G    1% /recovery
tmpfs           2.4G  136K  2.4G    1% /run/user/1000
/dev/sda2       224G  168G   57G   75% /media/daizc/软件
/dev/loop6       62M   62M     0  100% /snap/core20/1581
/dev/loop7       62M   62M     0  100% /snap/core20/1587
/dev/loop8      584M  584M     0  100% /data/uengine/data/rootfs
uengine-fuse     92G   69G   18G   80% /data/uengine/安卓应用文件
```

- 因为整个磁盘都挂在根目录下，所以只知道根目录满了，不知道具体是满的是什么
- 这里需要一层一层向下找

```shell
daizc@kl-All-Series:~$ sudo du -h --max-depth=1 |sort -hr  |head -n 10
2.1G    .
378M    ./service
223M    ./logs
187M    ./.vscode-server
140M    ./seata
105M    ./docker-image
13M     ./.kube
12M     ./sandbox
8.5M    ./.local
7.7M    ./.cache
```

