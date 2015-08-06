# 调度系统重构详细设计文档

## 一、模块设计

### 1.1 scheduler模块设计

调度器模块总体分为TimeScheduler，DAGScheduler和TaskScheduler

![调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/core_scheduler.png)

如上图所示，TimeScheduler负责进行定时任务的调度，DAGScheduler负责依赖任务的调度，TaskScheduler是真正的调度器，负责执行任务、反馈任务结果和状态。

三个Scheduler协同工作，共同完成调度系统的调度工作。

#### 1.1.1 时间调度器(TimeScheduler)

时间调度器负责调度基于时间触发的任务，支持Cron表达式时间配置。

![时间调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/time_based_scheduler_new.png)

时间调度器从数据库中加载周期性调度任务，或者从Rest API请求增加/修改/删除任务。根据任务配置的调度时间(CronExpression)，调度器定时计算出每个任务后面一段时间内（如：一天）的具体调度时间，并生成调度计划(Schedule Plan)。

- cron analyzer是一个cronTab表达式的语法分析器，输入是一个job，产出是一些task。

- task表会把时间最小的task排在最前面，数据结构上采用堆？

- cron schedule thread是一个线程，不断轮询task表，当满足时间就会提交给TaskScheduler。

#### 1.1.2 依赖调度器(DAGScheduler)

依赖调度器通过观察者模式，以监听事件的方式进行依赖触发等操作  

##### 1.1.2.1 Event设计

![uml_event](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_event.png)

如图所示，Event是一个接口，DAGEvent是一个抽象类，其子类有InitializeEvent,SuccessEvent,ScheduledEvent等。DAGEvent中主要有两个成员，jobid和planid。

##### 1.1.2.2 Observable和Observer设计

![uml_observable](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_DAGScheduler.png)

如图所示，Observable是一个接口，相当于观察者模式中的主题，Listener是一个接口是观察者模式中的观察者。

Observable提供注册、移除、通知观察者的接口。EventBusObservable是一个抽象类，是基于google EventBus观察者模式中的主题。DAGScheduler是具体的实现类。

Listener可以有多种实现，订阅继承Event接口的事件进行处理。

#### 1.1.3 基于依赖策略的调度设计


#### 2.3.4 任务调度器(TaskScheduler)

![uml_TaskScheduler](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_TaskScheduler.png)

![flow_TaskScheduler](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/flow_TaskScheduler.png)



### 1.2 dispatcher模块设计

### 1.3 dao模块设计

### 1.4 service模块设计

### 1.5 job模块设计

## 二、流程设计

### 2.1 提交任务

### 2.2 kill任务

### 2.3 重跑任务

### 2.4 修改任务

### 2.5 升级处理

#### 2.5.1 master升级

#### 2.5.2 worker升级

### 2.6 异常处理

## 三、内部接口设计

Server、Worker、LogServer、RestServer之间的通信均采用Netty、Protocol Buffers。


### 3.1 提交任务

- RestServer -> Server

请求:

| 字段         | 类型     | 必选   | 默认值  | 描述        | 
| :--------- | ------ | ---- | ---- | --------- |
| job_name   | string | T    |      | 任务名称      |
| cron_expression   | string | F    |      | cron表达式，如：0 0 23 * * ?      | 
| dependency_jobids | int32 | F    |      | 依赖任务ID，可以多个      |  
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   | 
| user       | string | T    |      | 提交任务的用户名称 | 
| job_type   | string | T    |      | 任务类型，如：hive、shell、mapreduce      | 
| command    | string | T    |      | 执行命令      | 
| group_id   | int32  | T    |      | Worker组ID | 
| priority   | int32  | F    | 1    | 任务优先级，取值范围1-10。后端执行系统可根据此值映射成自己对应的优先级      | 
| parameters | map | F    |      | 扩展参数，用于支持不同类型任务执行需要的额外参数，如：权限验证等      | 

响应:

| 字段      | 类型     | 必选   | 默认值  | 描述          | 
| ------- | ------ | ---- | ---- | ----------- | 
| job_id  | int64  | F    | -1   | 任务ID        | 
| accept  | bool   | T    |      | 提交的任务是否被接受 | 
| message | string | F    |      | 描述消息，用于说明任务被拒绝的原因。任务被接受时此字段为空            |


