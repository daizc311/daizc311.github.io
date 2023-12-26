---
title: '解决Springdoc-OAS3中Schema重复的问题'
date: 2022/08/16 09:52:05
cover:  https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/解决Springdoc-OAS3中Schema重复的问题/swagger_logo.svg
coverHeight: 270
coverWidth: 938 
categories:
- 我永远爱学习
tags:
- Java
---

在使用`springdoc-openapi`时，如果项目中存在多个SimpleName一致的Class，
那在Schema描述中将会依照解析的先后顺序互相覆盖，从而导致Api文档的描述与预期不一致。

**相似问题**

- [Duplicate class names in different packages get squashed in ControllerDocumentation](https://github.com/springfox/springfox/issues/182)

**解决方案**

- 启用`springdoc.use-fqn`属性，所有的类将解析为全限定名
- 重写`TypeNameResolver`，自定义解析规则

<!--more-->

## 重写`TypeNameResolver`

需要注意的是，父类中useFqn被限定为`private`，在子类中无法获取。因此重写的逻辑（除`super.getNameOfClass(cls)`
外）无法对开启`useFqn`的情况做出适配。

```java
/**
 * 用于自定义Schema生成逻辑 以区分不同包下的同名Schema
 */
public class CustomOAS3TypeNameResolver extends TypeNameResolver {

    @Override
    protected String getNameOfClass(Class<?> cls) {

        var packageName = cls.getPackageName();
        if (packageName.contains("xxxxxx")) {
            return cls.getSimpleName();
        } else if (packageName.contains("org.gitlab4j")) {
            return "Gitlab" + cls.getSimpleName();
        }
        return super.getNameOfClass(cls);
    }
}
```

## 替换掉原本的`TypeNameResolver`

1. 原本的`TypeNameResolver`是个私有静态单例，上面还打了`final`标记，看起来是没有替换原本变量的可能了。
2. 往上一层走到`ModelResolver`类中，这个类提供了两个构造，其中一个支持传入`TypeNameResolver`参数，可以从这里入手覆盖。
3. `ModelResolver`是被`ModelResolvers`初始化的，虽然`ModelConverters`
   也是个私有静态单例改不掉他，但他本质是个对`List<ModelConverter>`的包装类，提供了操作其中`converters`
   的方法，所以我们可以通过操作内部的数组来放入自定义的`TypeNameResolver`。
4. 由于`ModelConverters`也是静态单例，只要在Spring调用它解析Model之前替换调原本的`TypeNameResolver`就行了，因此可以直接怼到启动类中。

```java

@SpringBootApplication
public class BeamApplication {

    public static void main(String[] args) {
        customOAS3TypeConverter();
        SpringApplication.run(BeamApplication.class, args);
    }

    /**
     * 自定义OAS3类型转换器
     */
    private static void customOAS3TypeConverter() {
        var instance = ModelConverters.getInstance();
        var converter = instance.getConverters().get(0);
        instance.removeConverter(converter);
        instance.addConverter(new ModelResolver(Json.mapper(), new CustomOAS3TypeNameResolver()));
    }
}
```

重启项目后，打开openapi文档。相同SimpleName的Schema已经区分来了。
![完成截图](https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/解决Springdoc-OAS3中Schema重复的问题/截图_选择区域_20220816104309.png)
