---
title: '[Csharp]WPF集成Sqlite与数据库加解密' 
date: 2023-12-05 23:12:05

categories:

- [我永远爱学习]

tags:

- 'CSharp'
- '.NET'

cover: https://oss.note.dreamccc.cn/note/images/posts/[Csharp]WPF集成Sqlite与数据库加解密/title.png?x-oss-process=style/blog_title
---
# [Csharp]WPF集成Sqlite与数据库加解密

## 一句话总结

**关于依赖安装**  
抽象层选用`Entity Framework Core`，只能安装`3.x.x`版本的。
因为`.NETFramework`是传统框架，只支持到3版本，不然就只能装大版本为6的`Entity Framework(EF6)`，但是由于EF6已经不积极维护了，综合考虑还是更建议装`EFC3`。

**关于数据库加密**  
SQLite原版没有实现数据库加密，要加密就只能选用其他的SqLite发行版，他们的加密方式各异，用谁创建的数据库就得用谁读写。
推荐[SQLiteStudio](https://sqlitestudio.pl/)工具，可以选择很多加解密方式。

**关于WPF集成SqLite加密**  
巨硬官方已经出了例子了.
```shell
dotnet remove package Microsoft.Data.Sqlite    # 这是个集成包，删掉
dotnet add package Microsoft.Data.Sqlite.Core  # 这才是微软对Sqlite的封装核心
dotnet add package SQLitePCLRaw.bundle_e_sqlcipher  # 这个真正想使用的Sqlite发行版的bundle包
```
修改`ConnectString`即可
```c#
# 记得别直接字符串拼，到时候被人注入了
SqliteConnectionStringBuilder sqliteConnectionStringBuilder = new SqliteConnectionStringBuilder {
          Password = "xxxxxxxxxx",
          Mode = SqliteOpenMode.ReadWriteCreate,
          DataSource = "xxxxxxx.db"
      };
var connectString =  sqliteConnectionStringBuilder.ToString();  
```

<!--more-->

## 参考资料
 - [(1#)在 Windows 应用中使用 SQLite 数据库](https://learn.microsoft.com/zh-cn/windows/apps/develop/data-access/sqlite-data-access)
 - [(2#)Problems trying to encrypt SQLite database on UWP](https://stackoverflow.com/questions/63886988/problems-trying-to-encrypt-sqlite-database-on-uwp)
 - [(3#)加密 - Microsoft.Data.Sqlite](https://learn.microsoft.com/zh-cn/dotnet/standard/data/sqlite/encryption?tabs=netcore-cli)
 - [(4#)Incompatible SQLCipher 4 database](https://github.com/utelle/SQLite3MultipleCiphers/issues/47)
 - [(5#)SQLiteStudio](https://sqlitestudio.pl/)


## WPF应用集成数据库
通过阅读文档(1#)，大概了解了下DotNet的ORM生态,基本上就是用巨硬自家的解决方案`Entity Framework`。
然后我打开Nuget，开始安装依赖，很快就遇到了第一个问题**没法安装**

![第一个问题](/source/images/posts/[Csharp]WPF集成Sqlite与数据库加解密/EFC_Nuget.png)

为什么？因为这个包是`Entity Framework Core`，是给`DotNet Core`使用的对象映射框架。
而我们的基础框架是`.NETFramework 4.8.1`，只能用传统的`Entity Framework`，可恶的巨硬。
