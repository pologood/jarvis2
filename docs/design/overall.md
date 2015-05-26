# 系统总体设计，流程和功能点构思：

这里是系统的概要设计，流程逻辑，和需要实现的功能点的概述。

目的是确定总体业务逻辑和定义各模块基本的功能点。具体各模块实现细节设计文档请看个模块详细设计文档。

## 系统构成

总体原则: 各个组件尽量做到无状态化, 各种运行信息需要维护在DB或ZK中

### 组件构成

整个平台的组成包括：

 * Jarvis Master (HA)
   * 作业调度逻辑管理
   * 对外提供作业调度,查询等接口(对外以REST方式提供服务)
   * 以必要的HA方式运行

 * Jarvis Worker
   * 多个无差别实例
   * 负责管理和执行具体的作业
 
 * Jarvis Console
   * 展示作业调度信息, 比如当前运行情况, 历史信息查询等
   * 控制调度逻辑, 比如作业触发, 重置等
   * 配置管理权限
   * 系统状态监控,资源情况,master/worker状态等
   * 任务相关依赖文件管理等
   
 * 数据库
   * 存储作业配置信息, 任务调度信息, 作业运行历史信息, 作业日志信息?等. 

  * 下游相关系统

外围相关系统 ( 部分功能需要调整或新开发 )

 * Xray : 作业可视化开发平台, 对作业任务参数进行调度配置等.
    * 是否要考虑部分逻辑与Jarvis Console整合, 比如作业调度逻辑这一部分, 是否应该跳转到Jarvis Console后台实现.

## 概念

任务相关:

* 作业: Job -> 指的是一个作业,不管是重复的,还是一次性.以运行方式的配置为维度划分
* 任务: Task -> 指的一个作业的一次运行. 以运行实例为维度划分.

这里Task和原来系统的Task概念不一致...新老系统衔接的时候要留意.

作业标识:

* JobId : 全局唯一,自增ID
* PlanId : Job内部自增ID(一次plan递增一次?)
* ScheId : JobId_PlanId
* TaskId : ScheId_TryID(标识用作区分失败重试)
 
 

### 技术要点

#### 整体工作流程:  

Master节点以Rest API的形式提供http接口接受作业调度请求. 客户端向Master提交作业, Master将作业配置信息存储在数据库中,并制定调度计划. Master根据各个worker的资源,负载情况将就绪的任务通过内部通讯协议发送给Worker执行. Worker构建任务执行环境并监督任务的生命周期,将任务状态更新到数据库中, 并同时向Master汇报进度. 客户端通过向Master查询获得作业工作状态信息.

**待明确**

* 是否提供监听接口供客户端获取作业状态变更信息? 以什么形式? 需要能和Rest接口配合得比较好, 不会有太大冲突. ZK? 作业多的话, ZK压力会比较大. 其它形式? 别的系统怎么做的.

#### 内部通讯协议:

Protobuf

具体通讯协议内容待命确.

#### 对外接口:

Rest API

具体API待明确.

### 技术选型

* 依赖注入: google guice: http://code.google.com/p/google-guice/wiki/GettingStarted

## 各模块概要

### Jarvis Master


### Jarvis Worker

* worker状态的变更, 在worker内部, 可以通过observer模式实现? 这样状态变更是否直接写DB, 是否透过代理写DB, 是否写ZK, 是否汇报给Master, 严重错误是否报警,  是否发监控数据, 都可以通过注册不同的listener来实现. 逻辑互相不影响. 就是listener调用链要做好,不能有一个listener block 其它listener的执行. 不知道这一点有什么好的实现办法没有.  或者比如只是调用listener的put方法, 重要的listener实现的时候用同步处理的方式(比如写DB和通知master), 其它listener使用异步处理的方式(比如通知报警,发邮件,监控统计之类). -> java.util.Observable;

### Jarvis Console


### DB接口


### client模块

 * 可以考虑提供对REST API的封装.
 * 提供JAVA API

## 其它杂项？