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

##规划中的功能
###Dubbo服务监控
> 通过扩展dubbo提供的MonitorService接口来实现对服务的监控，由于官方版本的监控功能比较局限，所以将通过手机各方意见重新开发一套监控系统，从而可以实时对服务可控。

###服务调用Trace
> 将基于dubbo的框架可扩展性，对服务的调用过程进行跟踪，并且汇总分析，通过形象的方式呈现服务调用过程，从而帮助对服务问题进行定位。具体将参考谷歌的`Depper`。

###其他信息
> 预览地址：http://dk.dubboclub.net/dubbokeeper



> 可以通过git clone https://github.com/dubboclub/dubbokeeper.git
> 到项目根目录下面执行mvn install 在dubbokeeper-ui的target中将会有war包，将war中的classes/dubbo.properties的`dubbo.registry.address`改成你系统的注册中心地址即可。

##系统界面预览

