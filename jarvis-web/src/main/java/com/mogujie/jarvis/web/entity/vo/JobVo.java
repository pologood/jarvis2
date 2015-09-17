package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Job;

/**
 * Created by hejian on 15/9/17.
 */
public class JobVo extends Job {
    private String jobStatus;

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
