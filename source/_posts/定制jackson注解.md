---
title: 定制Jackson注解实现字段脱敏
date: 2020-12-10 09:51:54
categories:
- 我永远爱学习

tags:
- Java

---
## 功能

在保证jackson原注解不失效的前提下，通过自定义注解对POJO中部分指定的字段进行自定义处理

## 完成样式

```java
public class Test {

    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new EnhanceJacksonAnnotationIntrospector());
        A a = new A();
        a.setAge(10);
        a.setName("ASDASDASD");
        a.setRemark("REMARK!");
        a.setMobile("17600000000");
        System.out.println(objectMapper.writeValueAsString(a));
    }


    @Data
    static class A {

        private String name;

        private Integer age;

        private String remark;

        @JsonMask(a = "",b = "",c = "")
        private String mobile;
    }
}

```
## 运行结果

```json
  {"name":"ASDASDASD","age":10,"remark":"REMARK!","mobile":"1760****000"}
```

<!--more-->

## 核心逻辑
 
实现逻辑比较简单，核心类只有两个:
- Jackson注解拦截器 com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
- Json序列化器      com.fasterxml.jackson.databind.JsonSerializer<T>

Jackson序列化逻辑:

1. 首先看到这里`com.fasterxml.jackson.databind.ObjectMapper#_configAndWriteValue`，
这个方法根据objectMapper实例中的config创建对应的`JsonGenerator`并序列化value。
2. 追踪到`com.fasterxml.jackson.databind.ser.DefaultSerializerProvider#serializeValue`
根据源码可知，在序列化时使用哪个JsonSerializer是由`com.fasterxml.jackson.databind.SerializerProvider#findTypedValueSerializer`
决定的，`SerializerProvider.findTypedValueSerializer`就是`序列化器提供者.根据value类型查找序列化器`。
3. !(三句源码注释)[]根据三句源码注释可知，jackson在这里维护了一个本地缓存一个共享缓存，在两个缓存都没找到的时候再通过`com.fasterxml.jackson.databind.SerializerProvider#findValueSerializer`
获取新的序列化器并存入缓存。
4. 进入到`com.fasterxml.jackson.databind.SerializerProvider#findValueSerializer`内部，再次进行了缓存查找，
在都没有命中的情况下，调用`com.fasterxml.jackson.databind.SerializerProvider[521]`的`_createAndCacheUntypedSerializer`去创建序列化器。
5. 快进到`com.fasterxml.jackson.databind.ser.BeanSerializerFactory#createSerializer`,这里是构建序列化器的真实逻辑。注意下`findSerializerFromAnnotation(prov, beanDesc.getClassInfo())`这个方法，
根据方法名直译就是`根据注解获取序列化器`。
6. 进入到`com.fasterxml.jackson.databind.ser.BasicSerializerFactory#findSerializerFromAnnotation`内部，发现Jackson获取序列化器分为两步:
    - 调用`prov.getAnnotationIntrospector().findSerializer(a)`找到序列化器的class，随后在`prov.serializerInstance(a, serDef)`中实例化改序列化器
    - 调用`prov.getAnnotationIntrospector().findSerializer(a)`直接获取`JsonSerializer`类型的实例，随后在`prov.serializerInstance(a, serDef)`中强转为JsonSerializer<?>
7. 快进到`com.fasterxml.jackson.databind.AnnotationIntrospector#findSerializer`，这里具体使用的是`JacksonAnnotationIntrospector`类。这个类中定义了jackson中的序列化和反序列化有关的注解及其对应的处理方式。
8. 进入到`com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector.findSerializer`，发现这里处理了两个注解`@JsonSerialize`、`@JsonRawValue`，找到注解的情况下new出了对应的序列化器，没有找到任何注解返回了null。
9. 到此为止=====

根据上面整理的逻辑。要想jackson处理自定义注解，就必须给jackson配置一个自定义的`AnnotationIntrospector`，如果这里手动实现`AnnotationIntrospector`接口，那么jackson自带的注解都会失效，因此选择继承原有的`JacksonAnnotationIntrospector`类。
在这个类中，可以通过返回不同的序列化器来控制序列化的具体行为，其方法入参`Annotated`类实际上是对被序列化字段的类型的包装。但注意，在`AnnotationIntrospector`中是无法拿到被序列化的值本身的，只能拿到字段的相关信息。

## 重写

### 重写JacksonAnnotationIntrospector 增强Jackson注解处理器
```java
/**
 * <h2>增强Jackson注解处理器</h2>
 *
 * @author Daizc-kl
 * @date 2020/12/9 17:27
 */
public class EnhanceJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public Object findSerializer(Annotated a) {
        // 调用父类逻辑保证原注解继续生效
        Object serializer = super.findSerializer(a);
        // Jackson原装进口注解优先
        if (serializer == null) {
            // 只对Getter生效
            if (a instanceof AnnotatedMethod) {
                // 返回值类型为String
                if (a.getType().getRawClass().equals(String.class)) {
                    // 头上有MaskProperty注解
                    JsonMask jsonMask = a.getAnnotation(JsonMask.class);
                    if (null != jsonMask) {
                        // 实例化出自己的序列化器并返回
                        return new MaskMethodSerializer(jsonMask);
                    }
                }
            }
        }
        return serializer;
    }
}
```
### 重写JsonSerializer<T> 序列化器

```java
/**
 * <h2>xxx方法序列化器</h2>
 *
 * @author Daizc-kl
 * @date 2020/12/9 18:23
 */
public class MaskMethodSerializer extends JsonSerializer<String> {
    // 字段上的注解本身
    private final JsonMask jsonMask;
    
    public MaskMethodSerializer(JsonMask jsonMask) {
        this.jsonMask = jsonMask;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 自定义序列化行为
        // 略......

        gen.writeString("...最终输出的字符串...");
    }
}
```
