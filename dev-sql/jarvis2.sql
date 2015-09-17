-- Create syntax for TABLE 'crontab'
CREATE TABLE `crontab` (
  `cronId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'cronId',
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'JobId',
  `status` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态：0无效；1有效',
  `exp` varchar(1024) NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `startDate` datetime NOT NULL COMMENT '开始日期 YMD',
  `endDate` datetime NOT NULL COMMENT '结束日期 YMD',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`cronId`),
  KEY `index_jobId` (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='crontab表';

-- Create syntax for TABLE 'job'
CREATE TABLE `job` (
  `jobId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'jobID',
  `originJobId` bigint(11) unsigned NOT NULL COMMENT '原始jobId,区别重跑任务',
  `jobName` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `jobType` varchar(64) NOT NULL DEFAULT '0' COMMENT '任务类型 hive_sql; hive_script; shell; java; MR',
  `jobFlag` int(3) NOT NULL DEFAULT '1' COMMENT '任务标志 1:有效； 2:无效；3：垃圾箱；',
  `content` varchar(10000) NOT NULL DEFAULT '' COMMENT '任务内容,脚本内容或脚本名',
  `params` varchar(2048) NOT NULL DEFAULT '' COMMENT '任务参数，json格式',
  `submitUser` varchar(64) NOT NULL DEFAULT '' COMMENT '提交用户',
  `priority` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '优先级',
  `appName` varchar(64) NOT NULL DEFAULT '' COMMENT '应用名称',
  `expireStartTime` datetime DEFAULT NULL COMMENT '有效期开始时间',
  `expireEndTime` datetime DEFAULT NULL COMMENT '有效期结束时间',
  `workerGroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'worker组ID',
  `rejectAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时的重试次数',
  `rejectInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时重试的间隔(秒)',
  `failedAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时的重试次数',
  `failedInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时重试的间隔(秒)',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`jobId`),
  KEY `index_originJobId` (`originJobId`),
  KEY `index_submitUser` (`submitUser`),
  KEY `index_createTime` (`createTime`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='job表';

-- Create syntax for TABLE 'job_depend'
CREATE TABLE `job_depend` (
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'jobId',
  `preJobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置JobId',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`jobId`,`preJobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job依赖表';

-- Create syntax for TABLE 'job_depend_status'
CREATE TABLE `job_depend_status` (
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'jobId',
  `preJobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置JobId',
  `preTaskId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置TaskId',
  `preTaskStatus` int(3) unsigned NOT NULL DEFAULT '1' COMMENT '前置Task状态：0无效；1有效',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`jobId`,`preJobId`,`preTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job依赖状态表';

-- Create syntax for TABLE 'task'
CREATE TABLE `task` (
  `taskId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'taskId',
  `attemptId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后的尝试ID',
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '所属JobID',
  `jobContent` varchar(10000) NOT NULL DEFAULT '' COMMENT '任务内容',
  `jobParams` varchar(2048) NOT NULL DEFAULT '' COMMENT '任务参数',
  `dataYmd` date NOT NULL COMMENT '数据日期',
  `status` int(3) unsigned NOT NULL DEFAULT '1' COMMENT 'task状态： 1:waiting；2:ready；3:running；4:success；5:failed；6:killed',
  `executeUser` varchar(64) NOT NULL DEFAULT '' COMMENT '执行用户',
  `executeStartTime` datetime NOT NULL COMMENT '执行开始时间',
  `executeEndTime` datetime NOT NULL COMMENT '执行结束时间',
  `attemptExtra` varchar(1024) NOT NULL DEFAULT '' COMMENT '尝试扩展信息,json格式',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`taskId`),
  KEY `index_jobId` (`jobId`),
  KEY `index_dataYmd` (`dataYmd`),
  KEY `index_executeStartTime` (`executeStartTime`),
  KEY `index_executeUser` (`executeUser`) KEY_BLOCK_SIZE=4
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='task表';

-- Create syntax for TABLE 'worker'
CREATE TABLE `worker` (
  `workerId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerId',
  `workerName` varchar(64) NOT NULL DEFAULT '' COMMENT 'worker名称',
  `ip` char(16) NOT NULL DEFAULT '' COMMENT 'ip地址',
  `port` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '端口号',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`workerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker表';

-- Create syntax for TABLE 'worker_group'
CREATE TABLE `worker_group` (
  `wgroupId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerGroupID',
  `wgroupName` varchar(64) NOT NULL DEFAULT '' COMMENT 'workerGroup名称',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`wgroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='workerGroup表';

-- Create syntax for TABLE 'worker_group_relation'
CREATE TABLE `worker_group_relation` (
  `workerId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'workerId',
  `wgroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'workerGroupID',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime NOT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`workerId`,`wgroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='workerGroupRelation表';