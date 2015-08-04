# 调度系统重构设计说明书

## 一、引言

数据平台现有的调度系统(Jarvis)、哨兵系统(Sentinel)存在一些设计与实现上的不足，缺乏足够的稳定性与扩展性，不利于后期维护。本次重构将改进系统整体设计，以Akka开源框架为核心，提升系统稳定性及扩展性。



## 二、总体设计

### 2.1 需求概述

- 系统具备足够的稳定性、扩展性，支持任务的并行调度，保证任务不重复、不错漏执行

- 提高系统的容错性，主要组件无状态化，以便于服务的快速恢复

- 支持多种类型（自定义扩展）的任务执行，如：Hive、Shell、MapReduce、Spark等

- 不同类型的任务具有各自的任务接收策略，以支持对后端系统（如：Hadoop）的特定控制，如：流控、Load、CPU使用率、内存使用率等

- 任务支持重试、重试次数、重试间隔的配置

- 改进通信协议，采用更可靠的通信机制（Netty + Protocol Buffers）

- 支持时间触发、依赖触发两种调度方式

- 能够方便地修改执行计划

- 支持细粒度周期（小于天）任务的调度

- 支持对不同调度周期的任务依赖配置

- 统一的日志收集中心，更好地支持日志的随机读写（offset + lines），并改进对有实时读取日志需求应用的支持（如：Ironman）

- 支持任务的非功能性扩展，如：用户权限验证

- 开放系统接口，对外提供REST API



### 2.2 系统设计

![系统设计](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/jarvis_design.png)



#### 2.2.1 系统组成

![系统设计](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/server_topology.png)

- Server

>主要由调度器（时间调度器、依赖调度器）、执行队列、任务分发器组成

>对任务（包括周期性、非周期性）进行调度管理，根据任务的时间或依赖条件以及分发策略将任务发送给对应的Worker执行

>监听Worker的注册信息

>接收Worker发送的心跳汇报

>以HA方式运行

- Worker

>向Server注册信息

>根据接收策略决定是否接受Server发送的任务

>执行接受的任务并管理其生命周期

>周期性向Server汇报心跳、任务统计信息等

>将任务运行过程中输出的日志发送给LogServer存储

- LogServer

> 有状态，不能配置多个

- RestServer

>无状态，可以配置多个，通过nginx进行负载均衡

>restServer和server分开来的好处和logserver一样。

>与Server、Worker、LogServer进行数据交互，提供统一的REST API（任务调度、任务修改、状态查询、日志查询、Worker的上下线等）

- WebUI

>为用户提供Web操作界面，如：应用接入、配置任务、任务查看、重跑任务等

- 数据库

>存储接入应用的配置信息

>存储任务的配置信息（类型、调度时间、依赖、创建者等），任务的执行记录等


### 2.3 模块设计

#### 2.3.1 调度模块总体设计

调度模块总体分为TimeScheduler，DAGScheduler和TaskScheduler

![调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/core_scheduler.png)

如上图所示，TimeScheduler负责进行定时任务的调度，DAGScheduler负责依赖任务的调度，TaskScheduler是真正的调度器，负责执行任务、反馈任务结果和状态。

三个Scheduler协同工作，共同完成调度系统的调度工作。



#### 2.3.2 时间调度器(TimeScheduler)

时间调度器负责调度基于时间触发的任务，支持Cron表达式时间配置。

![时间调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/time_based_scheduler.png)

时间调度器从数据库中加载周期性调度任务，或者从Rest API请求增加/修改/删除任务。根据任务配置的调度时间(CronExpression)，调度器定时计算出每个任务后面一段时间内（如：一天）的具体调度时间，并生成调度计划(Schedule Plan)。

- 调度计划中的任务按照调度时间升序排序，调度器依次轮询检查任务的调度时间是否到达，到达调度时间的任务将被触发以等待执行

- 当任务的调度时间被修改时，调度器将从调度计划中删除该任务已生成的计划，然后重新生成新的调度计划

- 为使调度计划可恢复，调度计划存储于数据库中。



#### 2.3.3 依赖调度器(DAGScheduler)

依赖调度器通过观察者模式，以监听事件的方式进行依赖触发等操作  

- JobListener是一个job的监听器，用来监听一个job的事件，内部维护了job的依赖关系等原信息。 

- Observer是一个单例，负责添加、删除jobListener，以及通知事件给jobListener。observer只维护准备进入调度的依赖任务。

![核心调度器](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/dependency_based_scheduler_new.png)

如上图所示

当TimeScheduler提交任务给TaskScheduler之后，TaskScheduler会获取当前任务的孩子任务，把孩子注册到DAGScheduler中的observer中。

