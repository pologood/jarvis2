package com.mogujie.jarvis.core.domain;

/**
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 * User: 清远
 * mail: qingyuan@mogujie.com
 * date: 16/3/3
 * time: 下午12:08
 */
public enum OperationInfo {

  // job related
  INSERTJOB("insertJob", "新增job"),
  INSERTJOBDEPEND("insertJobDepend", "新增job依赖关系"),
  INSERTSCHEDULEEXPRESSION("insertScheduleExpression", "插入job的计划表达式"),
  UPDATEJOB("updateJob", "更新job"),
  UPDATESTATUS("updateStatus", "更新job状态"),
  UPDATEJOBDEPEND("updateJobDepend", "更新job的依赖关系"),
  UPDATESCHEDULEEXPRESSION("updateScheduleExpression", "更新job的计划表达式"),
  DELETEJOB("deleteJob", "删除JOB"),
  DELETEJOBANDRELATION("deleteJobAndRelation", "删除job和job的依赖关系"),
  DELETEJOBDEPEND("deleteJobDepend", "删除JOB依赖关系"),
  DELETEJOBDEPENDBYJOBID("deleteJobDependByJobId", "根据jodId删除job依赖关系"),
  DELETEJOBDEPENDBYPREJOBID("deleteJobDependByPreJobId", "根据前置jobId删除job依赖关系"),
  DELETESCHEDULEEXPRESSION("deleteScheduleExpression", "删除job的计划列表达式"),
  DELETESCHEDULEEXPRESSIONBYJOBID("deleteScheduleExpressionByJobId", "根据jobId删除job的计划表达式"),

  // task related
  CREATETASKBYJOBID("createTaskByJobId", "根据jodId创建task"),
  UPDATESTATUSWITHEND("updateStatusWithEnd", "根据endTime修改task的状态");

  OperationInfo(String name, String description) {
    this.name = name;
    this.description = description;
  }

  private String name;
  private String description;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
