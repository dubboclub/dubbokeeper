### 初始化数据库

既然是基于mysql存储的，肯定第一步是创建一个数据库，数据库名可以自定义一个，编码采用utf-8

创建完数据库之后，执行[数据库表初始化脚本](sql/application.sql)

### 调整配置参数

```xml
#dubbo应用名称
dubbo.application.name=mysql-monitor
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
#监控数据持久化周期,默认是一分钟,单位是秒
monitor.write.interval=6000

#mysql相关信息
#mysql数据库地址
dubbo.monitor.mysql.url=jdbc:mysql://localhost:3306/dubbokeeper
#mysql数据库用户名
dubbo.monitor.mysql.username=root
#mysql数据库用户密码
dubbo.monitor.mysql.password=root
#mysql数据库链接池最大连接数
dubbo.monitor.mysql.pool.max=10
#mysql数据库链接池最小连接数
dubbo.monitor.mysql.pool.min=10
```

