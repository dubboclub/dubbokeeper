# Dubbokeeper
##Dubbokeeper是什么
dubbokeeper是一个开源版本基于spring mvc开发的社区版dubboadmin，同时修复了官方admin存在的一些问题，以及添加了一下必要的功能
例如服务统计，依赖关系等图表展示功能，当前dubbokeeper还属于开发阶段。最终dubbokeeper会集成服务管理以及服务监控一体的DUBBO服务管理系统
##Dubbokeeper包含哪些功能
###应用管理
> 包含当前系统所有子应用信息列表，以及各个引用服务引用消费列表信息，同时可以对某个应用的服务进行上下线操作，权重调整等，同时可以查看应用部署实体信息列表。目的是实现服务的可管理

###动态配置
> 这是利用dubbo基于注册中心来管理服务实现对服务的参数在服务运行状态进行调整，比如对服务的超时时间调整，对服务的mock以及可以添加自定义参数的调整等，目的是服务的可配置。

###统计信息
> 除了对服务的可管理，可配置。同时通过图表的方式对服务相关信息进行统计，例如对整体系统的应用暴露，消费服务统计信息，应用依赖关系图信息等

###Dubbo服务监控
> 通过扩展dubbo提供的MonitorService接口来实现对服务的监控，由于官方版本的监控功能比较局限，所以将通过手机各方意见重新开发一套监控系统，从而可以实时对服务可控。

###zookeeper信息查看
> 通过配置zookeeper地址，可以查看多个zookeeper中的节点信息



##规划中的功能

###服务调用Trace
> 将基于dubbo的框架可扩展性，对服务的调用过程进行跟踪，并且汇总分析，通过形象的方式呈现服务调用过程，从而帮助对服务问题进行定位。具体将参考谷歌的`Depper`。
###Zookeeper
> 基于zookeeper的四字命令完成对zookeeper的基本监控

##部署过程：
> 1、下载源码

`git clone https://github.com/dubboclub/dubbokeeper.git `

>2、编译打包

在刚刚clone下来的文件夹内执行`mvn install -Dmaven.test.skip`

>3、调整配置

在`dubbokeeper-ui/target`下面会有`dubbokeeper-ui-${version}`目录，该目录就是编译后的部署结果（当然在`target`下面的dubbokeeper-ui-${version}.war也可以）

调整`dubbokeeper-ui-${version}/WEB-INF/classes/dubbo.properties`文件中的配置项。

```xml
#monitor的应用名，可根据自己情况自定义
dubbo.application.name=common-monitor
#应用的拥有者
dubbo.application.owner=bieber
#连接的dubbo注册中心地址
dubbo.registry.address=zookeeper://localhost:2181
#暴露MonitorServer接口的协议
dubbo.protocol.name=dubbo
#暴露MonitorServer接口的端口
dubbo.protocol.port=20884

#peeper config
#监控的zookeeper连接列表，多个通过‘,’(英文逗号)隔开。
peeper.zookeepers=localhost:2181
#监控的zookeeper连接会话超时时间
peeper.zookeeper.session.timeout=60000

#monitor lucene config
#被监控端同步监控数据周期时间，可不配置，默认是一分钟同步一次
monitor.collect.interval=60000
#使用lucene刷盘类型，默认是niofs，支持`niofs`,`simple`,`mmap`。
monitor.lucene.directory.type=niofs
#lucene存储监控数据的位置
monitor.lucene.directory=/usr/dev/op_disk/monitor-data
#lucene刷盘的频率，表示接受某个被监控端的监控数据多少条之后，对lucene进行异常刷盘，必须配置
monitor.lucene.commit.frequency=10

#logger
#dubbokeeper的日志目录
monitor.log.home=/usr/dev/op_disk/monitor-log
```

>4、部署&启动
将上面的`dubbokeeper-ui-${version}`整个目录拷贝到tomcat或者其他servlet容器下面，然后启动容器即可。


###其他信息
> 预览地址：http://dk.dubboclub.net/dubbokeeper




