---
title: "java8函数设计[1]-在filter中根据Key去重的函数"
date: 2019/12/19 17:54:52
updated: 2020/2/20 15:41:36
cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/java8函数设计[1]-在filter中根据Key去重的函数/title.jpg?x-oss-process=style/blog_title
comments: true
categories: 
    - 我永远爱学习
tags: 
    - Java
---

函数接口是如何写出来的？
<!--more-->

# java函数接口设计
## 在filter中根据Key去重的函数 StreamUtil.distinctByKey()

### 具体使用方法

- 该函数用于在filter中根据传入参数的某一属性进行过滤，以保证在收集为map的情况下不会出现重复主键


```java
List<entry<string, string="">&gt; simpleList = baseProjects.stream()
                .filter(StreamUtil.distinctByKey(BaseProject::getTypeDic))
                .map(StreamUtil.entry(BaseProject::getTypeDic, BaseProject::getTypeStr))
                .collect(Collectors.toList());
``` 


### 接口设计
```java
    /**
     * <h3>distinctByKey</h3>
     * <p>可用于在filter中根据属性过滤</p>
     *
     * @param function 接受一个 接受 类型A 返回 类型B 的函数式接口
     * @return Predicate 返回一个 接受 类型A 返回 Boolean型 的函数式接口
     */
    public static <t> Predicate<t> distinctByKey(Function<!--? super T, ?--> function) {
        Map filterMap = Maps.newConcurrentMap();

        return t -&gt; filterMap.putIfAbsent(function.apply(t), Boolean.TRUE) == null;
    }
```

### 设计思路

1. 观察Stream.filter()接口
2. 对接Stream.filter()接口
3. 实现去重功能
4. 参数优化

### 开始编写

####  观察Stream.filter()接口 &amp;&amp; 对接Stream.filter()接口

 Stream.filter()接口:`Stream<t> filter(Predicate<!--? super T--> predicate)`


 首先可以看到filter接口需要接收一个类型为`Predicate<!--? super T-->`的函数,这个函数接受一个参数返回一个boolean类型。

根据以上信息先写一个函数出来
```java
public static <e> Predicate<e> distinctByKey2() {
        return new Predicate<e>() {
            @Override
            public boolean test(E e) {
                return false;
            }
        };
    }

```

这个函数虽然可以被filter正常接收，但是由于没有形参，因此无法传递参数
```java
 public static void main(String[] args) {
        Lists.newArrayList()
                .stream()
                .filter(StreamUtil.distinctByKey2())
                .collect(Collectors.toList());
    }
```

现在与预期的效果比对一下
| 对比  | 函数 |
| :-- | - |
| 目前效果 | `.filter(StreamUtil.distinctByKey2())` |
| 预期的效果 | `.filter(StreamUtil.distinctByKey2(Xxxxx:getId))` |
有点不对？！这个函数虽然可以被filter正常接收，但是去无法传入参数。因此要给`distinctByKey2()`方法加上传入的参数。
通过观察预期效果，是需要传入的参数应该是一个函数的，这个函数接收一个`T类型`参数，返回一个`不知道什么类型`的参数。
`Function<t, r="">`函数接收一个T类型，返回一个R类型，可以满足这个需求。
继续观察预期效果，这个`不知道什么类型的参数`实际上就是`distinctByKey2方法中new出来Predicate<e>#test(E e)`中的那个e，说人话就是`Function<t,r>`中的R在此处就是`Predicate<e>`中的E。
那么补上我们的形参
```java
public static <e,r> Predicate<e> distinctByKey2(Function<e,r> function) {
        return new Predicate<e>() {
            @Override
            public boolean test(E t) {

                return false;
            }
        };
    }
```

#### 实现去重功能
现在与预期效果一致了，需要实现去重功能 去重功能可以用Set或者ConcurrentMap实现
使用ConcurrentMap存储function返回值的状态
根据`ConcurrentMap.putIfAbsent(xxx)`的特性 如果map中已经有同样的key和value就返回null，根据返回值是否为null来判断是否需要被过滤

```java
   public static <e,r> Predicate<e> distinctByKey2(Function<e,r> function) {

        Map filterMap = Maps.newConcurrentMap();

        return new Predicate<e>() {
            @Override
            public boolean test(E t) {
                // 调用方传入函数的结果
                R apply = function.apply(t);
                // putIfAbsent 在首次关联K,V时返回null 非首次的时候不执行put()方法 直接返回之前的值
                Boolean isNullIsDuplicate = filterMap.putIfAbsent(apply, Boolean.TRUE);
                // isNullIsDuplicate==null时 K就没重复 函数返回true
                return isNullIsDuplicate == null;
            }
        };
    }
```


#### 参数优化

把匿名内部类使用lambda替换掉，在把冗余代码inline，最后调整一下泛型参数
    
    在上面的例子中`Function<e,r>`中
    R是调用者函数的返回值类型，仅仅被当做key使用，本身是什么类型不重要，因此可以直接去掉这个R泛型，由?代替
    E是调用者函数的形参类型，函数调用方在函数中可能会显式声明参数类型`[注1]`，以达到强转形参类型的目的，因此将E修改为 <!--? super E-->
    
```java
 public static <e> Predicate<e> distinctByKey2(Function<!--? super E, ?--> function) {
        Map filterMap = Maps.newConcurrentMap();

        return e -&gt; filterMap.putIfAbsent(function.apply(e), Boolean.TRUE) == null;
    }
    
 //[注1] 
 public static void main(String[] args) {
        List collect = Lists.newArrayList()
                .stream()
                // 显式强转形参类型
                .filter(StreamUtil.distinctByKey2(baseEntity -&gt; baseEntity))
                .collect(Collectors.toList());
    }
```










