---
title: 在K8s中部署projector-idea-u
date: 2022-06-06 11:31:17
cover: https://oss.note.dreamccc.cn/note/images/posts/在K8s中部署projector-idea-u/Snipaste_2022-06-06_09-30-38.jpg

categories:
- [我永远爱学习]
  
tags:
- 容器化

---
<!--more-->

## 编写部署文件
```yaml
# 提供NodePort供外部访问
apiVersion: v1
kind: Service
metadata:
  name: idea-u-daizc-nodeport
  namespace: default
  labels:
    app.kubernetes.io/name: idea-u-daizc
spec:
  type: NodePort
  ports:
    - port: 8887
      targetPort: 8887
      nodePort: 31887
  selector:
    app.kubernetes.io/name: idea-u-daizc
---
# 提供ClusterIP供内部解析
apiVersion: v1
kind: Service
metadata:
  name: idea-u-daizc
  namespace: default
  labels:
    app.kubernetes.io/name: idea-u-daizc
spec:
  type: ClusterIP
  ports:
    - port: 8887
      protocol: TCP
      name: http
  selector:
    app.kubernetes.io/name: idea-u-daizc
---
# 使用有状态副本集部署
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: idea-u-daizc
  namespace: default
  labels:
    app.kubernetes.io/name: idea-u-daizc
spec:
  selector:
    matchLabels:
      app.kubernetes.io/name: idea-u-daizc
  serviceName: "idea-u-daizc"
  replicas: 1 # 默认值是 1
  minReadySeconds: 0 # 默认值是 0
  template:
    metadata:
      labels:
        app.kubernetes.io/name: idea-u-daizc # 必须匹配 .spec.selector.matchLabels
    spec:
      terminationGracePeriodSeconds: 60
      containers:
      - name: idea-u-daizc
        image: jetbrains/projector-idea-u:2021.3-projector-v1.8.1
        ports:
        - containerPort: 8887
          protocol: TCP
          name: http
        volumeMounts:
        - mountPath: /home/projector-user
          name:  idea-u-daizc-volume
      volumes:
        - name: idea-u-daizc-volume
          persistentVolumeClaim:
            claimName: idea-u-daizc-pvc
---
# 使用PVC声明需要的持久化存储空间
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: idea-u-daizc-pvc
spec:
  storageClassName: idea-u-daizc-pv-storage-class-name
  # 需要与PVC的对应，否则无法成功申领下面的PV
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 10Gi
---
# 为PVC手动创建PV卷
apiVersion: v1
kind: PersistentVolume
metadata:
  name: idea-u-daizc-pv
spec:
  # 访问模式 
  # https://kubernetes.io/docs/concepts/storage/persistent-volumes/#access-modes
  # ReadWriteOnce     卷可以被一个节点以读写方式挂载。 ReadWriteOnce 访问模式也允许运行在同一节点上的多个 Pod 访问卷。
  # ReadOnlyMany      卷可以被多个节点以只读方式挂载。
  # ReadWriteMany     卷可以被多个节点以读写方式挂载。
  # ReadWriteOncePod  卷可以被单个 Pod 以读写方式挂载。 如果你想确保整个集群中只有一个 Pod 可以读取或写入该 PVC， 请使用  # ReadWriteOncePod 访问模式。这只支持 CSI 卷以及需要 Kubernetes 1.22 以上版本。
  accessModes:
  - ReadWriteMany
  capacity:
    storage: 10Gi
  # 使用NFS作为PV提供者
  nfs:
    # 这个path需要在NFS处配置
    path: /opt/nfs/idea-u-daizc-pv
    server: 192.168.21.24
  storageClassName: idea-u-daizc-pv-storage-class-name
  # PV回收策略
  # https://kubernetes.io/zh/docs/concepts/storage/persistent-volumes/#reclaim-policy
  # Retain    手动回收
  # Recycle   基本擦除 (rm -rf /thevolume/*)
  # Delete    诸如 AWS EBS、GCE PD、Azure Disk 或 OpenStack Cinder 卷这类关联存储资产也被删除
  persistentVolumeReclaimPolicy: Retain
  volumeMode: Filesystem
```

