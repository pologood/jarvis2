# 调度平台问题汇总


## 问题汇总

问题汇总只是一个列表, 具体详情见Jira

问题|发现日期|问题简单描述|状态|详细情况跟踪Jira|后续工作?|问题类型
--------|--------|--------|--------|--------|--------|--------|
st_cpc_income_step3与st_cpc_income_step4任务闭环依赖|2015-07-16|任务闭环依赖导致任务执行等待中|跟踪|@公冶 http://jira.mogujie.org/browse/BDA-399||开发问题
mbi_st_xd_share_count_output_sailfish执行失败|2015-07-16|BDA数据库磁盘满了未报警导致写入失败|跟踪|@大鹏 http://jira.mogujie.org/browse/BDA-400u||环境问题
dw_trd_shop_advertise_step8执行失败|2015-07-16|临时文件未删除|跟踪|@海贼 http://jira.mogujie.org/browse/BDA-402||其他
st_app_fashion_maimaimai这个任务在07-08 16:51:15同时启动了两个执行流水 | 2015-07-09| 同一任务在调度系统不应同时出现多个执行流水| 跟踪 |@光明 http://jira.mogujie.org/browse/BDA-380 || 开发问题
yarn主动kill app造成状态不一致，无法kill掉| 2015-06-30|部分任务java heap异常，yarn主动kill app，出现state=running,但是finalStatus=killed的现象，且使用yarn application -kill命令无法删。|跟踪|@光明 http://jira.mogujie.org/browse/BDA-360 ||其他
资源管理程序yarn出问题导致任务执行失败|2015-06-12|资源管理程序yarn出问题导致任务执行失败|跟踪|http://jira.mogujie.org/browse/BDA-312 @冰山||其他
hive任务成功后被hang住 | 2015-06-08 | st_trd_complaint_detail任务执行成功了，但是进程没结束，导致任务被提示错误| 跟踪 | http://jira.mogujie.org/browse/BDA-303 @无崖 ||其他




# 历史问题归档


## 还有后续工作要考虑的

问题|发现日期|问题简单描述|状态|详细情况跟踪Jira|后续工作?|问题类型|
--------|--------|--------|--------|--------|------------|------------|
ironMan提交大任务导致资源占用|2015-06-11|ironMan提交大任务导致资源占用,却又通过了maps检查|保留|http://jira.mogujie.org/browse/BDA-313 @牧名||开发问题
哨兵Client日志太多磁盘空间不够，导致任务失败 | 2015-06-10 |70磁盘空间不足导致失败，原因是程序不经log4j直接写本地文件，异常时写入巨量内容 | fix | @光明 http://jira.mogujie.org/browse/BDA-309 ||开发问题
MR任务失败时候导致的临时表不存在|2015-07-12| |跟踪|@光明 http://jira.mogujie.org/browse/BDA-393||环境配置
ntp同步问题造成大量任务失败和超时|2015-06-29|ntp同步问题造成大量任务失败和超时|fix|http://jira.mogujie.org/browse/BDA-344 @鸣人 |后续可能要找一个方式监控起来|环境配置
调整yarn jvm参数造成任务失败 |2015-06-30|调整yarn jvm参数，部分任务java heap异常，需要在脚本中调大参数|fix|http://jira.mogujie.org/browse/BDA-361 @无崖 | 一旦出现该问题，还是需要手工调整脚本，增大mapred.map.child.java.opts和mapred.reduce.child.java.opts设置值|环境配置
hadoop集群出现峰值负载过高 | 2015-06-05 | 2015.6.5，2:22 ~2:27 hadoop集群出现峰值负载过高情况，观察到是mofa8023这台机器内存和cpu使用率都达到了100%。| fix | http://jira.mogujie.org/browse/BDA-299 | 原先presto服务配置不合理, 已经迁移出这几个节点, 暂时放到96G内存的节点上.|环境配置
st_cps_unioncpsadseffect_output错误|2015-06-25|output_data error (2006, 'MySQL server has gone away')。|fix|http://jira.mogujie.org/browse/BDA-314 @清远 | 解决办法是给表加索引，需要排查其他表|环境配置
dwd_trd_punish_cheat_dump任务失败 | 2015-06-05| DB多了一个字段，需要在hive中加上相应的字段 | fix | http://jira.mogujie.org/browse/BDA-298 | 以后如何跟踪表结构变化,及时发现问题?|环境配置
dwd_usr_shoplevelmonthdetail_dump任务java.lang.ClassCastException  | 2015-05-30 | 人工填写脚本错误 | fix  | http://jira.mogujie.org/browse/BDA-271 | 可以考虑建表语句的修改需要测试执行验证通过? 比如在测试集群里建表成功?|环境配置
任务执行时缺少权限  | 2015-05-29 | st_site_magic_outlets_group_output，t_dongcheng_app_push2_output等任务执行时python权限问题 | fix  | http://jira.mogujie.org/browse/BDA-270 | 需要过一下这类权限问题的现状, 帐号使用方式等.|环境配置
任务配置错误 | 2015-05-28 | dw_usr_zhongan_snapshot_output taskName:dw_usr_zhangan_snapshot不存在 清远已经解决，根本原因是hbase源时,需要手动创建导出脚本语句 | fix | | ?|环境配置
metadata_prepare error | 2015-05-28 | data_prepare_mysql_bda,MySql bda 不存在。根本原因是原先mysql有master和slave，现在仅有master，以后再有slave时不会有此问题 | fix |  | prepare脚本需要改动? 需要总结|环境配置
堆内存不够（dw_usr_visit_step3）| 2015-05-22 | 有些任务需要堆内存特别大，集群通用设定不够大，需要任务中设定的大一点 | fix | http://jira.mogujie.org/browse/BDA-253 | 需要总结按脚本设定的方式和最佳实践.|环境配置




