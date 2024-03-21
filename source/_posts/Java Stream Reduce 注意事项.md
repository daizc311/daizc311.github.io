---
title: 'Java Stream Reduce 注意事项'
date: 2024-03-21 22:43:00
cover:  https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/Java-Stream-Reduce注意事项/title.png

categories:
  - 我永远爱学习

tags:
  - Java
---
Java Stream Reduce 注意事项

👇出问题的代码，最终得到的List每个对象的属性都完全一样的，甚是邪门

```java
public List<CallboxAppVersion> listCAVbyObs(@NotNull String profile) {
    // 略...
    return objectListing.getObjects().stream().map(obsObject -> 略)
            .collect(Collectors.groupingBy(
                    CallboxObsKeyMatcherResult::version,
                    Collectors.reducing(
                            new CallboxAppVersion(),
                            (CallboxObsKeyMatcherResult mr) -> {
                                var appVersion = new CallboxAppVersion();
                                appVersion.setName("Callbox");
                                appVersion.setBrand(mr.brand());
                                appVersion.setVersion(mr.version());
                                appVersion.setType(mr.type());
                                if (mr.suffix().equals("zip")) {
                                    appVersion.setZipKey(mr.objectKey());
                                } else if (mr.suffix().equals("exe")) {
                                    appVersion.setExeKey(mr.objectKey());
                                }
                                return appVersion;
                            },
                            (o1, o2) -> {
                                o1.setName(o1.getName() == null ? o2.getName() : o1.getName());
                                o1.setVersion(o1.getVersion() == null ? o2.getVersion() : o1.getVersion());
                                o1.setBrand(o1.getBrand() == null ? o2.getBrand() : o1.getBrand());
                                o1.setType(o1.getType() == null ? o2.getType() : o1.getType());
                                o1.setExeKey(o1.getExeKey() == null ? o2.getExeKey() : o1.getExeKey());
                                o1.setZipKey(o1.getZipKey() == null ? o2.getZipKey() : o1.getZipKey());
                                return o1;
                            })
            ))
            .values().stream().toList();
}
```

由于代码太过复杂，所以先对逻辑进行剥离，方便观察。 首先先对最复杂的`Collectors.reducing()`函数进行简化：

- Collectors被大量引入，所以进行静态import
- `reducing()`中的`mapping`部分是将MatcherResult变为CallboxAppVersion，我们抽象为CallboxAppVersion的`constructor`
- `reducing()`中的`merge`部分是将两个CallboxAppVersion合并为一个，我们抽象为`fun merge()`

👇剥离业务逻辑后的代码

```java
 public List<CallboxAppVersion> listCAVbyObs(@NotNull String profile) {
    // 略...
    var collect = objectListing.getObjects().stream().map(obsObject -> 略)
            .filter(Objects::nonNull)
            .filter(matcherResult -> matcherResult.version() != null)
            .collect(Collectors.groupingBy(
                    CallboxObsKeyMatcherResult::version,
                    Collectors.reducing(
                            new CallboxAppVersion(),
                            CallboxAppVersion::new,
                            CallboxAppVersion::merge
                    )
            ));
    return collect.values().stream().toList();
}
```

观察上面的代码，在return处下了断点，我发现map的key正常，但所有value的属性是一样的，我认为是mapping时出现了问题，随即调整结构，将`reduce(new,mapping,merge)`
调整为`mapping(mapping,reduce(new,merge))`。

```java
public List<CallboxAppVersion> listCAVbyObs(@NotNull String profile) {
    // 略...
    var collect = objectListing.getObjects().stream().map(obsObject -> 略)
            .collect(Collectors.groupingBy(
                    CallboxObsKeyMatcherResult::version,
                    mapping(CallboxObsKeyMatcherResult::new,
                            reducing(new CallboxAppVersion(), CallboxAppVersion::merge)
                    )
            ));
    return collect.values().stream().toList();
}
```

保持return处的断点，再次运行后，结果仍然一致，这个时候我发现`collect`
的values中，所有的value是同一个引用，而代码中有一个`new CallboxAppVersion()`作为reducing的初始值，这十分可疑。  
但这个new在lambda中，就算使用调试器也不好直接观察，于是到CallboxAppVersion的constructor和merge中下断点。

