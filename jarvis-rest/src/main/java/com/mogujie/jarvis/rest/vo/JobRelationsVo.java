package com.mogujie.jarvis.rest.vo;

import java.util.List;

/**
 * job返回类
 * @author muming
 */
public class JobRelationsVo extends  AbstractVo{

    private List<JobFlagEntry> list;

    public List<JobFlagEntry> getList() {
        return list;
    }
    public void setList(List<JobFlagEntry> list) {
        this.list = list;
    }
    public static class JobFlagEntry{
        private long jobId;
        private int jobFlag;
        public long getJobId() {
            return jobId;
        }
        public JobFlagEntry setJobId(long jobId) {
            this.jobId = jobId;
            return this;
        }
        public int getJobFlag() {
            return jobFlag;
        }
        public JobFlagEntry setJobFlag(int jobFlag) {
            this.jobFlag = jobFlag;
            return this;
        }
    }
}
