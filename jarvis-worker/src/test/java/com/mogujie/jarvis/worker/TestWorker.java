package com.mogujie.jarvis.worker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.worker.util.TaskConfigUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class TestWorker {

    @Test
    public void testJobXml() throws UnirestException {

        TaskConfigUtils.getRegisteredJobs().get("dummy");
        int i = 5;
    }

}
