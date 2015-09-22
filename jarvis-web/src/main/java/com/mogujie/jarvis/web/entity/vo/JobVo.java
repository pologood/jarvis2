package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Job;

/**
 * Created by hejian on 15/9/17.
 */
public class JobVo extends Job {
    private String jobStatus;
    private String jobPriority;


    private String createTimeStr;
    private String updateTimeStr;
    private String activeStartDateStr;
    private String activeEndDateStr;

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

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public String getActiveStartDateStr() {
        return activeStartDateStr;
    }

    public void setActiveStartDateStr(String activeStartDateStr) {
        this.activeStartDateStr = activeStartDateStr;
    }

    public String getActiveEndDateStr() {
        return activeEndDateStr;
    }

    public void setActiveEndDateStr(String activeEndDateStr) {
        this.activeEndDateStr = activeEndDateStr;
    }
}
