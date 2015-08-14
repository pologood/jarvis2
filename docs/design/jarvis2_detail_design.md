# 调度系统重构详细设计文档

## 一、模块设计

### 1.1 DAGScheduler模块设计


DAGScheduler类图如下：

![uml_DAGScheduler](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_DAGScheduler.png)

DAGScheduler中的DAGMap，其数据结构为Map[Integer, DAGJob]  
DAGScheduler中的runningMap，其数据结构为Map[Integer, DAGTask]  
JobDependStatus中的jobStatusMap，其数据结构为Map[Integer,Map[Integer,Boolean]]


#### 1.1.1 定时任务调度

时间调度器负责调度基于时间触发的任务，支持Cron表达式时间配置。

![时间调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/time_based_scheduler_new.png)

时间调度器从数据库中加载周期性调度任务，或者从Rest API请求增加/修改/删除任务。根据任务配置的调度时间(CronExpression)，调度器定时计算出每个任务后面一段时间内（如：一天）的具体调度时间，并生成调度计划(Schedule Plan)。

- cron analyzer是一个cronTab表达式的语法分析器，输入是一个job，产出是一些task。

- task表会把时间最小的task排在最前面，数据结构上采用堆？

- cron schedule thread是一个线程，不断轮询task表，当满足时间就会提交给TaskScheduler。

#### 1.1.2 事件处理

DAGScheduler通过观察者模式进行事件处理，目的是把同步调用变成异步调用，跟外部调用者进一步解耦。并且为将来支持其他功能的监听提供了可扩展性。


##### 1.1.2.1 Event设计

![uml_event](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_event.png)

如图所示，Event是一个接口，DAGEvent是一个抽象类，其子类有InitializeEvent,SuccessEvent,ScheduledEvent等。DAGEvent中主要有两个成员，jobid和taskid。

##### 1.1.2.2 Observable和Observer设计

![uml_observable](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_mvc_observable.png)

如图所示，Observable是一个接口，相当于观察者模式中的主题，Listener是一个接口是观察者模式中的观察者。

Observable提供注册、移除、通知观察者的接口。EventBusObservable是一个抽象类，是基于google EventBus观察者模式中的主题。DAGScheduler是具体的实现类。

Listener可以有多种实现，订阅继承Event接口的事件进行处理。

#### 1.1.3 支持可扩展的依赖策略

DAGJob中有一个成员JobDependStatus，用来维护当前任务的依赖的状态，其内部数据结构主要是Map[Integer,Map[Integer,Boolean]], 表示Map[jobid,Map[taskid,status]]

比如c依赖于任务a和b，a每小时跑4次，b每小时跑1次，最终生成的依赖状态表如下表：

| jobid | taskid | 状态 |
| ------| ------ | ---- |
| joba  | taska1 |  T   | 
| joba  | taska2 |  T   |
| joba  | taska3 |  F   |  
| joba  | taska4 |  T   | 
| jobb  | taskb1 |  T   | 

然后根据任务的依赖策略判定依赖是否满足，当前支持的依赖策略有：ANYONE,LASTONE,ALL

- ANYONE表示b成功，a四次中任意一次成功都可以触发c;

- LASTONE表示b成功，a四次中最后一次成功才可以触发c；

- ALL表示b成功，a四次中全部成功才可以触发c；

这个任务当前依赖任务状态表会实时持久化到数据库中，当重跑历史任务或者系统异常重启的时候，也能获取之前依赖任务的状态。


### 1.2 ExecuteQueue模块设计

### 1.3 JobDispatcher模块设计

![Job Dispatcher](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_job_dispatcher.png)

Job Dispatcher负责从Worker组中分配一个Worker，然后将任务发给此Worker执行。

JobDispatcher接口中只有一个select方法，具体Worker分配逻辑在此方法中实现，以支持对不同分配策略的支持。默认已实现的有轮询分配(RoundRobinJobDispatcher)、随机分配(RandomJobDispatcher)。

RoundRobinJobDispatcher：内部维护Worker的索引，分配完一个Worker后索引递增，当索引超过Worker数后归0从新开始计算，与索引位置对应的Worker即为此次任务分配的Worker。

RandomJobDispatcher：随机生成一个Worker数以内的整数作为Worker索引，与此索引位置对应的Worker即为此次任务分配的Worker。

### 1.3 dao模块设计

### 1.4 service模块设计

调度系统有四个service，master,worker,logserver和restfulserver. 其中master,worker,logserver通过rpc协议通信，使用akka框架，其akka架构图如下：

![akka_service](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/akka_service.png)

如上图所示，sentinel master内部有ServerActor，HeartBeatActor，和JobMetricsRoutingActor。HeartBeatActor用来接收slave发送过来的心跳信息，由HeartBeatManager来维护所有client的信息。ServerActor作为master的核心actor，接收restfulServer发送过来的信息，通过负载均衡的分发策略把任务提交给ClientActor，向JobMetricsRoutingActor汇报任务状态和进度，把log通过LogRoutingActor写到logserver中。

JobMetricsRoutingActor对jobId进行哈希，把任务状态和进度路由给具体的JobMetricsActor。JobMetricsActor把任务状态写到DB中，来持久化任务状态，把任务进度反馈给前段。

LogRoutingActor和JobStatusRoutingActor类似，只是路由功能。由具体的LogWriterActor来写log，LogReadActor来读log。

### 1.5 Job模块设计

