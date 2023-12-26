---
title: Linux下的简单网络管理
date: 2022-08-25 17:28:52
categories:
- 我永远爱学习
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/Linux下的简单网络管理/title.png?x-oss-process=style/blog_title
tags:
- Linux

---

## 使用Netplan管理网络配置

## 相关资料

- [Netplan文档地址](https://netplan.io/reference)
- Netplan配置地址: `/etc/netplan/xxxx.yaml`
- [NetworkManager文档地址](https://networkmanager.dev/docs/)
- NetworkManager配置地址: `/etc/NetworkManager/NetworkManager.conf`

<!--more-->

## 步骤

1. 修改配置
2. 应用网络策略`sudo netlpan apply`
    ```yaml
    # This is the network config written by 'subiquity'
    network:
    version: 2
    # 可选值 NetworkManager | networkd
    renderer: NetworkManager
    ethernets:
        eno1:
        addresses:
        - 192.167.20.211/24
        gateway4: 192.167.20.1
        nameservers:
            addresses: 
            - 192.167.20.1
            - 114.114.114.114
            - 1.1.1.1
            search: []
        eno2:
        dhcp4: true
        
    ```
3. 使用`sudo nmtui`微调网络，如果nmtui中看不到接口，则检查配置后使用`sudo systemctl reload NetworkManager`重启服务即可



## 静态配置(旧版Ubuntu)
- 静态ip配置: `/etc/network/interfaces`
- 静态dns配置: `/etc/resolv.conf`