## 修改NFS配置
 - 在部署PV时写死的nfs路径需要真实存在，如果不存在需要新建
 - 在`/etc/exports`中新增导出项
    ```config
      # 集群地址放行   
      /opt/nfs/idea-u-daizc-pv        192.168.21.0/24(rw,sync,all_squash)
      # 本地地址放行
      /opt/nfs/idea-u-daizc-pv        192.167.20.38/32(rw,sync,all_squash)
    ```
    需要注意NFS用户权限映射相关配置（使用`man export`查看）
    - root_squash，当NFS客户端以root用户身份访问时，映射为NFS服务器的nfsnobody用户
    - no_root_squash，当NFS客户端以root身份访问时，映射为NFS服务器的root用户，也就是要为超级用户保留权限。这个选项会留下严重的安全隐患，一般不建议采用
    - all_squash，无论NFS客户端以哪种用户身份访问，均映射为NFS服务器的nfsnobody用户
其中默认值是root_squash，即当客户端以root用户的身份访问NFS共享时，在服务器端会自动被映射为匿名账号nfsnobody
 - 使用`exportfs -rav`命令重载NFS服务。
 - *如果挂载成功后pod内报权限异常就把整个nfs的挂载目录权限改成777

## 开始部署
 - 使用`kubectl apply -f xxx`部署上面的yml
 - 观察PV、PVC是否为绑定状态(STATUS为Bound)
    ```log
    daizc@KAILIN-DAIZC:/mnt/d/deployment$ kubectl get pvc
    NAME               STATUS   VOLUME            CAPACITY   ACCESS MODES   STORAGECLASS                         AGE
    idea-u-daizc-pvc   Bound    idea-u-daizc-pv   10Gi       RWX            idea-u-daizc-pv-storage-class-name   3d19h
    daizc@KAILIN-DAIZC:/mnt/d/deployment$ kubectl -n ops get pv
    NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                      STORAGECLASS                         REASON   AGE
    idea-u-daizc-pv                            10Gi       RWX            Retain           Bound    default/idea-u-daizc-pvc   idea-u-daizc-pv-storage-class-name            3d19h
    ```
  - 观察StatefulSet和Pod是否启动成功
    ```log
    daizc@KAILIN-DAIZC:/mnt/d/deployment$ kubectl describe statefulsets.apps idea-u-daizc
    Name:               idea-u-daizc
    Namespace:          default
    CreationTimestamp:  Thu, 02 Jun 2022 12:59:00 +0800
    Selector:           app.kubernetes.io/name=idea-u-daizc
    Labels:             app.kubernetes.io/name=idea-u-daizc
    Annotations:        <none>
    Replicas:           1 desired | 1 total
    Update Strategy:    RollingUpdate
      Partition:        0
    Pods Status:        1 Running / 0 Waiting / 0 Succeeded / 0 Failed
    Pod Template:
      Labels:  app.kubernetes.io/name=idea-u-daizc
      Containers:
      idea-u-daizc:
        Image:        jetbrains/projector-idea-u:2021.3-projector-v1.8.1
        Port:         8887/TCP
        Host Port:    0/TCP
        Environment:  <none>
        Mounts:
          /home/projector-user from idea-u-daizc-volume (rw)
      Volumes:
      idea-u-daizc-volume:
        Type:       PersistentVolumeClaim (a reference to a PersistentVolumeClaim in the same namespace)
        ClaimName:  idea-u-daizc-pvc
        ReadOnly:   false
    Volume Claims:  <none>
    Events:         <none>

    daizc@KAILIN-DAIZC:/mnt/d/deployment$ kubectl get pod idea-u-daizc-0  -oyaml
    apiVersion: v1
    kind: Pod
    metadata:
      annotations:
        cni.projectcalico.org/containerID: bbfe0e4675cece177bd362cd3955995b5b0bc3c4ef55939fbbf845b10fa29505
        cni.projectcalico.org/podIP: 10.233.97.107/32
        cni.projectcalico.org/podIPs: 10.233.97.107/32
      creationTimestamp: "2022-06-02T06:38:10Z"
      generateName: idea-u-daizc-
      labels:
        app.kubernetes.io/name: idea-u-daizc
        controller-revision-hash: idea-u-daizc-5578c99bf7
        statefulset.kubernetes.io/pod-name: idea-u-daizc-0
      name: idea-u-daizc-0
      namespace: default
      ownerReferences:
      - apiVersion: apps/v1
        blockOwnerDeletion: true
        controller: true
        kind: StatefulSet
        name: idea-u-daizc
        uid: 1e13554f-a842-48dc-ab71-f01114c8cb3d
      resourceVersion: "21106533"
      selfLink: /api/v1/namespaces/default/pods/idea-u-daizc-0
      uid: fa9a121d-278c-477b-bd6c-290401a49840
    spec:
      containers:
      - image: jetbrains/projector-idea-u:2021.3-projector-v1.8.1
        imagePullPolicy: IfNotPresent
        name: idea-u-daizc
        ports:
        - containerPort: 8887
          name: http
          protocol: TCP
        resources: {}
        terminationMessagePath: /dev/termination-log
        terminationMessagePolicy: File
        volumeMounts:
        - mountPath: /home/projector-user
          name: idea-u-daizc-volume
        - mountPath: /var/run/secrets/kubernetes.io/serviceaccount
          name: kube-api-access-dtdp9
          readOnly: true
      dnsPolicy: ClusterFirst
      enableServiceLinks: true
      hostname: idea-u-daizc-0
      nodeName: node9
      preemptionPolicy: PreemptLowerPriority
      priority: 0
      restartPolicy: Always
      schedulerName: default-scheduler
      securityContext: {}
      serviceAccount: default
      serviceAccountName: default
      subdomain: idea-u-daizc
      terminationGracePeriodSeconds: 60
      tolerations:
      - effect: NoExecute
        key: node.kubernetes.io/not-ready
        operator: Exists
        tolerationSeconds: 300
      - effect: NoExecute
        key: node.kubernetes.io/unreachable
        operator: Exists
        tolerationSeconds: 300
      volumes:
      - name: idea-u-daizc-volume
        persistentVolumeClaim:
          claimName: idea-u-daizc-pvc
      - name: kube-api-access-dtdp9
        projected:
          defaultMode: 420
          sources:
          - serviceAccountToken:
              expirationSeconds: 3607
              path: token
          - configMap:
              items:
              - key: ca.crt
                path: ca.crt
              name: kube-root-ca.crt
          - downwardAPI:
              items:
              - fieldRef:
                  apiVersion: v1
                  fieldPath: metadata.namespace
                path: namespace
    status:
      conditions:
      - lastProbeTime: null
        lastTransitionTime: "2022-06-02T06:38:11Z"
        status: "True"
        type: Initialized
      - lastProbeTime: null
        lastTransitionTime: "2022-06-02T07:04:13Z"
        status: "True"
        type: Ready
      - lastProbeTime: null
        lastTransitionTime: "2022-06-02T07:04:13Z"
        status: "True"
        type: ContainersReady
      - lastProbeTime: null
        lastTransitionTime: "2022-06-02T06:38:11Z"
        status: "True"
        type: PodScheduled
      containerStatuses:
      - containerID: containerd://d98a22a7534a147c240fb44d93d784090979e524e69a6f4195a908ee8f2609a6
        image: docker.io/jetbrains/projector-idea-u:2021.3-projector-v1.8.1
        imageID: docker.io/jetbrains/projector-idea-u@sha256:fab2a81caa691ecc92faa20c453e8e911b0cd216793a8d97c3dc2dce579b8424
        lastState:
          terminated:
            containerID: containerd://c88b860653dac1f3e4680fb62fdefcd5fe73e210780078374d985da55b2cfd39
            exitCode: 0
            finishedAt: "2022-06-02T07:04:11Z"
            reason: Completed
            startedAt: "2022-06-02T06:38:12Z"
        name: idea-u-daizc
        ready: true
        restartCount: 1
        started: true
        state:
          running:
            startedAt: "2022-06-02T07:04:12Z"
      hostIP: 192.168.21.89
      phase: Running
      podIP: 10.233.97.107
      podIPs:
      - ip: 10.233.97.107
      qosClass: BestEffort
      startTime: "2022-06-02T06:38:11Z"
    ```
  - 使用浏览器直接访问或者`JetBrains Projector`进行连接
    - 使用Chrome连接时,会遇到各种权限问题
      ![Projector](/source/images/posts/在K8s中部署projector-idea-u/Snipaste_2022-06-06_09-32-05.jpg)
    - 【建议】使用Projector连接，在拷贝内容时不会触发浏览器警告
      ![Projector](/source/images/posts/在K8s中部署projector-idea-u/Snipaste_2022-06-06_09-26-59.jpg)
      ![Projector](/source/images/posts/在K8s中部署projector-idea-u/Snipaste_2022-06-06_09-30-38.jpg)
