package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Job;

/**
 * Created by hejian on 15/9/17.
 */
public class JobVo extends Job {
    private String jobStatus;
    private String jobPriority;

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(String jobPriority) {
        this.jobPriority = jobPriority;
    }
}
