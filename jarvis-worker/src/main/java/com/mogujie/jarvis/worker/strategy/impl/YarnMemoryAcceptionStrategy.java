/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:57:49
 */

package com.mogujie.jarvis.worker.strategy.impl;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.exeception.AcceptionException;
import com.mogujie.jarvis.worker.strategy.AcceptionResult;
import com.mogujie.jarvis.worker.strategy.AcceptionStrategy;

/**
 * @author wuya
 *
 */
public class YarnMemoryAcceptionStrategy implements AcceptionStrategy {

  private int activeUriIndex = 0;
  private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  private static final Configuration CONFIG = ConfigUtils.getWorkerConfig();
  private static final double MAX_YARN_MEMORY_USAGE = CONFIG
      .getDouble("yarn.memory.usage.threshold", 0.9);
  private static final List<Object> YARN_REST_API_URIS = CONFIG
      .getList("yarn.resoucemanager.rest.api.uris");

  @Override
  public AcceptionResult accept() throws AcceptionException {
    for (int i = 0, len = YARN_REST_API_URIS.size(); i < len; i++) {
      try {
        HttpResponse<JsonNode> response = Unirest
            .get(YARN_REST_API_URIS.get(activeUriIndex).toString()).asJson();
        JSONObject clusterMetrics = response.getBody().getObject().getJSONObject("clusterMetrics");
        int allocatedMB = clusterMetrics.getInt("allocatedMB");
        int totalMB = clusterMetrics.getInt("totalMB");
        double currentMemoryUsage = (double) allocatedMB / totalMB;
        if (currentMemoryUsage < MAX_YARN_MEMORY_USAGE) {
          return new AcceptionResult(true, "");
        } else {
          return new AcceptionResult(false, "Yarn集群当前内存使用率"
              + decimalFormat.format(currentMemoryUsage) + ", 超过阈值" + MAX_YARN_MEMORY_USAGE);
        }
      } catch (UnirestException | JSONException e) {
        activeUriIndex = ++activeUriIndex % len;
      }
    }

    return new AcceptionResult(false, "Can not get yarn cluster metrics");
  }

}
