# 调度系统重构详细设计文档

## 一、模块设计

### 1.1 scheduler模块设计

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
