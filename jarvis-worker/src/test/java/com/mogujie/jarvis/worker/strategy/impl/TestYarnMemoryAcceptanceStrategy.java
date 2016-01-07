/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月7日 上午11:34:23
 */

package com.mogujie.jarvis.worker.strategy.impl;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mogujie.jarvis.core.exception.AcceptanceException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Unirest.class, ConfigUtils.class })
public class TestYarnMemoryAcceptanceStrategy {

    private JsonNode jsonNode = new JsonNode("{\"clusterMetrics\":{\"allocatedMB\":45678,\"totalMB\":1234567}}");

    @Before
    public void setup() throws UnirestException {
        @SuppressWarnings("unchecked")
        HttpResponse<JsonNode> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.getBody()).thenReturn(jsonNode);

        GetRequest getRequest = Mockito.mock(GetRequest.class);
        Mockito.when(getRequest.asJson()).thenReturn(response);

        PowerMockito.mockStatic(Unirest.class);
        Mockito.when(Unirest.get(Mockito.anyString())).thenReturn(getRequest);
    }

    @Test
    public void testAccept() throws AcceptanceException {
        Configuration config = new PropertiesConfiguration();
        config.addProperty(WorkerConfigKeys.YARN_MEMORY_USAGE_THRESHOLD, 0.9);
        config.addProperty(WorkerConfigKeys.YARN_RESOUCEMANAGER_REST_API_URIS, Lists.newArrayList("", ""));

        PowerMockito.mockStatic(ConfigUtils.class);
        Mockito.when(ConfigUtils.getWorkerConfig()).thenReturn(config);

        AcceptanceStrategy acceptanceStrategy = new YarnMemoryAcceptanceStrategy();
        double threshold = ConfigUtils.getWorkerConfig().getDouble(WorkerConfigKeys.YARN_MEMORY_USAGE_THRESHOLD, 0.9);
        Assert.assertEquals(acceptanceStrategy.accept().isAccepted(), 45678.0 / 1234567 < threshold);
    }
}