当某个任务执行完成，TaskScheduler中的statusManager会发送successEvent给DAGScheduler。jobListener监听到这个事件后，判断它是否是自己需要的前置依赖，如果是则更新前置依赖状态。当前置依赖全部完成了，提交任务到TaskScheduler中。

当依赖任务提交到TaskScheduler中后，就会开始调度，jobDispatcher会发送unregisterEvent给DAGScheduler，具体的某一个jobListener监听到这个事件的jobid和自己一样，就会把自己从observer中注销掉。同时jobDispatcher还会去获取这个任务的孩子任务，把孩子注册到DAGScheduler中的observer中。

如果修改了依赖关系，外部会发送modifyEvent给DAGScheduler，observer会通知所有的jobListener检查下自己的依赖，如果满足，向TaskScheduler提交任务。

TaskManager中的statusManager自己处理失败重试策略



#### 2.3.4 任务调度器(TaskScheduler)

TaskScheduler具体模块图入上图，其中最主要的模块是任务分发器。

server通过push的方式，由任务分发器按照可扩展的分发策略，主动推送任务给某一个worker执行任务。

- 任务分发策略可自定义扩展

- 默认的分发策略：轮询分发策略(RoundRobin)、随机分发策略(Random)

- 按照任务的优先级分发

- 当分发的任务被Worker拒绝时，任务分发器将选择同一个组内的另一个Worker分发

- 未避免任务分发过于频繁，当所有的Worker都拒绝后，任务分发器需间隔一段时间后再尝试重新发送

- 当任务运行失败时，根据重试配置重新分发任务

	

#### 2.3.5 任务接受策略(Job Accept Strategy)

任务接受策略用于控制Worker或任务后端执行系统的负载，Worker根据负载情况决定是否接受Server发送的任务。

- 任务接受策略可自定义扩展

- 接收策略分为公用和任务自定义两种，公用的接收策略每种任务类型都会匹配，同时每种类型的任务可定义各自不同的接受策略

- 接收策略以链式方式执行，当其中任一接收策略没有通过，则失败，返回拒绝。

- 默认实现的任务接受策略：

Worker：LoadStrategy、CPUStratery、MemoryStrategy

Hive任务：YarnStrategy



### 2.4 处理流程

#### 2.4.1 任务执行流程

![任务执行流程](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/job_execution_flow.png)



#### 2.4.2 任务终止流程

![任务终止流程](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/kill_job.png)



#### 2.4.3 任务重跑流程

调度系统的server不提供任务重跑的逻辑，由外部系统自己计算需要重跑的任务，然后传给调度系统。

![任务重跑流程](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/job_rerun.png)  

如上图所示是外部系统重跑的逻辑

针对第3点，比如任务b依赖于任务a1，a2，a3，T表示运行周期，则  T(b) = min(T(a1), T(a2), T(a3))



#### 2.4.4 调度计划修改流程

![调度计划修改流程](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/plan_modify.png)  

如上图，当修改任务的内容的时候，只需要修改数据库中的内容，不会影响调度逻辑。调度系统不会在内存中维护任务的执行内容，每次会去数据库中动态拿。

当修改定时任务的执行时间时，需要把TimeScheduler中对应的任务删除，再重新添加。

当修改依赖任务的依赖关系时，需要修改内存中维护的任务原信息中依赖关系，并重新计算对应的父亲和孩子。（如果必要的话这里要做环路检测）



#### 2.4.5 Executor交互流程

