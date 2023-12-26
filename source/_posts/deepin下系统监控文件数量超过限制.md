---
title: 文件观察者数量超过系统限制
date: 2022-06-16 10:08:12
categories:
- 就是一些笔记

tags:
- Linux

---

### 症状1
在系统文件管理器中新建文件时不会自动刷新。

### 症状2
启动Angular项目报了如下错误：

```log
Watchpack Error (watcher): Error: ENOSPC: System limit for number of file watchers reached, watch '/home/daizc/IdeaProjects/beam/beam-frontend/src/main/angular/node_modules/@webcomponents/webcomponentsjs'
Watchpack Error (watcher): Error: ENOSPC: System limit for number of file watchers reached, watch '/home/daizc/IdeaProjects/beam/beam-frontend/src/main/angular/node_modules/@webcomponents'
Watchpack Error (watcher): Error: ENOSPC: System limit for number of file watchers reached, watch '/home/daizc/IdeaProjects/beam/beam-frontend/src/main/angular/node_modules/@types/trusted-types'
Watchpack Error (watcher): Error: ENOSPC: System limit for number of file watchers reached, watch '/home/daizc/IdeaProjects/beam/beam-frontend/src/main/angular/node_modules/@types'
```

<!--more-->

### 解决方案:A
[React Native Error: ENOSPC: System limit for number of file watchers reached](https://stackoverflow.com/questions/55763428/react-native-error-enospc-system-limit-for-number-of-file-watchers-reached)

经验证，重启后会失效

```shell
sudo sysctl fs.inotify.max_user_watches=524288  # 我自己设置的是131072
sudo sysctl -p
```

### 解决方案:B
[Inotify 监视限制 (Linux)](https://youtrack.jetbrains.com/articles/IDEA-A-2/Inotify-Watches-Limit-Linux)

1. 将以下行添加到/etc/sysctl.conf文件或目录下的新*.conf文件（例如idea.conf）/etc/sysctl.d/:
    ```fs.inotify.max_user_watches = 524288```
2. 然后运行此命令以应用更改：
    ```sudo sysctl -p --system```


### 系统环境: 

- OS: Deepin 20.6 apricot
- Kernel: x86_64 Linux 5.10.101-amd64-desktop
