# 内核

Dubbo SPI



SpringFactoriesLoader ->   /META-INF/spring.factories

key= value

org.springframework.configuration.AutoConfiguration = \

  com.gupaoedu....



SPI（service provide interface）



## Dubbo的SPI

自适应扩展点

激活扩展点

能够被扩展的接口，必须要有这样的标记 -> @SPI()->Dubbo自定义的注解

@SPI("value") - > 表示当前扩展点的默认实现

proxy=javassit

## 静态扩展点

1. load 指定路径下对应SPI扩展点的实现，缓存到hashmap，key对应的就是文件中定义的key，value就是class
2. getExtension("name") -> EXTENSION_INSTANCES.get("name")

```
        Protocol protocol=ExtensionLoader.getExtensionLoader(Protocol.class).getExtension("myprotocol");
       
                                                       getExtension(cachedDefaultName);

```

## 依赖注入



set方法进行依赖注入（）

如果当前的扩展点中依赖其他的扩展点，则需要进行依赖注入



MyProtocol 

   private  OtherIterface oi;

   public void setOi(OtherIterfae oi){

​        this.oi=oi;

   }



## 自适应扩展点

@Adaptive

两种方法，



一种是类(提前写好了一个自适应的扩展点 -> 主要目的就是针对具体的实现进行适配)

一种是方法

![1563980048578](C:\Users\mic\AppData\Roaming\Typora\typora-user-images\1563980048578.png)

动态生成代理类。

```java
ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension()
```

![1563980036930](C:\Users\mic\AppData\Roaming\Typora\typora-user-images\1563980036930.png)

## 激活扩展点





