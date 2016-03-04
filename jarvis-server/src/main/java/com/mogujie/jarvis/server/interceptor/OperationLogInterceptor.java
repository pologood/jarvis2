package com.mogujie.jarvis.server.interceptor;

import com.google.inject.Inject;
import com.mogujie.jarvis.core.domain.OperationInfo;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dao.generate.OperationLogMapper;
import com.mogujie.jarvis.dao.generate.TaskMapper;
import com.mogujie.jarvis.dto.generate.*;
import com.mogujie.jarvis.dto.generate.OperationLog;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import java.util.Arrays;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;


/**
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 * User: 清远
 * mail: qingyuan@mogujie.com
 * date: 16/2/29
 * time: 上午10:48
 */

public class OperationLogInterceptor implements MethodInterceptor {

  @Inject
  private OperationLogMapper operationLogMapper;
  @Inject
  private JobMapper jobMapper;
  @Inject
  private TaskMapper taskMapper;
  private Logger LOGGER = Logger.getLogger(this.getClass());

  @Override
  public Object invoke(MethodInvocation invocation)
      throws Throwable {
    long start = System.nanoTime();
    try {
      if(invocation.getStaticPart().toString().indexOf(JobService.class.getCanonicalName()) != -1) {
        // add job operation log
        handleJobOpeLog(invocation);
      } else if(invocation.getStaticPart().toString().indexOf(TaskService.class.getCanonicalName()) != -1) {
        // add task operation log
        handleTaskOpeLog(invocation);
      }
      return invocation.proceed();
    } finally {
      LOGGER.info(String
          .format("Invocation of method %s () with parameters %s took %s ms.", invocation.getMethod().getName(),
              Arrays.toString(invocation.getArguments()), (System.nanoTime() - start) / 1000000.0));
    }
  }

  /**
   * 将insert,update,delete操作记录到DB中
   *
   * @param invocation
   */
  private void handleJobOpeLog(MethodInvocation invocation) {
    com.mogujie.jarvis.dto.generate.OperationLog operationLog = new OperationLog();

    String operation;
    try {
      operation = OperationInfo.valueOf(invocation.getMethod().getName().toUpperCase()).getDescription();
    } catch (IllegalArgumentException exception) {
      operation = invocation.getMethod().getName();
      LOGGER.error(String.format("method=%s is not register", invocation.getMethod().getName()));
    }
    if (operation == null) {
      operation = invocation.getMethod().getName();
    }

    Job job = null;
    if (invocation.getArguments().length == 1 && invocation.getArguments()[0] instanceof Job) {
      job = (Job) invocation.getArguments()[0];
    } else if (invocation.getMethod().getName().indexOf("delete") != -1 && invocation.getArguments().length > 0) {
      long jobId = (long) invocation.getArguments()[0];
      job = this.jobMapper.selectByPrimaryKey(jobId);
    }

    if(job == null) {
      return;
    }

    operationLog.setRefer(String.valueOf(job.getJobId()));
    operationLog.setOperator(job.getUpdateUser());
    operationLog.setTitle(job.getJobName());
    operationLog.setDetail(String.format("operation:%s\tcontent:%s", operation, job.getContent()));
    operationLog.setType("job");
    DateTime now = DateTime.now();
    operationLog.setOpeDate(now.toDate());

    this.operationLogMapper.insert(operationLog);

  }

  private void handleTaskOpeLog(MethodInvocation invocation) {
    OperationLog operationLog = new OperationLog();

    String operation;
    try {
      operation = OperationInfo.valueOf(invocation.getMethod().getName().toUpperCase()).getDescription();
    } catch (IllegalArgumentException exception) {
      operation = invocation.getMethod().getName();
      LOGGER.error(String.format("method=%s is not register", invocation.getMethod().getName()));
    }

    Task task = null;
    if (invocation.getArguments().length == 1 && invocation.getArguments()[0] instanceof Task) {
      task = (Task) invocation.getArguments()[0];
    } else if (invocation.getMethod().getName().indexOf("delete") != -1 && invocation.getArguments().length > 0) {
      long taskId = (long) invocation.getArguments()[0];
      task = this.taskMapper.selectByPrimaryKey(taskId);
    }

    if(task == null) {
      return;
    }

    operationLog.setRefer(String.valueOf(task.getTaskId()));
    operationLog.setOperator(task.getExecuteUser());
    operationLog.setTitle(String.valueOf(task.getJobId()));
    operationLog.setDetail(String.format("operation:%s\tcontent:%s", operation, task.getContent()));
    DateTime now = DateTime.now();
    operationLog.setOpeDate(now.toDate());
    operationLog.setType("task");

    this.operationLogMapper.insert(operationLog);
  }
}
