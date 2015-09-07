/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午1:44:53
 */

package com.mogujie.jarvis.worker;

import java.util.concurrent.Callable;

import com.mogujie.jarvis.core.job.AbstractJob;

public class JobCallable implements Callable<Boolean> {

  private AbstractJob job;

  public JobCallable(AbstractJob job) {
    this.job = job;
  }

  @Override
  public Boolean call() throws Exception {
    boolean result = false;
    job.preExecute();
    result = job.execute();
    job.postExecute();
    return result;
  }

}
