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

- 调度计划中的任务按照调度时间升序排序，调度器依次轮询检查任务的调度时间是否到达，到达调度时间的任务将被触发以等待执行

- 当任务的调度时间被修改时，调度器将从调度计划中删除该任务已生成的计划，然后重新生成新的调度计划

- 为使调度计划可恢复，调度计划存储于数据库中。


#### 2.3.3 依赖调度器(DAGScheduler)

依赖调度器通过观察者模式，以监听事件的方式进行依赖触发等操作  

- JobListener是一个job的监听器，用来监听一个job的事件，内部维护了job的依赖关系等原信息。当前支持两种listener: DAGListener和TimeDAGListener。

> DAGListener处理只有DAG依赖关系的任务的调度

> TimeDAGListener处理既有时间依赖，又有任务依赖的任务的调度。

- Observer是一个单例，负责添加、删除jobListener，以及通知事件给jobListener。observer只维护准备进入调度的依赖任务。

![依赖调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/dependency_based_scheduler_new.png)

如上图所示

- DAG依赖任务调度：

当TimeScheduler提交任务给TaskScheduler之后，TaskScheduler会获取当前任务的后置任务，把后置任务注册到DAGScheduler中的observer中，并且发送InitializeEvent。当某个jobListener监听到这个事件后，会把依赖状态置为false。

当某个任务执行完成，TaskScheduler中的statusManager会发送successEvent给DAGScheduler。jobListener监听到这个事件后，判断它是否是自己需要的前置依赖，如果是则更新前置依赖状态为true。当前置依赖全部完成了，提交任务到TaskScheduler中。

当依赖任务提交到TaskScheduler中后，就会开始调度，当任务调度起来之后，进入postSchedule，会发送scheduledEvent给DAGScheduler，具体的某一个jobListener监听到这个事件的jobid和自己一样，就会把自己从observer中注销掉。同时jobDispatcher还会去获取这个任务的后置任务，把后置任务注册到DAGScheduler中的observer中。

如果修改了依赖关系，外部会发送modifyEvent给DAGScheduler，observer会通知所有的jobListener检查下自己的依赖，如果满足，向TaskScheduler提交任务。

TaskManager中的statusManager自己处理失败重试策略

- 混合依赖（时间依赖和DAG依赖）任务的调度：

TimeDAGListener比DAGListener多监听一个事件，即TimeReadyEvent。由于混合依赖的不确定性，一开始可能由前置依赖或者TimeScheduler把它加入到DAGScheduler中。

如果是由TimeScheduler先提交给TaskScheduler，在preSchedule中，会检查任务类型，如果是时间+DAG依赖，则检查job信息中的依赖标志位是否为true，如果为true，则进入jobDispatcher进行调度；如果为false，则把自己注册到DAGScheduler中，并发送TimeReadyEvent。

TimeDAGListener收到前置依赖的successEvent，会更新依赖状态。收到TimeReadyEvent，会更新timeReady标志位。每次收到这两个事件后，都会做依赖检查，这里要检查前置依赖和时间标志位是否都满足。如果都满足了，更新job元信息中的依赖标志位，并且向TaskScheduler提交任务。

TimeDAGListener收到scheduledEvent之后，不但要把自己从observer中注销掉，还要把job元信息中的依赖标志位复位为false。

- 支持不通周期依赖策略的调度：

DAG依赖任务的前置依赖可能有不同的周期，现在有三种依赖策略：ANYONE, LASTONE, ALL。比如c依赖于a和b，a一小时跑4次，b一小时跑1次。

ANYONE表示b成功，a四次中任意一次成功都可以触发c;

LASTONE表示b成功，a四次中最后一次成功才可以触发c；

ALL表示b成功，a四次中全部成功才可以触发c；


#### 2.3.4 任务调度器(TaskScheduler)

TaskScheduler具体模块图入上图，其中最主要的模块是任务分发器。

server通过push的方式，由任务分发器按照可扩展的分发策略，主动推送任务给某一个worker执行任务。

- 任务分发策略可自定义扩展

- 默认的分发策略：轮询分发策略(RoundRobin)、随机分发策略(Random)

- 按照任务的优先级分发

- 当分发的任务被Worker拒绝时，任务分发器将选择同一个组内的另一个Worker分发

- 未避免任务分发过于频繁，当所有的Worker都拒绝后，任务分发器需间隔一段时间后再尝试重新发送

- 当任务运行失败时，根据重试配置重新分发任务

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