- Server -> Worker

请求:

| 字段         | 类型     | 必选   | 默认值  | 描述        | 
| :--------- | ------ | ---- | ---- | --------- |
| job_id   | string | T    |      | 任务名称      |
| job_name   | string | T    |      | 任务名称      | 
| app_name   | string | T    |      | 应用名称，如：XRay      |
| user       | string | T    |      | 提交任务的用户名称 | 
| job_type   | string | T    |      | 任务类型，如：hive、shell、mapreduce      | 
| command    | string | T    |      | 执行命令      |
| priority   | int32  | F    | 1    | 任务优先级，取值范围1-10。后端执行系统可根据此值映射成自己对应的优先级      | 
| parameters | map | F    |      | 扩展参数，用于支持不同类型任务执行需要的额外参数，如：权限验证等      | 

响应:

| 字段      | 类型     | 必选   | 默认值  | 描述          | 
| ------- | ------ | ---- | ---- | ----------- | 
| job_id  | int64  | F    | -1   | 任务ID        | 
| accept  | bool   | T    |      | 提交的任务是否被接受 | 
| message | string | F    |      | 描述消息，用于说明任务被拒绝的原因。任务被接受时此字段为空            |

### 3.2 终止任务

- RestServer -> Server

请求：

| 字段     | 类型    | 必选   | 默认值  | 描述   | 
| ------ | ----- | ---- | ---- | ---- | 
| job_id | int64 | T    |      | 任务ID | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否终止成功 | 

- Server -> Worker

请求：

| 字段     | 类型    | 必选   | 默认值  | 描述   | 
| ------ | ----- | ---- | ---- | ---- | 
| job_id | int64 | T    |      | 任务ID | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否终止成功 | 


### 3.3 任务状态汇报

- Worker -> Server

请求：

| 字段        | 类型    | 必选   | 默认值  | 描述   | 
| --------- | ----- | ---- | ---- | ---- | 
| job_id    | int64 | T    |      | 任务ID | 
| status    | int32 | F    | -1   | 状态   | 
| timestamp | int64 | F    | 0    | 时间戳  | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.4 日志写入

- Worker -> LogServer

请求：

| 字段     | 类型     | 必选   | 默认值  | 描述                 | 
| ------ | ------ | ---- | ---- | ------------------ | 
| job_id | int64  | T    |      | 任务ID               | 
| log    | string | T    |      | 日志内容               | 
| type   | int32  | T    |      | 日志类型：1-stdout、2-stderr | 
| is_end | bool   | T    |      | 日志写请求是否结束          | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.5 日志读取

- RestServer -> LogServer

请求:

| 字段     | 类型    | 必选   | 默认值  | 描述                 | 
| ------ | ----- | ---- | ---- | ------------------ | 
| job_id | int64 | T    |      | 任务ID               | 
| type   | int32 | T    |      | 日志类型：stdout、stderr | 
| offset | int64 | F    | 0    | 日志内容的字节偏移量         | 
| lines  | int32 | F    | 100  | 日志读取的行数            | 

响应:

| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| is_end | bool   | T    |      | 是否请求成功       | 
| log    | string | F    |      | 日志内容         | 
| offset | int64  | T    |      | 当前日志内容的字节偏移量 | 


### 3.6 Worker注册

- Worker -> Server

请求：

| 字段       | 类型     | 必选   | 默认值  | 描述          | 
| -------- | ------ | ---- | ---- | ----------- | 
| key      | string | T    |      | Worker授权Key | 
| group_id | int32  | T    |      | Worker组ID   |

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否注册成功 | 

### 3.7 Worker心跳汇报

- Worker -> Server

请求：

| 字段       | 类型     | 必选   | 默认值  | 描述          | 
| -------- | ------ | ---- | ---- | ----------- |
| job_num  | int32  | T    |      | 正在运行的任务数    | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.8 Worker上下线

- RestServer -> Worker

请求：

