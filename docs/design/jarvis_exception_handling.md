- 当Worker与Server失联后，如何异常处理？
  
  Worker可自身维护任务的状态数据，并标记汇报状态（是否向Server汇报成功）。
  
  如因网络问题导致Worker无法向Server进行心跳与任务状态汇报超过一定时间时，Server将所有发送到该Worker的任务状态置为Unknow（或者Failed，但无法区分失败原因）。在Worker与Server失联期间，Worker将任务状态持久化，并定期重试向Server汇报任务状态。
  
  如因Worker crash导致的与Server失联，重启时从状态数据中进行状态恢复，将Success或Failed的任务正常向Server汇报，将Running的任务汇报为Failed。
  
  PS：为使Worker无状态化，可考虑将状态数据存储 StateStore 接口化，提供多种Store方式，如：LocalFileSystemStateStore、DistributionFileSystemStateStore、ZooKeeperStateStore等，支持自定义配置。也可以考虑把任务状态恢复的逻辑也接口化，各个任务类型自己来实现，比如：shell任务可以保持之前的做法把exitcode存到本地文件中，hive、mr任务向rm查询。