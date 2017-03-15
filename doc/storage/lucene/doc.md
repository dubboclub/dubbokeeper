```xml
#dubbo应用名称
dubbo.application.name=lucene-monitor
#dubbo应用所有者
dubbo.application.owner=bieber
#dubbo的注册中心地址，保存和被监控应用一个注册中心
dubbo.registry.address=zookeeper://localhost:2181
#监控数据收集协议
dubbo.protocol.name=dubbo
#监控数据收集协议端口
dubbo.protocol.port=20884

#被监控端同步监控数据周期时间，可不配置，默认是一分钟同步一次
monitor.collect.interval=100
#use netty4
dubbo.provider.transporter=netty4

#monitor lucene config
#lucene存储数据的方式,当前支持mmap(存储在内存里面),niofs（nio的方式存储在磁盘）,simple（最简单的io方式存储）
monitor.lucene.directory.type=niofs
#lucene存储索引文件的目录
monitor.lucene.directory=/monitor
#lucene提交的平率，表示没接受几条数据提交一次（lucene只有提交过的数据，才能查询得到）
monitor.lucene.commit.frequency=10

```
