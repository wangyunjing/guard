# 简介
> 通过Guard集群来监控应用，主要用来监控应用出现故障后，自动重启，保证应用的可用性。
> 使用paxos来实现一致性

# 基础配置
## 通过脚本建立数据库
- `sql/guard_application.sql`
- `sql/guard_instance.sql`
## application
> 应用的配置：一个应用对于多个实例
## instance
> 实例的配置: `ip + ":" + port = instanceId`

## 配置数据库
根据`DataSourceProperties`配置连接数据库的参数

# 非集群模式
设置 `guard.whetherCluster=false`

# 集群模式
## 设置集群模式
`guard.whetherCluster=true`
## 设置集群应用的ID
`guard.clusterApplicationId=1`
## 设置paxos实例的个数
目前只支持`guard.paxosInstanceNum=-1`
## 集群的实例数量
必须设置集群的实例数量为奇数

# 界面监控
`http://ip:port`
- ip : 集群的某一个实例的IP
- port : 集群的某一个实例的Port
