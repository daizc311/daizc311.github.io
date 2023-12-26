---
title: 'Kubernetes安装笔记'
date: 2022/01/24 02:37:21
updated: 2022/01/24 02:37:21
comments: true
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/Kubernetes安装笔记/Kubernetes-Cluster.webp?x-oss-process=style/blog_title
categories:
- 我永远爱学习
tags:
- Kubernetes
---

本篇仅纯笔记，记录安装踩坑和一些细节，本次安装也仅仅只安装了3台机器，组建最小集群。

<!--more-->
### 踩的小坑

- 安装时要么全局走代理，要么走国内镜像(清华源/阿里源等)
- 容器运行时的cGroupDriver一定要与kubelet的配置一致，否则kubelet起不起来，会影响集群的init
- 集群初始化生成的加入集群的token只有24小时有效期，过期需要重新生成

```bash
# 关闭firewall
systemctl stop firewalld.service
systemctl disable firewalld.service

# 安装bash补全
yum install bash-completion

# 安装yum管理工具
sudo yum install -y yum-utils

# 添加EPEL源
curl  -s -o /etc/yum.repos.d/epel.repo http://mirrors.aliyun.com/repo/epel-7.repo

# 安装Docker
yum-config-manager     --add-repo     https://download.docker.com/linux/centos/docker-ce.repo
yum install docker-ce docker-ce-cli containerd.io
systemctl enable docker
systemctl start docker

# 添加K8sRepo - Google/Aliyun源
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo \
[kubernetes] \
name=Kubernetes \
# baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-\$basearch \
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/ 
enabled=1 \
gpgcheck=1 \
repo_gpgcheck=1 \
# gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg \
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg \
exclude=kubelet kubeadm kubectl \
EOF

# Set SELinux in permissive mode (effectively disabling it)
sudo setenforce 0
sudo sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config

# 安装kube工具集
yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes

# 启动kubelet
systemctl enable --now kubelet

# 预下载初始化集群需要的源 - Aliyun源
kubeadm config images pull --image-repository registry.aliyuncs.com/google_containers -v=7

# 打印用于kubeadm init的默认配置
kubeadm config print init-defaults > init-default.yml

# 如有必要，需要修改配置
vim init-default.yml

# #1 如果init-default.yml配置了cgroupDriver=systemd，需要将CRI的driver改为相同的值
# https://stackoverflow.com/questions/43794169/docker-change-cgroup-driver-to-systemd
cat <<EOF | tee /etc/docker/daemon.json \
{ \
  "exec-opts": ["native.cgroupdriver=systemd"] \
} \
EOF
systemctl restart docker

# 开始初始化集群
kubeadm init --config init-default.yml -v=7

# 配置kubectl
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config

# 配置kubectl自动完成
## UserProfile
echo 'source <(kubectl completion bash)' >>~/.bashrc
## SystemProfile
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null

```


### init-default.yml

用于集群初始化的配置文件，可以通过`kubeadm config print init-defaults`得到


```yml
apiVersion: kubeadm.k8s.io/v1beta3
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
---
# 配置cgroupDriver 推荐使用systemd (#1)
# https://kubernetes.io/docs/tasks/administer-cluster/kubeadm/configure-cgroup-driver/
kind: KubeletConfiguration
apiVersion: kubeadm.k8s.io/v1beta3
cgourpDriver: systemd
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta3
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controllerManager: {}
dns: {}
etcd:
  local:
    dataDir: /var/lib/etcd
# 更换了镜像仓库
# imageRepository: k8s.gcr.io
imageRepository: registry.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: 1.23.0
networking:
  dnsDomain: cluster.local
  serviceSubnet: 10.96.0.0/12
scheduler: {}
```
