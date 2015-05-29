# 调度平台问题汇总


## 问题汇总

问题汇总只是一个列表, 具体详情见Jira

问题|发现日期/开始跟踪日期|问题简单描述|状态|详细情况跟踪Jira
--------|--------|--------|--------|--------|--------
脚步中drop表没生效  | 2015-05-28 | yichen_search_info任务中脚步先drop table，之后再创建，报表已经存在错误 | 跟踪  | http://jira.mogujie.org/browse/BDA-269
任务执行时缺少权限  | 2015-05-28 | st_site_magic_outlets_group_output，t_dongcheng_app_push2_output等任务执行时python权限问题 | fix  | http://jira.mogujie.org/browse/BDA-270
st_trd_magic_outlets_list_output任务中MySQLConnection对象缺少属性  | 2015-05-28 | MySQLConnection object has no attribute _MySQLConnection__connection in > ignored  | fix  | http://jira.mogujie.org/browse/BDA-268
st_site_mgcms_cvt任务中有非法表名或字段 | 2015-05-28 | Invalid table alias or column reference | fix  | http://jira.mogujie.org/browse/BDA-266
任务配置错误 | 2015-05-28 | dw_usr_zhongan_snapshot_output taskName:dw_usr_zhangan_snapshot不存在 清远已经解决，根本原因是hbase源时,需要手动创建导出脚本语句 | fix |
脚步错误 | 2015-05-28 | st_cps_day，mid_site_cps_order_validmid_cps_order_introduce，mid_cps_order_introduce，mid_cps_user_first_orders 经南山确认是东惟脚步错误，已经解决| fix | 
hdata错误 | 2015-05-28, 之前一直都有 | st_search_keyword_pc_output，java.sql.SQLException: Parameter index out of bounds. 12 is not between valid values of 1 and 11 | 进行中 | http://jira.mogujie.org/browse/BDA-265
metadata_prepare error | 2015-05-28 | data_prepare_mysql_bda,MySql bda 不存在。根本原因是原先mysql有master和slave，现在仅有master，以后再有slave时不会有此问题 | fix | 
频繁出现Dump失败 | 2015-05-21 开始跟踪, 之前一直都有 | 0:20左右的一批dump任务经常超时失败 | 进行中 | http://jira.mogujie.org/browse/BDA-244
堆内存不够（dw_usr_visit_step3）| 2015-05-22 | 有些任务需要堆内存特别大，集群通用设定不够大，需要任务中设定的大一点 | fix | http://jira.mogujie.org/browse/BDA-253
临时表未找到(st_trd_magic_user_analysis)  | 2015-05-23 | Table not found 'tmp_st_trd_mobin_all_magic_spuxray20150514',一度发生，再执行又OK | 跟踪  |  |
yarn成功后未反应(st_app_trade_categories)  | 2015-05-23 | 任务执行成功后,哨兵系统未反应，一直提示执行中，最后只能kill掉 | 跟踪  | http://jira.mogujie.org/browse/BDA-252
output错误|2015-05-27|st_yungu_magicshop_all_device_output等job是由于job配置时字符串里面包含了分隔符引起的，已经解决。st_yungu_magicshop_all_device_output是由于mysql表结构引起，bi组已经解决|fix|http://jira.mogujie.org/browse/BDA-255

