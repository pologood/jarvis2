# 调度平台问题汇总


## 问题汇总

问题汇总只是一个列表, 具体详情见Jira

问题|发现日期/开始跟踪日期|问题简单描述|状态|详细情况跟踪Jira
--------|--------|--------|--------|--------|--------
提交到Sentinel失败,原因与gson有关|2015-06-11|6月11日16:00左右，Sentinel出错导致16个任务执行失败.java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 |跟踪|http://jira.mogujie.org/browse/BDA-311
资源管理程序yarn出问题导致任务执行失败|2015-06-12|资源管理程序yarn出问题导致任务执行失|跟踪|http://jira.mogujie.org/browse/BDA-312
ironMan提交大任务导致资源占用|2015-06-11|ironMan提交大任务导致资源占用,却又通过了maps检查|跟踪|http://jira.mogujie.org/browse/BDA-313
哨兵Client日志太多磁盘空间不够，导致任务失败 | 2015-06-10 |177磁盘空间不足导致失败，原因是日志文件太大未及时清理 | 跟踪 | @冰山 http://jira.mogujie.org/browse/BDA-309
st_im_daily_chat_num_output脚本错误 | 2015-06-10 | output_data error st_im_daily_chat_num 2015-06-09 create status file FAIL| fix | @千凡暂时停止任务。
st_trd_coupon_count临时表重复 | 2015-06-10 | 提示临时表不存在，原因是临时表命名重复| fix | @玄龄
st_trd_coupon_count_output文件读取失败,再执行后成功|2015-06-10|/apps/hive/warehouse/etlprd/output/st_trd_coupon_count.20150609.data.gz._COPYING_ (inode 280379051): File does not exist.|跟踪|@清远
dw_trd_shop_kpi_step7代码错误|2015-06-10|字段写错了Invalid column reference 'orderid'|fix|@海贼
hive任务成功后被hang住 | 2015-06-08 | st_trd_complaint_detail任务执行成功了，但是进程没结束，导致任务被提示错误| 跟踪 | http://jira.mogujie.org/browse/BDA-303
dwd_uni_unidarenschedule_dump失败 | 2015-06-08 | 任务失败，ods_uni_unidarenschedule_20150607表不存在| 跟踪 | http://jira.mogujie.org/browse/BDA-304
hadoop集群出现峰值负载过高 | 2015-06-05 | 2015.6.5，2:22 ~2:27 hadoop集群出现峰值负载过高情况，观察到是mofa8023这台机器内存和cpu使用率都达到了100%。| 跟踪 | http://jira.mogujie.org/browse/BDA-299
dwd_trd_punish_cheat_dump任务失败 | 2015-06-05| DB多了一个字段，需要在hive中加上相应的字段 | fix | http://jira.mogujie.org/browse/BDA-298
output任务丢失数据库连接造成任务失败| 2015-06-03|st_magic_activity_output，2015-06-03 17:26:15,119 - ERROR - output_data error (2013, "Lost connection to MySQL server at 'reading initial communication packet', system error: 111") | 跟踪 | http://jira.mogujie.org/browse/BDA-297
特殊字符'\xF0\x9F\x9A\xB9\xE9\xA1...'进入mysql db时不兼容出错 |2015-06-01|st_site_mobile_searchkey_top10_output任务中又特殊字符导致进入mysql db时出错| fix |http://jira.mogujie.org/browse/BDA-278
调度机制会自动禁用离职人的脚本，导致依赖任务没执行  | 2015-06-01 | combine_im_log_usr_action，dwd_usr_shoplevelmonthdetail_dump，st_im_daily_chat_person等任务有同样问题。| 跟踪  | http://jira.mogujie.org/browse/BDA-273
dwd_usr_shoplevelmonthdetail_dump任务java.lang.ClassCastException  | 2015-05-30 | 人工填写脚本错误 | fix  | http://jira.mogujie.org/browse/BDA-271
moving data到指定路径时没错，使用该路径时报告不存在  | 2015-05-29 | st_app_topic任务 | fix  | http://jira.mogujie.org/browse/BDA-272
任务执行时缺少权限  | 2015-05-29 | st_site_magic_outlets_group_output，t_dongcheng_app_push2_output等任务执行时python权限问题 | fix  | http://jira.mogujie.org/browse/BDA-270
脚步中drop表没生效  | 2015-05-28 | yichen_search_info任务中脚步先drop table，之后再创建，报表已经存在错误 | 重复  | http://jira.mogujie.org/browse/BDA-269
st_trd_magic_outlets_list_output任务中MySQLConnection对象缺少属性  | 2015-05-28 | MySQLConnection object has no attribute _MySQLConnection__connection in > ignored  | fix  | http://jira.mogujie.org/browse/BDA-268
st_site_mgcms_cvt任务中有非法表名或字段 | 2015-05-28 | Invalid table alias or column reference | fix  | http://jira.mogujie.org/browse/BDA-266
任务配置错误 | 2015-05-28 | dw_usr_zhongan_snapshot_output taskName:dw_usr_zhangan_snapshot不存在 清远已经解决，根本原因是hbase源时,需要手动创建导出脚本语句 | fix |
脚步错误 | 2015-05-28 | st_cps_day，mid_site_cps_order_validmid_cps_order_introduce，mid_cps_order_introduce，mid_cps_user_first_orders 经南山确认是东惟脚步错误，已经解决| fix | 
hdata错误 | 2015-05-28, 之前一直都有 | st_search_keyword_pc_output，java.sql.SQLException: Parameter index out of bounds. 12 is not between valid values of 1 and 11 | fix | http://jira.mogujie.org/browse/BDA-265
metadata_prepare error | 2015-05-28 | data_prepare_mysql_bda,MySql bda 不存在。根本原因是原先mysql有master和slave，现在仅有master，以后再有slave时不会有此问题 | fix | 
频繁出现Dump失败 | 2015-05-21 开始跟踪, 之前一直都有 | 0:20左右的一批dump任务经常超时失败 | fix | http://jira.mogujie.org/browse/BDA-244
堆内存不够（dw_usr_visit_step3）| 2015-05-22 | 有些任务需要堆内存特别大，集群通用设定不够大，需要任务中设定的大一点 | fix | http://jira.mogujie.org/browse/BDA-253
临时表未找到(st_trd_magic_user_analysis)  | 2015-05-23 | Table not found 'tmp_st_trd_mobin_all_magic_spuxray20150514',一度发生，再执行又OK | 跟踪  |  |
output错误|2015-05-27|st_yungu_magicshop_all_device_output等job是由于job配置时字符串里面包含了分隔符引起的，已经解决。st_yungu_magicshop_all_device_output是由于mysql表结构引起，bi组已经解决|fix|http://jira.mogujie.org/browse/BDA-255