![Executor交互流程](http://gitlab.mogujie.org/bigdata/jarvis2/raw/master/docs/design/img/job_executor.png)



### 2.5 涉及技术

- 分布式：Akka

- 依赖注入：Spring

- 数据库连接池：HikariCP

- DAO：MyBatis

- 内部通讯协议：Netty 、Protocol Buffers

- RESTful：Jersey

- 其它：guava

### 2.6 FAQ

- 系统启动的时候做什么？

>worker启动的时候会向master发送心跳，同时扫描本地文件系统，发现有任务没有发送成功的，再次发送。

>master启动的时候，接收worker发送过来的心跳，如果通过权限校验则把worker加入workerManager中。同时从DB load任务原信息，在内存中计算任务的孩子和父亲，并把定时任务加入到TimeScheduler中。

- 任务如何调度？

>任务分为定时任务和依赖任务，定时任务通过配置crontab表达式定义自己的启动时间，依赖任务通过配置依赖关系，当前置依赖都满足的时候触发依赖任务。

>当系统启动的时候，会把所有任务的原信息从数据库中load到内存中来，并计算任务的孩子和父亲，维护在内存中。如果任务配置了crontab表达式，则加入TimeScheduler中。

>TimeScheduler是一个时间调度器，满足时间就会提交任务到TaskScheduler中。

>TaskScheduler内部维护一个executeQueue和jobDispatcher，jobDispatcher并发地从executeQueue中取任务执行。

>当jobDispatcher取到一个任务，就会获取这个任务的孩子是谁，然后把孩子注册到DAGScheduler的observer中。

>当某个任务执行成功，TaskScheduler的statusManager就会向DAGScheduler发送successEvent，某个jobListener监听到自己的依赖有这个成功的任务时，就会更新自己依赖任务的状态，当依赖全部满足时，就会提交任务到TaskScheduler中。

- 任务如何分发？

>任务分发器从执行队列中通过多线程方式并发取任务，然后通过可扩展的分发策略，从workerManager中选一个负载比较小的worker，发送任务。

>任务分发器取任务的时候会优先选取优先级比较高的任务。

- 任务如何工作？

>任务运行在worker本地，当worker接收master发送过来的任务时，会根据任务类型启动相应的任务。如果是shell类型的任务，会fork一个进程来执行。

- 任务的生命周期和持久化？

>任务状态：等待中（WAITING），准备中(READY)，运行中(RUNNING)，成功（SUCCESS），失败(FAILED)，接收（ACCEPTED），拒绝(REJECTED)

>特别的：任务从server提交到client端，还有个隐含的状态，提交中(SUBMITTING)，但是这个状态极其短暂，发过去之后不是接收就是拒绝，所以没有持久化在数据库中。

>任务持久化在数据库中，每一次更新任务状态都确保能更新到数据库中

>当任务生成执行计划的时候，任务初始状态为WAITING。当任务进入执行队列时，状态更新为READY。当任务被work接收时，状态更新为ACCEPTED，反之更新为REJECTED。当worker开始运行任务的时候，状态更新为RUNNING。当任务运行成功后，状态更新为SUCCESS，反之更新为FAILED。

- 任务如何标识？

>表结构设计中，主要有job表(任务的内容表)，task表(任务的配置表，在job表基础上增加crontab)，plan表(任务每次的执行表)，所以在调度系统的数据结构中，有taskid和planid两个字段。

>因为任务支持重试策略，还有attemptid。

>taskid，planid和attemptid在调度系统中分成三个字段存储，这样和存成一个字段的好处是，每次新增一个planid或者attemptid，不需要先split。

>最终反映在jobname上，jobname = originjobname_taskid_planid_attemptid，这样可以唯一标识一个job，在yarn上观察和kill都很方便，也支持现有bgmonitor通过jobname唯一标识一个任务的需求。

## 三、接口设计

### 3.1 内部接口

Server、Worker、LogServer、RestServer之间的通信接口均采用Protocol Buffers。



#### 3.1.1 提交任务(Server -> Worker)

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

#### 3.1.2 终止任务(Server -> Worker)

Server请求

| 字段     | 类型    | 必选   | 默认值  | 描述   | 
| ------ | ----- | ---- | ---- | ---- | 
| job_id | int64 | T    |      | 任务ID | 

Worker响应

| 字段      | 类型   | 必选   | 默认值  | 描述     | 
| ------- | ---- | ---- | ---- | ------ | 
| success | bool | T    |      | 是否终止成功 | 

#### 3.1.3 任务状态汇报(Worker -> Server)

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

#### 3.1.4 日志写入(Worker -> LogServer)

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

#### 3.1.5 日志读取(RestServer -> LogServer)

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

#### 3.1.6 心跳汇报(Worker -> Server)

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

#### 3.1.7 Worker上下线(RestServer -> Worker)

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

### 3.2 外部接口

RestServer对外提供REST API。



#### 3.2.1 执行任务

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

#### 3.2.2 获取日志

接口：/server/querylog

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

#### 3.2.3 获取任务状态

接口：/server/jobstatus

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

#### 3.2.4 获取查询结果

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

#### 3.2.5 终止任务

接口：/server/killjob

Method：POST

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

#### 3.2.6 下载查询结果

接口：/server/result/download

Method：GET

| 字段    | 类型     | 必选   | 默认值  | 描述     | 
| ----- | ------ | ---- | ---- | ------ | 
| token | string | T    |      | 接口调用口令 | 
| time  | long   | T    |      | 时间戳    | 
| name  | string | T    |      | 应用名称   | 
| jobId | long   | T    |      | 任务ID   | 

#### 3.2.7 Worker上下线

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

## 四、数据结构设计



## 五、系统容错设计