

# Dump of table crontab
# ------------------------------------------------------------

DROP TABLE IF EXISTS `crontab`;

CREATE TABLE `crontab` (
  `cronId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'cronId',
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'JobId',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '状态：0无效；1有效',
  `exp` varchar(1024) NOT NULL DEFAULT '' COMMENT 'cron表达式',
  `startDate` int(8) NOT NULL DEFAULT '0' COMMENT '开始日期 YMD',
  `endDate` int(8) NOT NULL DEFAULT '0' COMMENT '结束日期 YMD',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`cronId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='crontab表';



# Dump of table job
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job`;

CREATE TABLE `job` (
  `jobId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'jobID',
  `jobName` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `jobType` varchar(64) NOT NULL DEFAULT '0' COMMENT '任务类型 1：hive；2：shell；3：java； 4：MR',
  `content` varchar(10000) NOT NULL DEFAULT '' COMMENT '任务内容',
  `params` varchar(1024) NOT NULL DEFAULT '' COMMENT '任务参数',
  `executUser` varchar(64) NOT NULL DEFAULT '' COMMENT '执行用户',
  `priorty` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '优先级',
  `appName` varchar(64) NOT NULL DEFAULT '' COMMENT '应用名称',
  `workerGroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'worker组ID',
  `rejectAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时的重试次数',
  `rejectInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务被Worker拒绝时重试的间隔(秒)',
  `failedAttempts` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时的重试次数',
  `failedInterval` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '任务运行失败时重试的间隔(秒)',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`jobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job表';



# Dump of table job_depend
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job_depend`;

CREATE TABLE `job_depend` (
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'jobId',
  `preJobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置JobId',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`jobId`,`preJobId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job依赖表';



# Dump of table job_depend_status
# ------------------------------------------------------------

DROP TABLE IF EXISTS `job_depend_status`;

CREATE TABLE `job_depend_status` (
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT 'jobId',
  `preJobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置JobId',
  `preTaskId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '前置TaskId',
  `preTaskStatus` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '前置Task状态：0无效；1有效',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`jobId`,`preJobId`,`preTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job依赖状态表';



# Dump of table task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `taskId` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'taskId',
  `attemptId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '尝试ID',
  `jobId` bigint(11) unsigned NOT NULL DEFAULT '0' COMMENT '所属JobID',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT 'task状态： 1:waiting；2:ready；3:running；4:success；5:failed；6:killed',
  `executUser` varchar(64) NOT NULL DEFAULT '' COMMENT '执行用户',
  `executeTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '执行时间',
  `dataYmd` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '数据日期',
  `attemptInfo` varchar(1024) NOT NULL DEFAULT '' COMMENT '尝试信息,json格式',
  `startDate` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '开始日期 YMD',
  `endDate` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '结束日期 YMD',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  `updateUser` varchar(64) NOT NULL DEFAULT '' COMMENT '更新用户',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='task表';



# Dump of table worker
# ------------------------------------------------------------

DROP TABLE IF EXISTS `worker`;

CREATE TABLE `worker` (
  `workerId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerId',
  `workerName` varchar(64) NOT NULL DEFAULT '' COMMENT 'worker名称',
  `ip` char(16) NOT NULL DEFAULT '' COMMENT 'ip地址',
  `port` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '端口号',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`workerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker表';



# Dump of table worker_group
# ------------------------------------------------------------

DROP TABLE IF EXISTS `worker_group`;

CREATE TABLE `worker_group` (
  `wgroupId` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'workerGroupID',
  `wgroupName` varchar(64) NOT NULL DEFAULT '' COMMENT 'workerGroup名称',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`wgroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='workerGroup表';



# Dump of table worker_group_relation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `worker_group_relation`;

CREATE TABLE `worker_group_relation` (
  `workerId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'workerId',
  `wgroupId` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'workerGroupID',
  `createTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
  `updateTime` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后更新时间',
  PRIMARY KEY (`workerId`,`wgroupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='workerGroupRelation表';





