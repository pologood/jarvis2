# 原有的DB表结构信息

原有的DB表结构信息, 分析哪些表结构内容是我们需要的, 哪些是应该改变的.

# sentinel

* 执行流水表 sentenel.job_exec

字段 | 用途/参数/值等 描述 | 备注: 有没有用, 需要调整, 有什么问题之类 |
-----|-----|-----|
id | 自增id | 有必要? |
jobid |  |  |
client id |  smallint(4) 用于标识哨兵client, 关联 | 这个id如何能自动映射? 怎么保证不同client id不需要人工干预自动正确分配? 是否直接用IP+port? 或者其它自动唯一生成的方法来避免手工添加映射问题? 这个谁来写入的? master? client? |
content | longtext, 执行内容记录 | 会有数据量很大的情况么? |
 ... |  |  |
status | 状态(0-成功,1-等待,2-被Client接受,3-执行中,4-错误,5-失败,6-恢复,8-终止) | 这个是否要考虑怎么和其它组件关于作业的状态能够尽可能匹配上? 各个组件之间作业状态要映射也是比较麻烦.|
其它请补充完整  |  |  |



表结构参考:
<pre/>
CREATE TABLE `job_exec` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_id` int(11) NOT NULL,
  `client_id` smallint(4) DEFAULT NULL COMMENT 'client_id',
  `content` longtext NOT NULL COMMENT '执行内容',
  `executor` varchar(11) NOT NULL DEFAULT '' COMMENT '执行者',
  `application_id` text COMMENT 'application_id ,间隔',
  `add_time` datetime NOT NULL COMMENT '任务 添加时间',
  `start_time` datetime DEFAULT NULL COMMENT '任务开始执行时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `cpu_time` int(11) DEFAULT NULL COMMENT 'CPU耗时',
  `exec_time` int(20) DEFAULT NULL COMMENT '执行时间',
  `status` int(4) NOT NULL COMMENT '状态(0-成功,1-等待,2-被Client接受,3-执行中,4-错误,5-失败,6-恢复,8-终止)',
  `message` longtext COMMENT '记录失败信息',
  PRIMARY KEY (`id`),
  KEY `job_id` (`job_id`),
  KEY `add_time` (`add_time`)
) ENGINE=InnoDB AUTO_INCREMENT=683916 DEFAULT CHARSET=utf8;
</pre>


* 其它表, 内容, 用途描述,如何调整之类?


# Jarvis-sche


# Jarvis-console





