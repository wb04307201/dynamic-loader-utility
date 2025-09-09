# Loader Util - 动态类加载工具库

> 一个用于Java动态类加载、编译和Spring集成的工具库，支持运行时动态编译Java代码、加载类文件、注册Spring Bean等功能。

[![](https://jitpack.io/v/com.gitee.wb04307201/loader-util.svg)](https://jitpack.io/#com.gitee.wb04307201/loader-util)
[![star](https://gitee.com/wb04307201/loader-util/badge/star.svg?theme=dark)](https://gitee.com/wb04307201/loader-util)
[![fork](https://gitee.com/wb04307201/loader-util/badge/fork.svg?theme=dark)](https://gitee.com/wb04307201/loader-util)
[![star](https://img.shields.io/github/stars/wb04307201/loader-util)](https://github.com/wb04307201/loader-util)
[![fork](https://img.shields.io/github/forks/wb04307201/loader-util)](https://github.com/wb04307201/loader-util)  
![MIT](https://img.shields.io/badge/License-Apache2.0-blue.svg) ![JDK](https://img.shields.io/badge/JDK-17+-green.svg) ![SpringBoot](https://img.shields.io/badge/Srping%20Boot-3+-green.svg)

## 代码示例
1. 使用[动态编译工具](https://gitee.com/wb04307201/loader-util)实现的[动态编译工具工具示例代码](https://gitee.com/wb04307201/loader-util-test)
2. 使用[动态调度](https://gitee.com/wb04307201/dynamic-schedule-spring-boot-starter)、[消息中间件](https://gitee.com/wb04307201/message-spring-boot-starter)、[动态编译工具](https://gitee.com/wb04307201/loader-util)、[实体SQL工具](https://gitee.com/wb04307201/sql-util)实现的[在线编码、动态调度、发送钉钉群消息、快速构造web页面Demo](https://gitee.com/wb04307201/dynamic-schedule-demo)

## 功能特性

## 主要功能

### 1. 动态编译和类加载
### 2. Spring 集成
### 3. 方法调用工具
### 4. 切面编程支持

## 核心组件

### LoaderUtils
主要的工具类，提供以下功能：
- [compiler()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L23-L32): 编译Java源代码并加载到内存
- [compilerOnce()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L45-L60): 一次性编译并加载Java代码
- [load()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L68-L77): 加载已编译的类
- [addJarPath()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L84-L92): 添加JAR文件到类路径
- [registerSingleton()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L103-L114): 注册单例Bean
- [registerController()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L125-L136): 注册控制器
- [clear()](src\main\java\cn\wubo\loader\util\LoaderUtils.java#L144-L146): 清理动态类加载器缓存

### MethodUtils
方法调用工具类，提供：
- [invokeClass()](src\main\java\cn\wubo\loader\util\MethodUtils.java#L28-L39): 调用类中的方法
- [invokeBean()](src\main\java\cn\wubo\loader\util\MethodUtils.java#L74-L76): 调用Spring Bean中的方法
- [proxy()](src\main\java\cn\wubo\loader\util\MethodUtils.java#L105-L107): 创建代理对象并应用切面

### SpringContextUtils
Spring上下文工具类，提供：
- Bean的注册和注销
- 控制器的动态注册
- Spring上下文访问功能

## 快速开始
### 引入依赖
增加 JitPack 仓库
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

1.1.0版本后升级到jdk17 SpringBoot3+  
1.2.0重构核心代码
继续使用jdk 8请查看jdk8分支
```xml
<dependency>
    <groupId>com.gitee.wb04307201</groupId>
    <artifactId>loader-util</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 使用
#### 编译Class并执行
```java
void testClass() {
  String javaSourceCode = """
          package cn.wubo.loader.util;
                          
          public class TestClass {
                          
              public String testMethod(String name){
                  return String.format("Hello,%s!",name);
              }
          }
          """;
  LoaderUtils.compiler(javaSourceCode, "cn.wubo.loader.util.TestClass");
  Class<?> clazz = LoaderUtils.load("cn.wubo.loader.util.TestClass");
  String str = (String) MethodUtils.invokeClass(clazz, "testMethod", "world");
}
//注意：如果重复编译同样的类，会发生异常，如果确实需要这种场景请使用LoaderUtils.compilerOnce
//也可以使用LoaderUtils.clear方法关闭旧的DynamicClassLoader单例后重新编译

// 通过LoaderUtils.compiler编译的类会缓存到内存中，可以在其他方法中获得
void testClassDelay() {
  Class<?> clazz = LoaderUtils.load("cn.wubo.loader.util.TestClass");
  String str = (String) MethodUtils.invokeClass(clazz, "testMethod", "world");
}

//如果不想将编译的类会缓存到内存，请使用LoaderUtils.compilerOnce方法
void testClassOnce() {
  String javaSourceCode = """
          package cn.wubo.loader.util;
                          
          public class TestClass7 {
                          
              public String testMethod(String name){
                  return String.format("Hello,%s!",name);
              }
          }
          """;
  Class<?> clazz = LoaderUtils.compilerOnce(javaSourceCode, "cn.wubo.loader.util.TestClass7");
  String str = (String) MethodUtils.invokeClass(clazz, "testMethod", "world");
}
```

#### 加载外部jar并执行
```java
void testJarClass() {
  LoaderUtils.addJarPath("./hutool-all-5.8.29.jar");
  Class<?> clazz = LoaderUtils.load("cn.hutool.core.util.IdUtil");
  String str = (String) MethodUtils.invokeClass(clazz, "randomUUID");
}
```

#### 编译Class并加载到Bean
> 使用DynamicBean需要配置@ComponentScan，包括cn.wubo.loader.util.SpringContextUtils文件
```java
void testBean() {
  String javaSourceCode = """
          package cn.wubo.loader.util;
                          
          public class TestClass2 {
                          
              public String testMethod(String name){
                  return String.format("Hello,%s!",name);
              }
          }
          """;
  LoaderUtils.compiler(javaSourceCode, "cn.wubo.loader.util.TestClass2");
  Class<?> clazz = LoaderUtils.load("cn.wubo.loader.util.TestClass2");
  String beanName = LoaderUtils.registerSingleton(clazz);
  String str = MethodUtils.invokeBean(beanName, "testMethod", "world");
}
```

#### 5. DynamicController 动态编译加载Controller并执行
```java
public void loadController() {
  String fullClassName = "cn.wubo.loaderutiltest.DemoController";
  String javaSourceCode = """
          package cn.wubo.loaderutiltest;
                          
          import org.springframework.web.bind.annotation.GetMapping;
          import org.springframework.web.bind.annotation.RequestMapping;
          import org.springframework.web.bind.annotation.RequestParam;
          import org.springframework.web.bind.annotation.RestController;

          @RestController
          @RequestMapping(value = "test")
          public class DemoController {

              @GetMapping(value = "hello")
              public String hello(@RequestParam(value = "name") String name) {
                  return String.format("Hello,%s!",name);
              }
          }
          """;
  LoaderUtils.compiler(javaSourceCode, "cn.wubo.loaderutiltest.DemoController");
  Class<?> clazz = LoaderUtils.load("cn.wubo.loaderutiltest.DemoController");
  String beanName = LoaderUtils.registerController(clazz);
}
```
```http request
GET http://localhost:8080/test/hello?name=world
Accept: application/json

Hello,world!
```

#### 动态增加切面代理
```java
void testAspect() {
  String javaSourceCode = """
          package cn.wubo.loader.util;
                          
          public class TestClass6 {
                          
              public String testMethod(String name){
                  return String.format("Hello,%s!",name);
              }
          }
          """;
  LoaderUtils.compiler(javaSourceCode, "cn.wubo.loader.util.TestClass6");
  Class<?> clazz = LoaderUtils.load("cn.wubo.loader.util.TestClass6");
  try {
    Object obj = MethodUtils.proxy(clazz.newInstance());
    String str = MethodUtils.invokeClass(obj, "testMethod", "world");
  } catch (InstantiationException | IllegalAccessException e) {
    throw new RuntimeException(e);
  }
}
```
输出示例
```text
2023-04-08 21:22:14.174  INFO 32660 --- [nio-8080-exec-1] cn.wubo.loader.util.aspect.SimpleAspect  : SimpleAspect before cn.wubo.loader.util.TestClass testMethod
2023-04-08 21:22:14.175  INFO 32660 --- [nio-8080-exec-1] cn.wubo.loader.util.aspect.SimpleAspect  : SimpleAspect after cn.wubo.loader.util.TestClass testMethod
2023-04-08 21:22:14.175  INFO 32660 --- [nio-8080-exec-1] cn.wubo.loader.util.aspect.SimpleAspect  : StopWatch 'cn.wubo.loader.util.TestClass testMethod': running time = 65800 ns
```

可以通过继承IAspect接口实现自定义切面，并通过MethodUtils.proxy(Class<?> clazz, Class<? extends IAspect> aspectClass)方法调用切面

## 如何在服务器上运行
因为本地和服务器的差异导致classpath路径不同，  
进而使服务上动态编译class时会发生找不到import类的异常，  
因此需要对maven编译配置和启动命令做出一定的修改  
### 1. maven编译配置增加如下部分
```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifest>
                            <!-- 是否要把第三方jar加入到类构建路径 -->
                            <addClasspath>true</addClasspath>
                            <!-- 外部依赖jar包的最终位置 -->
                            <classpathPrefix>lib/</classpathPrefix>
                            <!--指定jar程序入口-->
                            <mainClass>cn.wubo.loaderutiltest.LoaderUtilTestApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <!-- lib依赖包输出目录，打包的时候不打进jar包里 -->
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <excludeTransitive>false</excludeTransitive>
                            <stripVersion>false</stripVersion>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
### 2. 执行编译命令，会在jar包的同级目录下生成lib文件夹存放依赖包
![img.png](img.png)
### 3. 将jar包和lib文件夹上到服务器，并在启动命令中增加`-Dloader.path=lib/`
```shell
java -jar -Dloader.path=lib/ loader-util-test-0.0.1-SNAPSHOT.jar
```

## 注意说明
```text
如果编译报错： Can't initialize javac processor due to (most likely) a class loader problem: java.lang.NoClassDefFoundError: com/sun/tools/javac/processing/JavacProcessingEnvironment
```
####

这是因为JAVA编译器是通过JavaFileManager来加载相关依赖类的，而JavaFileManager来自tools.jar。

解决办法： 
- **idea启动的话**，打开Project Strcutre，添加tools.jar
  ![img.png](img.png)
- 服务器启动，跑jar包的时候需要加入`-Xbootclasspath/a:$toolspath/tools.jar`参数,nohup java -Xbootclasspath/a:$toolspath/tools.jar -jar loader-util-test-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

