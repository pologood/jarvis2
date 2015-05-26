# 调度系统重构项目

# 总体介绍

## 业务场景

数据平台核心作业调度管理系统


## 核心需求

* 支持多种不同类型的作业调度任务, 当前主要目标:
  * 高优先 : Hive / MR / Java / Shell
  * 低优先 : SSH / Python (可以考虑用通用方式处理?)

* 自动化动态部署作业运行环境
  * 通过参数配置,后台管理等形式, 使用户能动态的集中管理和部署作业运行所需环境, 无需在集群上提前静态部署相关环境.
  
* 用户权限管理
  * 对用户权限能方便的进行管理, 包括用户对作业相关的操作权限, 对资源的使用权限等

* 灵活的外部接入方式
  * 需要提供统一的业务接入模式和编程接口

* 监控
  * 需要提供作业运行状态和集群本身的监控


