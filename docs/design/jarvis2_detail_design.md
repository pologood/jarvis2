# 调度系统重构详细设计文档

## 一、模块设计

### 1.1 scheduler模块设计

调度器模块总体分为TimeScheduler，DAGScheduler和TaskScheduler

![调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/core_scheduler.png)

如上图所示，TimeScheduler负责进行定时任务的调度，DAGScheduler负责依赖任务的调度，TaskScheduler是真正的调度器，负责执行任务、反馈任务结果和状态。

三个Scheduler协同工作，共同完成调度系统的调度工作。

#### 2.3.2 时间调度器(TimeScheduler)

时间调度器负责调度基于时间触发的任务，支持Cron表达式时间配置。

![时间调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/time_based_scheduler_new.png)

时间调度器从数据库中加载周期性调度任务，或者从Rest API请求增加/修改/删除任务。根据任务配置的调度时间(CronExpression)，调度器定时计算出每个任务后面一段时间内（如：一天）的具体调度时间，并生成调度计划(Schedule Plan)。

- cron analyzer是一个cronTab表达式的语法分析器，输入是一个job，产出是一些task。

- task表会把时间最小的task排在最前面，数据结构上采用堆？

- cron schedule thread是一个线程，不断轮询task表，当满足时间就会提交给TaskScheduler。

#### 2.3.3 依赖调度器(DAGScheduler)

依赖调度器通过观察者模式，以监听事件的方式进行依赖触发等操作  

#### 2.3.3.1 Event设计

![mvc事件](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_event.png)

如图所示，Event是一个接口，DAGEvent是一个抽象类，其子类有InitializeEvent,SuccessEvent,ScheduledEvent等。DAGEvent中主要有两个成员，jobid和planid。

#### 2.3.3.2 Observable和Observer设计

![mvc事件](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_DAGScheduler.png)

如图所示，Observable是一个接口，相当于观察者模式中的主题，Listener是一个接口是观察者模式中的观察者。

Observable提供注册、移除、通知观察者的接口。EventBusObservable是一个抽象类，是基于google EventBus观察者模式中的主题。DAGScheduler是具体的实现类。

Listener可以有多种实现，订阅继承Event接口的事件进行处理。

#### 2.3.3.3 基于依赖策略的调度设计


#### 2.3.4 任务调度器(TaskScheduler)


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

Server、Worker、LogServer、RestServer之间的通信接口均采用Protocol Buffers。


### 3.1 提交任务(Server -> Worker)

Server请求

| 字段         | 类型     | 必选   | 默认值  | 描述        | 
| :--------- | ------ | ---- | ---- | --------- | 
| job_id     | int64  | T    |      | 任务ID      | 
| job_name   | string | T    |      | 任务名称      | 
| app_name   | string | T    |      | 应用名称      | 
| app_key    | string | T    |      | 应用授权Key   | 
| user       | string | T    |      | 用户名       | 
| job_type   | string | T    |      | 任务类型      | 
| command    | string | T    |      | 执行命令      | 
| group_id   | int32  | T    |      | Worker组ID | 
| priority   | int32  | F    | 1    | 优先级       | 
| parameters | string | F    |      | 扩展参数      | 

Worker响应

| 字段      | 类型     | 必选   | 默认值  | 描述          | 
| ------- | ------ | ---- | ---- | ----------- | 
| job_id  | int64  | F    | -1   | 任务ID        | 
| accept  | bool   | T    |      | 是否被Worker接受 | 
| message | string | F    |      |             | 

### 3.2 终止任务(Server -> Worker)

Server请求

| 字段     | 类型    | 必选   | 默认值  | 描述   | 
| ------ | ----- | ---- | ---- | ---- | 
| job_id | int64 | T    |      | 任务ID | 

Worker响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否终止成功 | 

### 3.3 任务状态汇报(Worker -> Server)

Worker请求

| 字段        | 类型    | 必选   | 默认值  | 描述   | 
| --------- | ----- | ---- | ---- | ---- | 
| job_id    | int64 | T    |      | 任务ID | 
| status    | int32 | F    | -1   | 状态   | 
| timestamp | int64 | F    | 0    | 时间戳  | 