```java

public class CallboxAppVersion {
    private String name;
    private String brand;
    private Version version;
    private String type;
    private String zipKey;
    private String exeKey;

    public CallboxAppVersion(CallboxObsKeyMatcherResult mr) {
        this.setName("Callbox");
        this.setBrand(mr.brand());
        this.setVersion(mr.version());
        this.setType(mr.type());
        if (mr.suffix().equals("zip")) {
            this.setZipKey(mr.objectKey());
        } else if (mr.suffix().equals("exe")) {
            this.setExeKey(mr.objectKey());
        }
    }

    public CallboxAppVersion merge(CallboxAppVersion that) {
        this.setName(that.getName() == null ? this.getName() : that.getName());
        this.setBrand(that.getBrand() == null ? this.getBrand() : that.getBrand());
        this.setVersion(that.getVersion() == null ? this.getVersion() : that.getVersion());
        this.setType(that.getType() == null ? this.getType() : that.getType());
        this.setZipKey(that.getZipKey() == null ? this.getZipKey() : that.getZipKey());
        this.setExeKey(that.getExeKey() == null ? this.getExeKey() : that.getExeKey());
        return this;
    }
}
```

👇猛然发现在merge方法中return了this，并没有复制实体，于是修改merge实现

```java
public CallboxAppVersion merge(CallboxAppVersion that) {
    CallboxAppVersion merged = new CallboxAppVersion();
    merged.setName(that.getName() == null ? this.getName() : that.getName());
    merged.setBrand(that.getBrand() == null ? this.getBrand() : that.getBrand());
    merged.setVersion(that.getVersion() == null ? this.getVersion() : that.getVersion());
    merged.setType(that.getType() == null ? this.getType() : that.getType());
    merged.setZipKey(that.getZipKey() == null ? this.getZipKey() : that.getZipKey());
    merged.setExeKey(that.getExeKey() == null ? this.getExeKey() : that.getExeKey());
    return merged;
}
```

修改merge后，返回结果正常。判定是`new CallboxAppVersion()`
的问题，结合`reducing(new CallboxAppVersion(), CallboxAppVersion::new, CallboxAppVersion::merge)`的写法，我们可以反向推断lambda在这里的实现

👇伪代码

```
groupingBy(key,values){
    reducedValue = reducing(firstobj,fun mapping,fun merge){
        var current = firstobj
        for value of values:
            next = mapping(value)
            current = merge(first,next)
        # 其中这个current被复用了，就是我new的那个CallboxAppVersion对象
        retrun current
    }
    retrun (key,reducedValue)
}
```

lambda用多了老是会觉得对象都是final的不可变也不复用，就感觉这个reduce的设计有点反直觉，如果这个current的变量设计为一个creator函数就不会有问题了，就像下面这样  
👇伪代码

```
groupingBy(key,values){
    reducedValue = reducing(fun firstCretor,fun mapping,fun merge){
        # 这个current每次都传新对象，就不会有这个问题了
        var current = firstCretor()
        for value of values:
            next = mapping(value)
            current = merge(first,next)
        retrun current
    }
    retrun (key,reducedValue)
}
```

对应代码

```java
// 原实现
@SuppressWarnings("unchecked")
private static <T> Supplier<T[]> boxSupplier(T identity) {
    return () -> (T[]) new Object[]{identity};
}

// 注意到第一个参数名是identity，语义上感觉这个就应该传入一个独一无二的变量才对
public static <T, U> Collector<T, ?, U> reducing(U identity, Function<? super T, ? extends U> mapper, BinaryOperator<U> op) {
    return new CollectorImpl<>(
            // 这里参数列表对不上，还起了个数组来hold住identity，不知道这么实现是为了什么？
            boxSupplier(identity),
            (a, t) -> {a[0] = op.apply(a[0], mapper.apply(t));},
            (a, b) -> {
                a[0] = op.apply(a[0], b[0]);
                return a;
            },
            a -> a[0], CH_NOID);
}

// 期望实现
public static <T, U> Collector<T, ?, U> reducing(Supplier<U> identityCreator, Function<? super T, ? extends U> mapper, BinaryOperator<U> op) {
    // 第一个参数如果改成Supplier就可以每次生成新对象，避免上述问题了
    return new CollectorImpl<>(
            identityCreator,
            (a, t) -> {a[0] = op.apply(a[0], mapper.apply(t));},
            (a, b) -> {
                a[0] = op.apply(a[0], b[0]);
                return a;
            },
            a -> a[0], CH_NOID);
}
```

**其他优化**

发现CallboxAppVersion类中的merge方法完全可以封装成接口，这个肯定很常用的，而jdk里没带Mergeable接口，Spring带的又没泛型，那就自行封装一个，然后CallboxAppVersion去实现一下。

```java
interface Mergeable<T> {
    T merge(T that);
}
```