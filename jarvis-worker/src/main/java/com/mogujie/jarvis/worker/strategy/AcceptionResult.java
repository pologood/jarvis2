/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:51:57
 */

package com.mogujie.jarvis.worker.strategy;

/**
 * @author wuya
 *
 */
public class AcceptionResult {

  private boolean accepted;
  private String message;

  public AcceptionResult(boolean accepted, String message) {
    this.accepted = accepted;
    this.message = message;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public String getMessage() {
    return message;
  }

}