| 字段      | 类型     | 必选   | 默认值  | 描述                  | 
| ------- | ------ | ---- | ---- | ------------------- | 
| ip      | string | T    |      | Worker授权Key         | 
| port    | int32  | T    |      | Worker组ID           | 
| offline | bool   | T    |      | 状态：True-下线，False-上线 | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.9 任务状态查询

- RestServer -> Server

请求：

| 字段      | 类型     | 必选   | 默认值  | 描述                  | 
| ------- | ------ | ---- | ---- | ------------------- | 
| job_id | int64   | T    |      | 任务ID | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| status | int32 | T    |      | 状态 |

## 四、外部接口设计

RestServer对外提供REST API。


### 4.1 提交任务

接口：/api/job/submit

Method：POST

| 字段       | 类型     | 必选   | 默认值  | 描述        | 
| -------- | ------ | ---- | ---- | --------- | 
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   | 
| job_name   | string | T    |      | 任务名称      |
| cron_expression   | string | F    |      | cron表达式，如：0 0 23 * * ?      | 
| dependency_jobids | int32 | F    |      | 依赖任务ID，可以多个      |  
| user       | string | T    |      | 提交任务的用户名称 | 
| job_type   | string | T    |      | 任务类型，如：hive、shell、mapreduce      | 
| command    | string | T    |      | 执行命令      | 
| group_id   | int32  | T    |      | Worker组ID | 
| priority   | int32  | F    | 1    | 任务优先级，取值范围1-10。后端执行系统可根据此值映射成自己对应的优先级      | 
| parameters | map | F    |      | 扩展参数，用于支持不同类型任务执行需要的额外参数，如：权限验证等      | 

响应:

| 字段      | 类型     | 必选   | 默认值  | 描述          | 
| ------- | ------ | ---- | ---- | ----------- | 
| job_id  | int64  | F    | -1   | 任务ID        | 
| accept  | bool   | T    |      | 提交的任务是否被接受 | 
| message | string | F    |      | 描述消息，用于说明任务被拒绝的原因。任务被接受时此字段为空            |

### 4.2 读取日志

接口：/api/log

Method：GET

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   | 
| job_id | int64 | T    |      | 任务ID               | 
| type   | int32 | T    |      | 日志类型：stdout、stderr | 
| offset | int64 | F    | 0    | 日志内容的字节偏移量         | 
| lines  | int32 | F    | 100  | 日志读取的行数            |

响应:

| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| job_id | int64 | T    |      | 任务ID               | 
| type   | int32 | T    |      | 日志类型：stdout、stderr | 
| is_end | bool   | T    |      | 是否请求成功       | 
| log    | string | F    |      | 日志内容         | 
| offset | int64  | T    |      | 当前日志内容的字节偏移量 |

### 4.3 下载日志

接口：/api/log/download

Method：GET

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ |
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   |
| job_id | long   | T    |      | 任务ID   |
| type   | int32 | T    |      | 日志类型：stdout、stderr | 

响应：文件

### 4.4 获取任务状态

接口：/api/job/status

Method：GET

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ |
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   |
| job_id | long   | T    |      | 任务ID   | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| job_id | long   | T    |      | 任务ID   | 
| status | int32 | T    |      | 状态 |

### 4.5 终止任务

接口：/api/job/kill

Method：DELETE

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ |
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   | 
| job_id | long   | T    |      | 任务ID   | 

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| job_id | long   | T    |      | 任务ID   | 
| success | bool | T    |      | 是否终止成功 | 


### 4.6 Worker上下线

接口：/api/worker/status

Method：POST

| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ |
| app_name   | string | T    |      | 应用名称，如：XRay      | 
| app_key    | string | T    |      | 应用授权Key   | 
| ip     | string | T    |      | Worker IP地址  | 
| port   | int    | T    |      | Worker 端口    | 
| status | int    | T    |      | 状态：1-上线，0-下线 |

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ |
| ip     | string | T    |      | Worker IP地址  | 
| port   | int    | T    |      | Worker 端口    | 
| success | bool | T    |      | 是否请求成功 | 

## 五、表结构设计
