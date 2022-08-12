---
title: '为Oracle实例挂载块存储'
date: 2022/08/12 16:43:10
cover: https://oss.note.dreamccc.cn/note/images/posts/为Oracle实例挂载块存储/title.png?x-oss-process=style/blog_title
categories:
- [我永远爱学习]

tags:
- Cloud
---

所谓块存储说人话就是云硬盘，购买块存储简单理解就是买了块已经做好scsi的硬盘，买块硬盘之后插到服务器上就能用了。
大概流程就是下面这种：
```
购买块存储 -> 附加到实例 -> 分区 -> 挂载  
```
如果需要多个实例共享一个空间的话则有多种方式
- 将块存储同时挂载到多个实例，再将存储中分区的文件系统格式化为共享型存储系统
- 将块存储挂载到单个实例上，再将使用NFS共享块存储

<!--more-->

## 购买块存储

准备好钱直接买就是了，免费账户有50G的免费空间，做做实验啥的也够了。

## 附加到实例

买好了块存储，点进详情就可以附加到实例。这里需要注意下，如果多个实例想挂载同一个块存储，那需要选择`读/写 - 可共享选项`。
![附加到实例](cover: https://oss.note.dreamccc.cn/note/images/posts/为Oracle实例挂载块存储/attach2instance.png)

## 参考资料
- [xfs-vs-ext4](https://www.partitionwizard.com/partitionmanager/xfs-vs-ext4.html)
- [service-iscsi](https://ubuntu.com/server/docs/service-iscsi)
- [choosing-between-network-and-shared-storage-file-systems_assembly_overview-of-available-file-systems](https://access.redhat.com/documentation/zh-cn/red_hat_enterprise_linux/8/html/managing_file_systems/choosing-between-network-and-shared-storage-file-systems_assembly_overview-of-available-file-systems)
- [ch-ext4](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/storage_administration_guide/ch-ext4)
- [Ramble_about_distributed_storage_schemes.pdf](https://www.ibm.com/it-infrastructure/cn-zh/assets/pdf/storage/Ramble_about_distributed_storage_schemes.pdf)