Server响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.4 日志写入(Worker -> LogServer)

Worker请求

| 字段     | 类型     | 必选   | 默认值  | 描述                 | 
| ------ | ------ | ---- | ---- | ------------------ | 
| job_id | int64  | T    |      | 任务ID               | 
| log    | string | T    |      | 日志内容               | 
| type   | int32  | T    |      | 日志类型：stdout、stderr | 
| is_end | bool   | T    |      | 日志写请求是否结束          | 

LogServer响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.5 日志读取(RestServer -> LogServer)

RestServer请求

| 字段     | 类型    | 必选   | 默认值  | 描述                 | 
| ------ | ----- | ---- | ---- | ------------------ | 
| job_id | int64 | T    |      | 任务ID               | 
| type   | int32 | T    |      | 日志类型：stdout、stderr | 
| offset | int64 | F    | 0    | 日志内容的字节偏移量         | 
| lines  | int32 | F    | 100  | 日志读取的行数            | 

LogServer响应

| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| is_end | bool   | T    |      | 是否请求成功       | 
| log    | string | F    |      | 日志内容         | 
| offset | int64  | T    |      | 当前日志内容的字节偏移量 | 

### 3.6 心跳汇报(Worker -> Server)

Worker请求

| 字段       | 类型     | 必选   | 默认值  | 描述          | 
| -------- | ------ | ---- | ---- | ----------- | 
| key      | string | T    |      | Worker授权Key | 
| group_id | int32  | T    |      | Worker组ID   | 
| job_num  | int32  | T    |      | 正在运行的任务数    | 

Server响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

### 3.7 Worker上下线(RestServer -> Worker)

RestServer请求

| 字段      | 类型     | 必选   | 默认值  | 描述                  | 
| ------- | ------ | ---- | ---- | ------------------- | 
| ip      | string | T    |      | Worker授权Key         | 
| port    | int32  | T    |      | Worker组ID           | 
| offline | bool   | T    |      | 状态：True-下线，False-上线 | 

Worker响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 | 

## 四、外部接口设计

RestServer对外提供REST API。


### 4.1 执行任务

接口：/server/execute

Method：POST

| 字段       | 类型     | 必选   | 默认值  | 描述        | 
| -------- | ------ | ---- | ---- | --------- | 
| token    | string | T    |      | 接口调用口令    | 
| time     | long   | T    |      | 时间戳       | 
| name     | string | T    |      | 应用名称      | 
| content  | string | T    |      | 执行命令      | 
| jobName  | string | T    |      | 任务名称      | 
| executor | string | T    |      | 用户名称      | 
| jobType  | string | F    | hive | 任务类型      | 
| groupId  | int    | F    | 1    | Worker组ID | 

### 4.2 获取日志

接口：/server/querylog

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

### 4.3 获取任务状态

接口：/server/jobstatus

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

### 4.4 获取查询结果

接口：/server/result

Method：POST

| 字段     | 类型     | 必选   | 默认值   | 描述     | 
| ------ | ------ | ---- | ----- | ------ | 
| token  | string | T    |       | 接口调用口令 | 
| time   | long   | T    |       | 时间戳    | 
| name   | string | T    |       | 应用名称   | 
| jobId  | long   | T    |       | 任务ID   | 
| offset | long   | F    | 0     | 字节偏移量  | 
| lines  | int    | F    | 10000 | 日志读取行数 | 

### 4.5 终止任务

接口：/server/killjob

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

### 4.6 下载查询结果

接口：/server/result/download

Method：GET

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

### 4.7 Worker上下线

接口：/server/clientstatus

Method：POST

| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| token  | string | T    |      | 接口调用口令       | 
| time   | long   | T    |      | 时间戳          | 
| name   | string | T    |      | 应用名称         | 
| ip     | string | T    |      | Worker IP地址  | 
| port   | int    | T    |      | Worker 端口    | 
| status | int    | T    |      | 状态：1-上线，0-下线 |

## 五、表结构设计
