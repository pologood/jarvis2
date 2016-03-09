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
import com.mogujie.jarvis.core.exception.AcceptanceException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceResult;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

/**
 * @author wuya
 *
 */
public class YarnMemoryAcceptanceStrategy implements AcceptanceStrategy {

    private int activeUriIndex = 0;
    private static final String DECIMAL_FORMAT = "#0.00";
    private static final Configuration CONFIG = ConfigUtils.getWorkerConfig();
    private static final double MAX_YARN_MEMORY_USAGE = CONFIG.getDouble(WorkerConfigKeys.YARN_MEMORY_USAGE_THRESHOLD, 0.9);
    private static final List<Object> YARN_REST_API_URIS = CONFIG.getList(WorkerConfigKeys.YARN_RESOUCEMANAGER_REST_API_URIS);

    @Override
    public AcceptanceResult accept() throws AcceptanceException {
        if (YARN_REST_API_URIS == null || YARN_REST_API_URIS.size() < 1) {
            throw new AcceptanceException("The value of " + WorkerConfigKeys.YARN_RESOUCEMANAGER_REST_API_URIS + " is invalid");
        }

        for (int i = 0, len = YARN_REST_API_URIS.size(); i < len; i++) {
            try {
                HttpResponse<JsonNode> response = Unirest.get(YARN_REST_API_URIS.get(activeUriIndex).toString()).asJson();
                JSONObject clusterMetrics = response.getBody().getObject().getJSONObject("clusterMetrics");
                int allocatedMB = clusterMetrics.getInt("allocatedMB");
                int totalMB = clusterMetrics.getInt("totalMB");
                double currentMemoryUsage = (double) allocatedMB / totalMB;
                if (Double.isNaN(currentMemoryUsage)) {
                    currentMemoryUsage = 0;
                }

                if (currentMemoryUsage < MAX_YARN_MEMORY_USAGE) {
                    return new AcceptanceResult(true, "");
                } else {
                    return new AcceptanceResult(false,
                            "Yarn集群当前内存使用率" + new DecimalFormat(DECIMAL_FORMAT).format(currentMemoryUsage) + ", 超过阈值" + MAX_YARN_MEMORY_USAGE);
                }
            } catch (UnirestException | JSONException e) {
                activeUriIndex = ++activeUriIndex % len;
            }
        }

        return new AcceptanceResult(false, "Can not get yarn cluster metrics");
    }

}
