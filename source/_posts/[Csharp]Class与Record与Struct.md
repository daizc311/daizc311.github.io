---
title: '[Csharp]Class与RecordStruct'
date: 2023-12-14 11:20:22
categories:

  - 我永远爱学习
tags:

  - 'CSharp'
  - '.NET'
cover:  https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/[Csharp]Class与RecordStruct/Class_Struct_Record.png
---

<!--more-->

**示例代码**

为了测试在C#中`Class`、`Struct`、`Record`三者的区别，建立了以下测试类。每个类中，分别建立了一个字段，一个自动属性，一个仅init的自动属性和一个具有私有setter的自动属性，
其中Record额外建立了一个记录属性。

```c#
public class ClassA {
    public string field;
    public string Prop { get; set; }
    public string PropInit { get; init; }
    public string PropPrivateSet { get; private set; }
    public override string ToString() {
        return $"{nameof(this.field)}: {this.field}, {nameof(this.Prop)}: {this.Prop}, {nameof(this.PropInit)}: {this.PropInit}, {nameof(this.PropPrivateSet)}: {this.PropPrivateSet}";
    }
}

public struct StructA {
    public string field;
    public string Prop { get; set; }
    public string PropInit { get; init; }
    public string PropPrivateSet { get; private set; }
    public override string ToString() {
        return $"{nameof(this.field)}: {this.field}, {nameof(this.Prop)}: {this.Prop}, {nameof(this.PropInit)}: {this.PropInit}, {nameof(this.PropPrivateSet)}: {this.PropPrivateSet}";
    }
}

public record RecordA(string PropRc) {
    public string field;
    public string Prop { get; set; }
    public string PropInit { get; init; }
    public string PropPrivateSet { get; private set; }
}
```

### 反序列化测试

设计一个反序列化的测试类，对三种类型分别进行反序列化测试，三种类型均可以正常进行反序列化。

```c#
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Serialization;
using System;

string str = """
             {
                "field":"Field",
                "prop":"Prop",
                "propInit":"Init Prop",
                "propPrivateSet":"Private Setter Prop",
                "PropRc":"Enum Main Constructor Param"
             }
             """;

var deserializeObject = JsonConvert.DeserializeObject<JObject>(str);
var classA = deserializeObject.ToObject<ClassA>();
var structA = deserializeObject.ToObject<StructA>();
var recordA = deserializeObject.ToObject<RecordA>();

var classB = JsonConvert.DeserializeObject<ClassA>(str);
var structB= JsonConvert.DeserializeObject<StructA>(str);
var recordB= JsonConvert.DeserializeObject<RecordA>(str);

Console.WriteLine(classA);
Console.WriteLine(structA);
Console.WriteLine(recordA);
Console.WriteLine(classB);
Console.WriteLine(structB);
Console.WriteLine(recordB);
```

![均可以正常序列化](https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/[Csharp]Class与RecordStruct/Class_Struct_Record.png)

### struct是值类型，是不可变的