## 没有后续工作,或者后续工作做完的

问题|发现日期|问题简单描述|状态|详细情况跟踪Jira|后续工作|问题类型
--------|--------|--------|--------|--------|------------|------------|
hdata错误 | 2015-05-28| st_search_keyword_pc_output，java.sql.SQLException: Parameter index out of bounds. 12 is not between valid values of 1 and 11 | fix | http://jira.mogujie.org/browse/BDA-265 | 改进Pyramid Hive output任务, 减少数据落地, 减少中间步骤.|开发问题
dwd_uni_unidarenschedule_dump失败 | 2015-06-08 | 任务失败，ods_uni_unidarenschedule_20150607表不存在|fix | http://jira.mogujie.org/browse/BDA-304 @清远 ||环境配置
db连接超时后自动被kill导致lurker_stat_urlstat_c_output任务失败 | 2015-07-06|lurker_stat_urlstat_c_output表未建索引，导致导致连接超时被kill掉。|fix|http://jira.mogujie.org/browse/BDA-367 @清远||环境配置
account_pay_bill_export任务本身有sql错误 | 2015-07-06| Invalid table alias or column reference 'refundid':。|fix|http://jira.mogujie.org/browse/BDA-368 @南山||脚本问题
业务方创建分区但是没有文件，导致st_site_kpi_output执行错误 | 2015-07-10| 业务方没有提供分区文件,st_site_kpi_output执行错误| fix | http://jira.mogujie.org/browse/BDA-383 @清远 ||环境配置
网络事件路径分布output执行时数据长度与mysql表中path字段不符 | 2015-07-10| Caused by: java.sql.BatchUpdateException: Data truncation: Data too long for column 'path' at row 1| fix | http://jira.mogujie.org/browse/BDA-384 @杰伦 ||脚本问题
dw_trd_xshop_wide执行失败|2015-07-16|字段添加了，脚本没有改|fix|@馥雅||脚本问题
st_cpc_income_step2执行失败|2015-07-16|脚本修改后未保存|fix|@暖馨||脚本问题|
account_business_bill_export执行失败|2015-07-16|表名未修改|fix|@纳兰||脚本问题
奇克执行st_app_fashion_maimaimai时没有写表权限 | 2015-07-08|Authorization failed:No privilege 'Update' found for outputs { database:default, table:st_app_fastfashion_jingxuan}. Use show grant to get more details. | fix | @南山 http://jira.mogujie.org/browse/BDA-374 ||环境配置
dw_trd_item_advertise_stepend任务中Table not found 'tmp_trd_item_cpcxray20150626' | 2015-07-07| Table not found 'tmp_trd_item_cpcxray20150626'|fix|http://jira.mogujie.org/browse/BDA-371 @南山||脚本问题
fw_app_event_lx_count_day_output和fw_app_event_url_lx_count_day_output任务中导入mysql的记录数和原hive中记录数不相等 | 2015-07-06| 导入mysql的记录数和原hive中记录数不相等。|fix|http://jira.mogujie.org/browse/BDA-369 @清远||环境配置
st_title_post_output任务中使用的mysql表的字符集问题 | 2015-07-07| 表名：st_title_post，Incorrect string value: '\xF0\x9F\x98\x98+\xE5...' for column 'title'。将表和相应字段的字符集改为utf8mb4|fix|||环境配置
st_yungu_magic_search 任务失败|2015-07-03|文件拷贝失败，权限问题。|fix|@晓海 把文件所有者改为etlprd ||环境配置
dw_usr_zhongan_step2 任务失败|2015-07-03|空值异常|fix|@暖馨 where语句加入对于空值判断 ||脚本错误
st_im_whitename 任务失败|2015-07-03|表格列数不对|fix|@馥雅 文件修改后未保存 ||脚本错误
st_title_post_output任务失败|2015-07-03|导入mysql的记录数和原本记录数不相等对|fix|@雪伦  ||环境配置
mid_cps_directplanshopstat_output 任务失败|2015-07-03|output_data error (1317, 'Query execution was interrupted')|fix|@拓邪  ||脚本问题
上午批量任务执行失败 |2015-07-02|哨兵maser临时回滚到上个版本，导致同一时间执行中的批量任务受到影响，需要重新执行。|fix|http://jira.mogujie.org/browse/BDA-364 @无崖 @冰山 ||开发问题
dw_trd_shop_click_stepend任务失败|2015-07-01|Table insclause-0 has 51 columns, but query has 49 columns.|fix|@海贼 脚本未保存 ||脚本问题
user_action_from_search_data任务失败|2015-07-01|Table not found 'user_action_from_search_1'|fix|@洋平 业务表已经删除，脚本停用 ||脚本问题
st_trd_youdian_good_end任务失败|2015-06-26|Table not found 'tmp_st_trd_youdian_good_1xray20150625'|fix|@南山 http://data.mogujie.org/sche/tasklog/log.htm?id=1413496 ||脚本问题
data_prepare_mysql_data_meta失败|2015-06-25|元数据监控中没有注册meta|fix|http://data.mogujie.org/sche/tasklog/errorInfo.htm?id=1407398 @博文||环境配置
dwd_usr_tradevipuser_dump失败|2015-06-25|业务变更问题，对应的钻石会员业务下线|fix|http://data.mogujie.org/sche/tasklog/errorInfo.htm?id=1407321 @南山跟踪||环境配置
dwd_xd_marketabroadlive_dump中类型转换错误|2015-06-19|字符串转整数错误，java.lang.NumberFormatException: For input string: "澳洲"。|fix|@南山 http://jira.mogujie.org/browse/BDA-338  ||环境配置
提交到Sentinel失败,原因与gson有关|2015-06-11|6月11日16:00左右，Sentinel出错导致16个任务执行失败.java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 |Fix|@冰山 http://jira.mogujie.org/browse/BDA-311 ||开发问题
哨兵Client日志太多磁盘空间不够，导致任务失败 | 2015-06-10 |177磁盘空间不足导致失败，原因是日志文件太大未及时清理 | Fix | @冰山 http://jira.mogujie.org/browse/BDA-309 ||开发问题
st_im_daily_chat_num_output脚本错误 | 2015-06-10 | output_data error st_im_daily_chat_num 2015-06-09 create status file FAIL| fix | @千凡暂时停止任务。 ||脚本错误
st_trd_coupon_count临时表重复 | 2015-06-10 | 提示临时表不存在，原因是临时表命名重复| fix | @玄龄 ||脚本错误
dw_trd_shop_kpi_step7代码错误|2015-06-10|字段写错了Invalid column reference 'orderid'|fix|@海贼 ||脚本错误
特殊字符'\xF0\x9F\x9A\xB9\xE9\xA1...'进入mysql db时不兼容出错 |2015-06-01|st_site_mobile_searchkey_top10_output任务中又特殊字符导致进入mysql db时出错| fix |http://jira.mogujie.org/browse/BDA-278 ||环境配置
调度机制会自动禁用离职人的脚本，导致依赖任务没执行  | 2015-06-01 | combine_im_log_usr_action，dwd_usr_shoplevelmonthdetail_dump，st_im_daily_chat_person等任务有同样问题。| 跟踪  | http://jira.mogujie.org/browse/BDA-273 ||开发问题
moving data到指定路径时没错，使用该路径时报告不存在  | 2015-05-29 | st_app_topic任务 | fix  | http://jira.mogujie.org/browse/BDA-272 ||环境问题
st_trd_magic_outlets_list_output任务中MySQLConnection对象缺少属性  | 2015-05-28 | MySQLConnection object has no attribute _MySQLConnection__connection in > ignored  | fix  | http://jira.mogujie.org/browse/BDA-268 ||环境配置
脚步中drop表没生效  | 2015-05-28 | yichen_search_info任务中脚步先drop table，之后再创建，报表已经存在错误 | 重复  | http://jira.mogujie.org/browse/BDA-269 ||脚本问题
st_site_mgcms_cvt任务中有非法表名或字段 | 2015-05-28 | Invalid table alias or column reference | fix  | http://jira.mogujie.org/browse/BDA-266 | |脚本问题
脚步错误 | 2015-05-28 | st_cps_day，mid_site_cps_order_validmid_cps_order_introduce，mid_cps_order_introduce，mid_cps_user_first_orders 经南山确认是东惟脚步错误，已经解决| fix | | |脚本错误
频繁出现Dump失败 | 2015-05-21 开始跟踪, 之前一直都有 | 0:20左右的一批dump任务经常超时失败 | fix | http://jira.mogujie.org/browse/BDA-244| jarvis/sentinel等做了调度和流控.|开发问题
output错误|2015-05-27|st_yungu_magicshop_all_device_output等job是由于job配置时字符串里面包含了分隔符引起的，已经解决。st_yungu_magicshop_all_device_output是由于mysql表结构引起，bi组已经解决|fix|http://jira.mogujie.org/browse/BDA-255 | 无?|环境配置
临时表未找到(st_trd_magic_user_analysis)  | 2015-05-23 | Table not found 'tmp_st_trd_mobin_all_magic_spuxray20150514',一度发生，再执行又OK | 重复  |  ||开发问题
st_usr_nightmarket一直在执行中|2015-06-16|sql问题，处理大量数据，11个小时不能结束。|跟踪|http://jira.mogujie.org/browse/BDA-324 @南山 ||脚本问题
mid_cps_commodity_clicks mid_cpc_unioncommodity错误|2015-06-16|sql语法错误，拓邪提交的，目前南山和他沟通负责跟进。|跟踪|http://jira.mogujie.org/browse/BDA-323 @南山 ||脚本问题
st_cps_unioncpsadseffect_output错误|2015-06-15|output_data error (2006, 'MySQL server has gone away')。|跟踪|http://jira.mogujie.org/browse/BDA-314 @南山 ||环境配置
