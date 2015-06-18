服务框架提供的 RPC 服务是将在服务器端发布的服务通过远程调用协议提供给用户使用。用户按要求的协议调用服务并给出相应的参数，服务器端完成运算逻辑后，数据通过网络按照约定的协议传输的调用方。以下文档提供给 Java 应用开发者:

## 1. 添加辅助包到项目中

在项目的 Build Path 中添加以下 Jar 包。下载以下一组 Jar 包，并通过 IDE 添加到项目中。使用构建工具(Maven)的项目，可将 Jar 对应的依赖的信息复制到项目的构建配置中。

|包 | Maven dependency | 其他构建工具|
|---| ---------------- | ----------|
{{#libsNeeded}}
|`{{groupId}}/{{artifactId}}/{{version}}` | `<dependency><groupId>{{groupId}}</groupId><artifactId>{{artifactId}}</artifactId><version>{{version}}</version></dependency>` | <a href="http://mvnrepository.com/artifact/{{groupId}}/{{artifactId}}/{{version}}">前往 mvnrepository 查看</a>|
{{/libsNeeded}}

**注意**: 

1. 服务的接口包需向服务提供者索取。
2. 部分 Jar 包可能与项目已使用的包(比如 Spring)有版本冲突，用户可自行调整测试后决定采用何种版本。

## 2. 在项目中添加服务调用配置

通过 <a href="http://spring.io" target="_blank">`Spring`</a> 的 <a href="http://docs.spring.io/spring/docs/2.0.x/api/org/springframework/context/ApplicationContext.html" target="_blank">`ApplicationContext`</a> 来完成服务调用的配置，需要在项目的 class path 中添加配置文件，如 `consumer.xml`，内容样例如下，你可以复制到配置文件中。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
	
	<!--修改为自己的项目名称 -->
	<dubbo:application name="{{demoBeanName }}-demo" />
	
	<!--zookeeper注册中心 -->
	<dubbo:registry protocol="zookeeper" address="{{{registry}}}" />

	<dubbo:reference id="{{demoBeanName }}" interface="{{serviceName}}" timeout="30000" />
	
	<dubbo:monitor protocol="registry" />
</beans>
```

## 3. 获取服务对象

使用 `Spring` 的 `ApplicationContext` 加载机制，通过加载之前创建的配置文件，可以获得服务调用对象，示例代码如下

```java
	// ApplicationContext 的创建
	ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("consumer.xml");
	classPathXmlApplicationContext.start();

	// 服务调用对象的获取
	{{demoBeanClassName}} {{demoBeanName }} = ({{demoBeanClassName}}) classPathXmlApplicationContext.getBean("{{demoBeanName }}");

```
这段代码中 `classPathXmlApplicationContext` 的创建开销较大，建议缓存此对象供之后多次使用。

## 4. 调用具体的服务方法

示例代码如下

```java
{{demoBeanName}}.{{demoMethod}}(arg1, arg2 ...);
```

关于网络
------

用户的应用需要能够访问服务部署的网络。参考的测试方法如下：

```shell
{{#providers}}
telnet {{address}}
{{/providers}}
```

