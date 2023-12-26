---
title: '[Csharp]可为空的类型' 
date: 2023-12-05 23:12:05

categories:

- 我永远爱学习

tags:

- 'CSharp'
- '.NET'
---

# [Csharp]可为空的类型

## 一句话总结

值类型的不能`?`操作符的是通过`Nullable<T>`包装实现的，而引用类型由于本来就可以为空，所以`?`操作符只是个供编译器推断的标识而已。

<!--more-->

## 参考资料

- [可为 null 的引用类型](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/builtin-types/nullable-reference-types)
- [可为 null 的值类型](https://learn.microsoft.com/zh-cn/dotnet/csharp/language-reference/builtin-types/nullable-value-types)

```c#

    // 此处输出为 Nullable[int]
    Console.WriteLine($"int?:\t{typeof(int?)}");
    // 此处输出为 string
    Console.WriteLine($"obj?:\t{typeof(string?)}");
```

### 可空值类型

**一句话总结:**
`int?`和`int`是两个不一样的类型，`int?`其实是`Nullable<int>`的语法糖，因为值类型是直接指向具体值的，因此不能直接被指向null,必须经过包装。

```c#
    int aNn = null; // Error CS0037 : 无法将 null 转换为“int”，因为后者是不可为 null 的值类型

    int? a = null; // 其实是 Nullable<int> = new Nullable<>(); 
    Console.WriteLine(a.Value);     // 输出 null
    Console.WriteLine(a.HasValue);  // 输出 true
```

### 可空引用类型

**一句话总结:**  
`BaseWsRequest<object>?`和` BaseWsRequest<object>`其实是同一个类型，`string`和`string?`也一样。因为他们都是引用类型，可以指向null，因此编译器并不会对他们额外进行包装。

```c#
    string? str = null;
    string strNn = null;
    
    // 引用类型可以被声明为空引用
    BaseWsRequest<object>? obj = null;
    // 引用类型可以为null
    BaseWsRequest<object> objNn = null;
```

