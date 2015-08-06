## 表结构设计


### job表 

| 字段    | 类型    |  主键  |是否为NULL | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| jobId    | int(11) | key|F|   | 任务Id           | 
| jobName  | string  |    |F|'' | 任务名称          | 
| type     | string  |    |F|1  | 任务类型:1 hive；2:shell;3 java; 4:MR | 
| content  | string |     |F|'' | 任务内容          | 
| executor | string |     |F|'' | 执行者           | 
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 


### jobDependence表
| 字段    | 类型   |  主键  | 是否为NULL | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| jobId  | int(11)   | key|F    |      | jobId           | 
| preJobId  | int(11) |key  | F    |   0   | 前置JobId    | 


### crontab表
| 字段    | 类型  |  主键  | 是否为NULL | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| cronId    | int(11) |key  | F    |      | cronId           | 
| jobId     | int(11) | | F    |   0   | 关联jobId           | 
| status    | tinyint |  | F    |   1   | 状态：0无效；1有效           | 
| exp       | string  | | F    |   ''   | cron表达式           | 
| startDate | int(8)  | | F    | 0     | 开始日期  YMD     | 
| endDate   | int(8)  | | F    |  0    | 结束日期  YMD     | 
| editor    | string  | | F    |   ''   | 设定者     | 
| updateTime| int(11) | | F    | 0     |   更新时间      | 


### task表
| 字段    | 类型   |  主键   | 是否为NULL   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| taskId | int(11) |key|F    |      | taskId       | 
| jobId   | int(11) |  | F    |      | 关联JobID          | 
| status  | tinyint | |F    |      | task状态： 1:waittng；2:ready；3:running；4:success；5:failed；6:killed   | 
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 

### taskDependence表
| 字段    | 类型   |  主键   | 是否为NULL   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| taskId | int(11) |key|F    |      | taskId       | 
| preTaskId   | int(11) |key  | F    |      | 前置taskID          | 
| preFinishFlag  | tinyint | |F    |0      | 前置完成标记： 0:未完成 ；1：完成   | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 


### taskAttempt表
| 字段    | 类型     |  主键 | 是否为NULL   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| taskId    | int(11) | key|F    |      | taskId       | 
| attemptId | int(11) |key  | F    |      |attemptID  |
| jobId     | int(11) |  | F    |      | 关联JobID ，冗余字段 |
| status    | tinyint |  | F    |      | task状态： 1:waittng；2:ready；3:running；4:success；5:failed；6:killed   | 
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 




### worker表
| 字段     | 类型  |  主键    | 是否为NULL   | 默认值  | 描述  | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| workerId   | int(11) |key| F    |      | workerId       | 
| workerName | string  |   | F    |      | worker名称      | 
| ip         | string  |   | F    |      | ip地址          | 
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 




### workerGroup表
| 字段     | 类型  |  主键    | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| groupId    | int(11) |key | F    |      | workerGroupID    | 
| groupName  |  string |    | F    |      | group名称       |
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 


### workerGroupRelation表
| 字段     | 类型   |  主键   | 必选   | 默认值  | 描述    | 
| ------ | ------ | ---- | ---- | ------------ | ---- |
| workerId  | int(11) | key|F    |      | workerID    | 
| groupId  |  string | key|F    |      | groupID      | 
| createTime  | int(11)|     |F|0  |   创建时间      | 
| updateTime  | int(11)|     |F|0  |   更新时间      | 









