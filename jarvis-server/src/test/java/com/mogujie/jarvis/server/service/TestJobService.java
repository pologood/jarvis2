package com.mogujie.jarvis.server.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mogujie.jarvis.dto.generate.Job;

/**
 * Created by muming on 15/11/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:context.xml")
public class TestJobService {
    @Autowired
    private  JobService jobService;

    @Test
    public void testGetActiveJobs(){
        List<Job> jobs = jobService.getNotDeletedJobs();
        Assert.assertTrue(!jobs.isEmpty());
    }

}
