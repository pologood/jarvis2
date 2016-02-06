package com.mogujie.jarvis.web.entity.qo;

/**
 * @author  muming
 */
public class JobDependQo {

    private long jobId;
    private int showTaskStartTime;
    private int showTaskEndTime;

    public long getJobId() {
        return jobId;
    }

    public JobDependQo setJobId(long jobId) {
        this.jobId = jobId;
        return this;
    }

    public int getShowTaskStartTime() {
        return showTaskStartTime;
    }

    public JobDependQo setShowTaskStartTime(int showTaskStartTime) {
        this.showTaskStartTime = showTaskStartTime;
        return this;
    }

    public int getShowTaskEndTime() {
        return showTaskEndTime;
    }

    public JobDependQo setShowTaskEndTime(int showTaskEndTime) {
        this.showTaskEndTime = showTaskEndTime;
        return this;
    }
}
