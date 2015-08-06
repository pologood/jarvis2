## 表结构设计


### job表 

| 字段    | 类型    | 是否为NULL | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| jobId  | int(11)   | F    |      | 任务Id           | 
| jobName| string | F    |    ''  | 任务名称          | 
| type| string | F    |    1  | 任务类型:1 hive；2:shell;3 java; 4:MR | 
| content| string | F    |    ''  | 任务内容          | 
| executor  | string | F    |    ''  | 执行者           | 
| created   | int(11)    | F    | 0     |   创建时间      | 
| updated   | int(11)    | F    | 0     |   修改时间      | 



### crontab表
| 字段    | 类型    | 是否为NULL | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| cronId  | long   | F    |      | cronId           | 
| jobId  | long   | F    |   0   | 关联jobId           | 
| status  | tinyint   | F    |   1   | 状态：0无效；1有效           | 
| exp  | string   | F    |   ''   | cron表达式           | 
| startDate  | int(8)   | F    | 0     | 开始日期  YMD     | 
| endDate  | int(8)   | F    |  0    | 结束日期  YMD     | 
| editor  | string   | F    |   ''   | 设定者     | 
| updated   | int(11)    | F    | 0     |   修改时间      | 



### task表
| 字段    | 类型     | 是否为NULL   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| taskId | int(11) | F    |      | taskId       | 
| jobId   | int(11)   | F    |      | 关联JobID          | 
| status  | tinyint | F    |      | task状态： 1:waittng；2:ready；3:running；10:success；11:failed；12:killed   | 



### worker表
| 字段     | 类型     | 是否为NULL   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| ip  | string | F    |      | ip地址       | 
| workerName  | string | F    |      | worker名称      | 



### workerGroup表
| 字段     | 类型     | 必选   | 默认值  | 描述           | 
| ------ | ------ | ---- | ---- | ------------ | 
| groupId  | int(11) | F    |      | workerGroupID    | 
| groupName  |  string | F    |      | group名称       | 






