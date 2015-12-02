-- Create syntax for TABLE 'alarm'
CREATE TABLE `alarm` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `jobId` bigint(11) unsigned NOT NULL,
  `alarmType` varchar(32) NOT NULL COMMENT '报警类型，可以有多个:1-短信，2-TT，3-邮件，4-微信',
  `receiver` varchar(256) NOT NULL DEFAULT '',
  `status` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '状态：0-禁用；1-启用',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`id`,`jobId`),
  KEY `index_jobId` (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'app'
CREATE TABLE `app` (
  `appId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'appId',
  `appName` varchar(64) NOT NULL DEFAULT '' COMMENT 'app名称',
  `appKey` varchar(32) NOT NULL DEFAULT '' COMMENT 'appKey',
  `appType` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '类型：1：普通；2：管理',
  `status` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态：0：停用；1：启用；',
  `maxConcurrency` int(11) unsigned NOT NULL DEFAULT '10' COMMENT '最大任务并行度',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `updateUser` varchar(32) NOT NULL DEFAULT '' COMMENT '最后更新用户',
  PRIMARY KEY (`appId`),
  UNIQUE KEY `index_appName` (`appName`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='app表';

-- Create syntax for TABLE 'app_worker_group'
CREATE TABLE `app_worker_group` (
  `appId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'appId',
  `workerGroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'workerGroupID',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `updateUser` varchar(32) NOT NULL DEFAULT '' COMMENT '最后更新用户',
  PRIMARY KEY (`appId`,`workerGroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='app_workgroup表';

-- Create syntax for TABLE 'job'
CREATE TABLE `job` (
  `jobId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'jobID',
  `jobName` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `jobType` varchar(64) NOT NULL DEFAULT '0' COMMENT '任务类型 hive_sql; hive_script; shell; java; MR',
  `jobFlag` int(3) NOT NULL DEFAULT '1' COMMENT '任务标志 1:有效； 2:无效；3：过期；4：垃圾箱；',
  `content` varchar(10000) NOT NULL DEFAULT '' COMMENT '任务内容,脚本内容或脚本名',
  `params` varchar(2048) DEFAULT '' COMMENT '任务参数，json格式',
  `submitUser` varchar(32) NOT NULL DEFAULT '' COMMENT '提交用户',
  `priority` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '优先级,1:low,2:normal,3:high,4:verg high',
  `appId` int(11) NOT NULL COMMENT '应用ID',
  `workerGroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'worker组ID',
  `activeStartDate` datetime DEFAULT NULL COMMENT '有效开始日期',
  `activeEndDate` datetime DEFAULT NULL COMMENT '有效结束日期',
  `rejectAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时的重试次数',
  `rejectInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时重试的间隔(秒)',
  `failedAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时的重试次数',
  `failedInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时重试的间隔(秒)',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `updateUser` varchar(32) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`jobId`),
  KEY `index_submitUser` (`submitUser`),
  KEY `index_createTime` (`createTime`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='job表';

-- Create syntax for TABLE 'job_depend'
CREATE TABLE `job_depend` (
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'jobId',
  `preJobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置JobId',
  `commonStrategy` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '通用依赖策略。0:ALL, 1:LASTONE, 2:ANYONE',
  `offsetStrategy` varchar(1024) NOT NULL DEFAULT '' COMMENT '偏移依赖策略',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `updateUser` varchar(32) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`jobId`,`preJobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job依赖表';

-- Create syntax for TABLE 'job_schedule_expression'
CREATE TABLE `job_schedule_expression` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'JobId',
  `expressionType` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '1:cron; 2:rate; 3:delay; 4:ISO8601',
  `expression` varchar(64) NOT NULL,
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  KEY `index_jobId` (`jobId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Create syntax for TABLE 'task'
CREATE TABLE `task` (
  `taskId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'taskId',
  `attemptId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后的尝试ID',
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '所属JobID',
  `content` varchar(10000) NOT NULL DEFAULT '' COMMENT '任务内容',
  `params` varchar(2048) DEFAULT '' COMMENT '任务参数',
  `scheduleTime` datetime DEFAULT NULL COMMENT '调度时间',
  `progress` float NOT NULL DEFAULT '0' COMMENT '执行进度',
  `status` int(3) unsigned NOT NULL DEFAULT '1' COMMENT 'task状态： 1:waiting；2:ready；3:running；4:success；5:failed；6:killed',
  `workerId` int(11) DEFAULT '0' COMMENT 'workerId',
  `executeUser` varchar(32) NOT NULL DEFAULT '' COMMENT '执行用户',
  `executeStartTime` datetime DEFAULT NULL COMMENT '执行开始时间',
  `executeEndTime` datetime DEFAULT NULL COMMENT '执行结束时间',
  `attemptExtra` varchar(1024) DEFAULT '' COMMENT '尝试扩展信息,json格式',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`taskId`),
  KEY `index_jobId` (`jobId`),
  KEY `index_dataYmd` (`scheduleTime`),
  KEY `index_executeStartTime` (`executeStartTime`),
  KEY `index_executeUser` (`executeUser`) KEY_BLOCK_SIZE=4
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COMMENT='task表';

-- Create syntax for TABLE 'task_depend'
CREATE TABLE `task_depend` (
  `taskId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'taskId',
  `dependTaskIds` varchar(1024) NOT NULL DEFAULT '' COMMENT '依赖的task情况',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='task依赖表';

-- Create syntax for TABLE 'worker'
CREATE TABLE `worker` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerId',
  `workerGroupId` int(11) unsigned NOT NULL COMMENT 'workerGroupID',
  `ip` char(16) NOT NULL DEFAULT '' COMMENT 'ip地址',
  `port` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '端口号',
  `status` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '状态：0：下线；1：上线；',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='worker表';

-- Create syntax for TABLE 'worker_group'
CREATE TABLE `worker_group` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerGroupID',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT 'workerGroup名称',
  `authKey` varchar(32) NOT NULL DEFAULT '' COMMENT '认证key',
  `status` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态：0：无效；1：有效',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `updateUser` varchar(32) NOT NULL DEFAULT '' COMMENT '最后更新用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='workerGroup表';