![Job](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_job.png)

Job为任务的抽象类，主要包括4个接口：preExecute()、execute()、postExecute()、kill()，作用分别为：

preExecute()：执行任务之前的预处理，如：数据库连接、数据清理等，此方法在execute()之前调用；

execute()：任务的主要执行方法，具体执行内容在此方法中实现，如：执行HiveQL等。JobContext中包含了任务运行所需的输入参数，如：任务类型、执行命令、任务名称、扩展参数等。任务执行中输出的日志通过调用LogCollector的方法将日志发送给LogServer，LogServer收到后将日志持久化至存储系统中。此方法调用时向Server汇报”执行中“状态，执行完成时向Server汇报”成功“或”失败“状态。执行过程中可调用ProgressReporter汇报任务进度。

postExecute()：任务运行完成后的处理，如：报警等，此方法在execute()之后调用；

kill()：终止任务。

通过实现不同的Job抽象类可以支持多种类型任务的运行，默认实现的任务包括：Shell、Hive、Presto、Java、MapReduce等。

## 二、流程设计

### 2.1 提交任务

### 2.2 Kill任务

![Kill Job](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/uml_kill_job.png)

RestServer收到Kill请求转发给Server，然后Server将Kill请求发送到执行对应任务的Worker，由Worker调用job的kill()方法终止任务，完成后向Server返回响应。

### 2.3 重跑任务

1. task表根据日期持久化到数据库中，如果没有修改crontab表，每次重跑历史任务或者当天任务，taskid不变。
2. 重跑任务时，可以选择是否重跑后置依赖，如果不选，不会主动把后置依赖任务注册到DAGScheduler中，反之，则会把后置依赖注册到DAGScheduler中。
3. 重跑指定任务时，直接根据taskid重跑，如果是定时任务，加入到TimeScheduler，如果是依赖任务，注册到DAGScheduler中。
4. 重跑一段时间的定时任务，首先根据cron表的updateTime判断是否需要更新task。如果不需要更新，直接根据起止时间计算出要重跑哪些taskid，然后重跑这些task。如果需要更新，则重新生成task。
5. 重跑一段时间的依赖任务，首先根据jobDependency表的updateTime判断是否需要更新。如果需要更新则更新taskDependency表，把没用的依赖去掉。接着把这个任务当天所有task变成Time+DAG任务，并且Time为当前系统时间，立即执行。当这个任务收到TimeReadyEvent时，会从数据库的taskDependency表中获取依赖状态，如果通过依赖检查，则会开始调度。
6. 重跑任务结束后，会主动更新taskDependency中的状态。便于后续依赖任务的依赖检查。

### 2.4 修改任务

1. 如果修改job内容，即job表，不会影响调度逻辑
2. 如果修改cron表，会重新刷新task表，已经提交的任务不做回退
3. 如果修改依赖关系，修改jobDependency和taskDependency表，并且发送ModifyEvent给DAGScheduler重新刷新依赖关系。

### 2.5 升级处理

不考虑RollingUpgrade，目标做到无状态重启，重启服务不丢失任务状态。

#### 2.5.1 master升级

确保每次更新任务状态和依赖任务的状态，都实时更新到DB中。确保每次生成和修改执行计划，都实时更新到DB的task表中。

master启动的时候，包括standbyserver切换为active的时候，做如下事情：   
1. load jobDependency表，重建DAG表  
2. load task表，恢复定时任务  
3. load jobDependStatus表，恢复依赖任务 


#### 2.5.2 worker升级

每次任务执行完成会在本地生成状态文件，当向master汇报完状态，会把该文件删除。

每次worker启动的时候会扫描该文件，发现有状态是成功或失败的，重新向master发送状态，然后删除该文件。

发现该文件是running，根据任务类型去获取任务状态，如果能获取到并且是成功或失败，重新向master发送状态，然后删除该文件。

如果状态是running，向master发送失败状态，必要场合，释放该任务相关的资源（比如kill yarn上的任务）。

如果无法获取任务状态，向master发送失败状态，必要场合，释放该任务相关的资源（比如kill yarn上的任务）。


### 2.7 异常处理
#### 2.7.1 server端的异常处理
- server重启

master/stand by HA切换处理

恢复上次运行的状态
包括，DAG表，task表，jobDependStatus表等

检查时间触发器，恢复重启过程中未触发的时间事件。

与worker取得联系，继续接受worker消息。

- worker失联处理

超过3分钟联系不上worker，则把该worker上的任务重新发到其他worker执行。
（先把任务设置为失败，然后重新执行任务）

![worker_miss](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/worker_miss.png)

#### 2.7.2 worker端的异常处理
- worker重启
恢复执行中的任务，并向server继续发送消息。

如果任务不可恢复，则汇报任务执行失败。

- server失联处理

超过3分钟联系不上server，则把执行中的任务都kill掉。

![worker_miss](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/server_miss.png)



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

### 3.10 进度汇报

- Worker -> Server

请求：

| 字段      | 类型     | 必选   | 默认值  | 描述                  | 
| ------- | ------ | ---- | ---- | ------------------- | 
| job_id      | int64 | T    |      | 任务ID         | 
| progress    | double  | T    |      | 任务完成进度           |

响应：

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否请求成功 |

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

[表结构设计](http://gitlab.mogujie.org/bigdata/jarvis2/blob/master/docs/design/jarvis2_design_db.md)