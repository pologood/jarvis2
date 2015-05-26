# 调度平台问题汇总


## 问题汇总

问题汇总只是一个列表, 具体详情见Jira

问题|发现日期/开始跟踪日期|问题简单描述|状态|详细情况跟踪Jira
--------|--------|--------|--------|--------|--------
频繁出现Dump失败 | 2015-05-21 开始跟踪, 之前一直都有 | 0:20左右的一批dump任务经常超时失败 | 进行中 | http://jira.mogujie.org/browse/BDA-244
堆内存不够（dw_usr_visit_step3）| 2015-05-22 | 有些任务需要堆内存特别大，集群通用设定不够大，需要任务中设定的大一点 | fix |  |
临时表未找到(st_trd_magic_user_analysis)  | 2015-05-23 | Table not found 'tmp_st_trd_mobin_all_magic_spuxray20150514',一度发生，再执行又OK | 跟踪  |  |
yarn成功后未反应(st_app_trade_categories)  | 2015-05-23 | 任务执行成功后,哨兵系统未反应，一直提示执行中，最后只能kill掉 | 跟踪  | http://jira.mogujie.org/browse/BDA-252


