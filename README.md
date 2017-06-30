# springboot-starter-rocketmq
spring boot 集成 RocketMq消息中间件

### Start broker
* Unix platform

  `nohup sh mqbroker &`

### Shutdown broker
  sh mqshutdown broker
  sh mqadmin wipeWritePerm -b brokerN -n namesrvA（建议使用这种）

### Start Nameserver
* Unix platform

  `nohup sh mqnamesrv &`

### Shutdown Nameserver
    sh mqshutdown namesrv

###根据配置文件开启broker
`nohup sh mqbroker -c （filepath）`

