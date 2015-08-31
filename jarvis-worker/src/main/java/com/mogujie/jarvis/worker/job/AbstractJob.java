/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:51:49
 */

package com.mogujie.jarvis.worker.job;

import com.mogujie.jarvis.worker.JobContext;
import com.mogujie.jarvis.worker.exception.JobException;

/**
 * @author wuya
 *
 */
public abstract class AbstractJob {

  private final JobContext jobContext;

  public AbstractJob(JobContext jobContext) {
    this.jobContext = jobContext;
  }

  public JobContext getJobContext() {
    return jobContext;
  }

  public void pre() throws JobException {
  }

  public abstract boolean execute() throws JobException;

  public void post() throws JobException {
  }

  public abstract boolean kill() throws JobException;
}
