[![Build Status](https://travis-ci.org/dubboclub/dubbokeeper.svg?branch=master)](https://travis-ci.org/dubboclub/dubbokeeper)
[![Latest Version](http://img.shields.io/badge/latest-1.0.1-brightgreen.svg)](https://github.com/dubboclub/dubbokeeper/releases/tag/dubbokeeper-1.0.1)
[![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/dubboclub/dubbokeeper/blob/master/LICENSE)

# Dubbokeeper

## Dubbokeeper是什么
dubbokeeper是一个开源版本基于spring mvc开发的社区版dubboadmin，同时修复了官方admin存在的一些问题，以及添加了一下必要的功能
例如服务统计，依赖关系等图表展示功能，当前dubbokeeper还属于开发阶段。最终dubbokeeper会集成服务管理以及服务监控一体的DUBBO服务管理系统

## Dubbokeeper包含哪些功能
### 应用管理
> 包含当前系统所有子应用信息列表，以及各个引用服务引用消费列表信息，同时可以对某个应用的服务进行上下线操作，权重调整等，同时可以查看应用部署实体信息列表。目的是实现服务的可管理

### 动态配置
> 这是利用dubbo基于注册中心来管理服务实现对服务的参数在服务运行状态进行调整，比如对服务的超时时间调整，对服务的mock以及可以添加自定义参数的调整等，目的是服务的可配置。

### 统计信息
> 除了对服务的可管理，可配置。同时通过图表的方式对服务相关信息进行统计，例如对整体系统的应用暴露，消费服务统计信息，应用依赖关系图信息等

### Dubbo服务监控
> 通过扩展dubbo提供的MonitorService接口来实现对服务的监控，由于官方版本的监控功能比较局限，所以将通过收集各方意见重新开发一套监控系统，从而可以实时对服务可控。

### zookeeper信息查看
> 通过配置zookeeper地址，可以查看多个zookeeper中的节点信息

## 1.0.1版本变动内容

dubbokeeper在1.0.1版本对监控数据存储模块抽离出来，做为单独的应用部署，而不是和1.0.0版本和前端展示集成在一个应用里面

在1.0.0版本中暂时提供了mysql以及1.0.0中已有的lucene存储

这样做的目的是使得dubbokeeper的监控数据收集端能够集群部署（使用lucene，将不支持集群部署,如果使用lucene存储，需要jdk1.7+）

所以1.0.1以后的版本，对dubbokeeper的部署将包含两部分，一部分是监控数据存储端以及另一部分是监控数据展示的部署

## 规划中的功能

### 服务调用Trace
> 将基于dubbo的框架可扩展性，对服务的调用过程进行跟踪，并且汇总分析，通过形象的方式呈现服务调用过程，从而帮助对服务问题进行定位。具体将参考谷歌的[Dapper](https://github.com/sdcuike/DistributedTracingSystem/blob/master/dapper-2010-1.pdf
)。
### Zookeeper
> 基于zookeeper的四字命令完成对zookeeper的基本监控

## 部署过程：
> 1、下载源码

`git clone https://github.com/dubboclub/dubbokeeper.git `

> 2、编译打包

由于监控数据的存储和展示显示进行了分离，那么打包有所变动。在下载源码的根目录会发现install-xxx.bat(sh),这个可以根据你想要的不同存储执行对应的脚本。

执行完之后在`target`目录下面会发现`xxx-dubbokeeper-ui`,`xxx-dubbokeeper-server`以及`xxx-dubbokeeper-server.tar.gz`

其中`xxx-dubbokeeper-ui`下会有一个war包，将该war包部署到tomcat或者jetty里面（或者其他servlet容器），那么就部署好了监控展示应用了


> 3、监控数据暂时端调整配置

对上面的war包解压出来后对其中`WEB-INF/classes/dubbo.properties`文件中的配置项进行调整。

```xml
#monitor的应用名，可根据自己情况自定义
dubbo.application.name=monitor-ui
#应用的拥有者
dubbo.application.owner=bieber
#连接的dubbo注册中心地址，保持部署监控数据存储的zk地址一样
dubbo.registry.address=zookeeper://localhost:2181

#use netty4
dubbo.reference.client=netty4

#peeper config
#监控的zookeeper连接列表，多个通过‘,’(英文逗号)隔开。
peeper.zookeepers=localhost:2181
#监控的zookeeper连接会话超时时间
peeper.zookeeper.session.timeout=60000

#被监控端同步监控数据周期时间，可不配置，默认是一分钟同步一次
monitor.collect.interval=60000

#logger
#dubbokeeper的日志目录
monitor.log.home=/usr/dev/op_disk/monitor-log
```

> 4、监控数据存储端配置调整以及启动

通过上面编译后会得到`xxx-dubbokeeper-server`目录，在改名了的`xxx-server`下面包含三个子目录`bin`,`conf`以及`lib`。

`bin`:启动存储端的脚本，实行`start-xx.sh(bat)`则启动该应用<br>

`conf`:存储端的相关配置，具体配置下面会介绍<br>

`lib`:应用依赖的相关jar包<br>


下面列出不同存储的对配置的相关描述

[lucene存储配置介绍](doc/storage/lucene/doc.md)

[mysql存储配置介绍](doc/storage/mysql/doc.md)

[mongodb存储配置介绍](doc/storage/mongodb/doc.md)



按照上面对应存储配置完毕之后，执行`start-xx.sh(bat)`，便启动了dk的存储



