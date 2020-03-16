---
title: '好用常用人人都要会的最基本的Maven插件收集'
date: 2019-03-11 03:05:56
updated: 2020/2/20 15:41:36
comments: true
cover: /2019/03/11/好用常用人人都要会的最基本的Maven插件收集/title.jpg
coverWidth: 776
coverHeight: 450
categories: 
    - [<del>我永远爱学习</del>]
tags: 
    - java
    - maven
---

把手上的好几个项目的pom整理了一遍 规范了一下model 整理出了一些插件 方便新手们入门maven
<!--more-->

# 好用常用人人都要会的最基本的Maven插件收集
大部分的插件其实都可以从[MAVEN官方的可用插件列表]( https://maven.apache.org/plugins/index.html)中直接找到
在这里取了比较常用并且实用的几个插件做一点说明
大多数使用场景无非是：
- 要自定义打包的名称
- 要将打包后的jar包自动复制到某处
- 要将外置lib目录和maven依赖一起打进去
- 要根据环境复制对应的配置文件

这里选出的插件足以应对大部分情况了 


## Maven依赖管理插件[maven-dependency-plugin](https://maven.apache.org/plugins/maven-dependency-plugin)

主要用于管理依赖，比如引入某个特殊的jar包，或者从某个jar包中提取文件
`tree`命令和`display-ancestors`命令在搭建工程时是比较好用的，可以很方便的找出重复依赖项，保证依赖版本一致

### 常用功能
- list 列出的依赖关系
- tree 以树型结构列出的依赖关系
- copy-dependencies 拷贝某个依赖项
- unpack-dependencies 解包某个依赖项
- display-ancestors 显示所有父依赖



## Maven构建小助手 [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/)

主要用于为POM生成各种属性,比如打包时间、IP地址之类的，功能比较多就不上代码了。

###  常用功能
- add-source 将更多`source`目录添加到POM
- add-test-source 将更多`test source`目录添加到POM
- add-resource 将更多`resource directories`目录添加到POM
- add-test-resource 将更多`test resource directories`目录添加到POM
- attach-artifact Attach additional artifacts to be installed and deployed.  不知道???干啥的
- maven-version 获取Maven核心版本
- regex-property 使用正则生成某个属性
- regex-properties使用正则生成属性
- released-version Resolve the latest released version of this project.
- parse-version Parse the version into different properties.
- remove-project-artifact 用于在构建过程中删除某个作为依赖的项目以节省空间
- reserve-network-port 保留一个未使用的端口号的随机列表
- local-ip 获取当前主机IP
- cpu-count 获取当前主机CPU核心数
- timestamp-property 生成一个事件放入指定属性中 常用于在包名上附加打包时间
- uptodate-property 检查某个属性根据检查结果设置其他属性
- uptodate-properties 检查多个属性根据检查结果设置多个其他属性
- rootlocation 重定义多模块构建的根目录

## 复制并重命名插件 [Copy Rename Maven Plugin](https://coderplus.github.io/copy-rename-maven-plugin/)

功能字如其名,就是用来复制和重命名的，可以用来复制打包好的jar包，也可以在打包过程中复制文件。
  
### 常用功能
- copy 复制
- rename 重命名
- 没了

### 一个小栗子
```xml
<plugin>
    <groupid>com.coderplus.maven.plugins</groupid>
    <artifactid>copy-rename-maven-plugin</artifactid>
    <version>1.0</version>
    <executions>
        <execution>
            <id>copy-file</id>
            <phase>package</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <sourcefile>target/${project.build.finalName}.jar</sourcefile>
                <destinationfile>/xingyi/${project.build.finalName}.jar</destinationfile>
            </configuration>
        </execution>
    </executions>
</plugin>
```


## Maven编译插件 [maven-compiler-plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)

主要用于设定编译环境和编译器的属性,是比较基础的插件

```xml
 <plugin>
    <groupid>org.apache.maven.plugins</groupid>
    <artifactid>maven-compiler-plugin</artifactid>
    <version>3.8.0</version>
    <configuration>
        <!-- 一般而言，target与source是保持一致的，但是，有时候为了让程序能在其他版本的jdk中运行(对于低版本目标jdk，源代码中不能使用低版本jdk中不支持的语法 )，会存在target不同于source的情况 -->
        <!-- 源代码使用的JDK版本 -->
        <source>1.8</source>
        <!-- 需要生成的目标class文件的编译版本 -->
        <target>1.8</target> 
        <!-- 字符集编码 -->
        <encoding>UTF-8</encoding>
        <verbose>true</verbose>
        <showwarnings>true</showwarnings>
        <!-- 要使compilerVersion标签生效，还需要将fork设为true，用于明确表示编译版本配置的可用 -->
        <fork>true</fork>
        <!-- 指定插件将使用的编译器的版本 -->
        <compilerversion>1.5</compilerversion>
        <!-- 编译器使用的初始内存 -->
        <meminitial>128m</meminitial>
        <!-- 编译器使用的最大内存 -->
        <maxmem>512m</maxmem>
        <!--使用指定的javac命令，例如：<executable>${JAVA_1_4_HOME}/bin/javac</executable> -->
        <executable><!-- path-to-javac --></executable>
        <!-- 跳过测试 -->
        <skiptests>true</skiptests>
        <!-- 这个选项用来传递编译器自身不包含但是却支持的参数选项-->
        <compilerargument>-verbose -bootclasspath ${java.home}\lib\rt.jar</compilerargument>
    </configuration>
</plugin>
```

###  SpringBootMaven插件[spring-boot-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/)


####  configuration.layout=ZIP 的用处

###### 资料
- [How to really package and deploy a Spring Boot application]("http://weyprecht.de/2018/05/02/how-to-really-package-and-deploy-a-spring-boot-application/")

###### 作用
将该工程的布局改为ZIP布局，所有的lib将外置到jar包外，在打包时将com.example:demo下的所有依赖放入外置的lib目录

```xml
    <build>
      <plugins>
        <plugin>
          <groupid>org.springframework.boot</groupid>
          <artifactid>spring-boot-maven-plugin</artifactid>
          <configuration>
            <layout>ZIP</layout>
          </configuration>
          <includes>
            <include>
           		<groupid>com.example</groupid>
            	<artifactid>demo</artifactid>
            </include>
          </includes>
        </plugin>
      </plugins>
    </build>
```
