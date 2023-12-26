---
title: '搭建Maven脚手架时一些并不太常见的问题'
date: 2022/08/10 15:55:00
updated: 2022/08/10 15:55:00
comments: true
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/搭建Maven脚手架时一些并不太常见的问题/title.png?x-oss-process=style/blog_title
categories:
- 我永远爱学习
tags:
- Java
- Maven
---


<!--more-->

## 使用脚手架生成的pom.xml中含有大量空白行

片段如下图，生成的xml含有大量空白行
```xml
  <dependencies>
        <dependency>


            <groupId>com.alibaba</groupId>


            <artifactId>fastjson</artifactId>


            <version>1.2.79</version>


        </dependency>
        <dependency>


            <groupId>com.google.guava</groupId>


            <artifactId>guava</artifactId>


            <version>31.0.1-jre</version>


        </dependency>
        <dependency>


            <groupId>org.slf4j</groupId>


            <artifactId>slf4j-ext</artifactId>


        </dependency><!-- endregion -->


    </dependencies>
```
### 参考资料

- [Remove dom4j library](https://issues.apache.org/jira/browse/ARCHETYPE-568)
- [Resulting root pom.xml from archetype generation has additional newlines with JDK11](https://issues.apache.org/jira/browse/ARCHETYPE-584)
- [Multi-modules Archetypes' Root POM file contains empty lines in Java 11](https://issues.apache.org/jira/browse/ARCHETYPE-587)

### 解决方案
这是`maven-archetype-plugin`插件的BUG，起因是`maven-archetype-plugin:3.1.1`使用`Java XML API`替换了`dom4j`用以生成xml，此行为导致不同的Java版本将会影响最终xml的生成结果。

最终解决方案就是换用`maven-archetype-plugin:3.1.0`及以下版本，或者使用Java8.

## 在项目中添加.gitignore文件

脚手架结构如下图所示，其中以`.`开头的隐藏文件均为包含在输出目录中

``` 
xxx-archetype
|- .idea
|- src
|  |- main
|     |- resources
|        |- archetype-resources
|        |  |- __rootArtifactId__-common
|        |  |- __rootArtifactId__-data
|        |  |- __rootArtifactId__-proxy
|        |  |- __rootArtifactId__-service
|        |  |- __rootArtifactId__-web
|        |  |- .editorconfig
|        |  |- .gitignore
|        |  |- .gitlab-ci.yml
|        |  |- Dockerfile
|        |  |- lombok.config
|        |  |- pom.xml
|        |- META-INF
|           |- maven
|           |- archetype-metadata.xml
|- pom.xml
```

### 解决方式：　
- 修改`pom.xml`升级`maven-archetype-plugin`到`3.2.1`版本，并设置变量`useDefaultExcludes=false`
    ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-archetype-plugin</artifactId>
        <version>3.2.1</version>
        <configuration>
            <useDefaultExcludes>false</useDefaultExcludes>
        </configuration>
    </plugin>
    ```
- 修改`pom.xml`升级`maven-resources-plugin`到`3.2.0`版本，并设置变量`addDefaultExcludes=false`
    ```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>3.2.0</version>
        <configuration>
            <addDefaultExcludes>false</addDefaultExcludes>
        </configuration>
    </plugin>
    ```
- 在`archetype-metadata.xml`中添加需要包含的文件
    ```xml
    <fileSets>
        <fileSet filtered="true" encoding="UTF-8">
            <directory/>
            <includes>
                <include>.editorconfig</include>
                <include>.gitignore</include>
                <include>Dockerfile</include>
                <include>lombok.config</include>
                <include>.gitlab-ci.yml</include>
            </includes>
        </fileSet>
    </fileSets>
    ```
## 在archetype-metadata.xml中启用代码提示 (适用于所有XML)

按照官方文档将namespace更新为1.1.0版本，并在idea中将光标指向xmlns链接点击获取外部资源即可。

```xml
<archetype-descriptor
        xmlns="https://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.1.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="https://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.1.0 http://maven.apache.org/xsd/archetype-descriptor-1.1.0.xsd"
        name="xxxxx-archetype" partial="false">
</archetype-descriptor>
```
