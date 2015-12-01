package com.mogujie.jarvis.rest.vo;

import com.mogujie.jarvis.core.domain.OperationMode;

import java.util.List;

/**
 * job返回类
 * @author muming
 */
public class JobEntryVo extends  AbstractVo{

    private long jobId;
    private String jobName;
    private String jobType;
    private Integer jobFlag;
    private String content;
    private List<ParameterEntity> params;
    private Integer workerGroupId;
    private Integer priority;
    private Integer activeStartTime;
    private Integer activeEndTime;
    private Integer rejectAttempts;
    private Integer rejectInterval;
    private Integer failedAttempts;
    private Integer failedInterval;
    private List<DependencyEntry> dependencyList;
    private ScheduleExpressionEntry scheduleExpressionEntry;

    public class ParameterEntity{
        private String key;
        private String value;
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public class DependencyEntry{
        private OperationMode operatorMode;
        private Long preJobId;
        private Integer commonStrategy;
        private String offsetStrategy;

        public OperationMode getOperatorMode() {
            return operatorMode;
        }
        public void setOperatorMode(OperationMode operatorMode) {
            this.operatorMode = operatorMode;
        }
        public Integer getCommonStrategy() {
            return commonStrategy;
        }
        public void setCommonStrategy(Integer commonStrategy) {
            this.commonStrategy = commonStrategy;
        }
        public String getOffsetStrategy() {
            return offsetStrategy;
        }
        public void setOffsetStrategy(String offsetStrategy) {
            this.offsetStrategy = offsetStrategy;
        }
        public Long getPreJobId() {
            return preJobId;
        }
        public void setPreJobId(Long preJobId) {
            this.preJobId = preJobId;
        }
    }

    public class ScheduleExpressionEntry{
        private OperationMode operatorMode;
        private Integer expressionType;
        private String expression;

        public OperationMode getOperatorMode() {
            return operatorMode;
        }
        public void setOperatorMode(OperationMode operatorMode) {
            this.operatorMode = operatorMode;
        }
        public Integer getExpressionType() {
            return expressionType;
        }
        public void setExpressionType(Integer expressionType) {
            this.expressionType = expressionType;
        }
        public String getExpression() {
            return expression;
        }
        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

    public long getJobId() {
        return jobId;
    }
    public void setJobId(long jobId) {
        this.jobId = jobId;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    public Integer getJobFlag() {
        return jobFlag;
    }
    public void setJobFlag(Integer jobFlag) {
        this.jobFlag = jobFlag;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<ParameterEntity> getParams() {
        return params;
    }
    public void setParams(List<ParameterEntity> params) {
        this.params = params;
    }
    public Integer getWorkerGroupId() {
        return workerGroupId;
    }
    public void setWorkerGroupId(Integer workerGroupId) {
        this.workerGroupId = workerGroupId;
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public Integer getActiveStartTime() {
        return activeStartTime;
    }
    public void setActiveStartTime(Integer activeStartTime) {
        this.activeStartTime = activeStartTime;
    }
    public Integer getActiveEndTime() {
        return activeEndTime;
    }
    public void setActiveEndTime(Integer activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    public Integer getRejectAttempts() {
        return rejectAttempts;
    }
    public void setRejectAttempts(Integer rejectAttempts) {
        this.rejectAttempts = rejectAttempts;
    }
    public Integer getRejectInterval() {
        return rejectInterval;
    }
    public void setRejectInterval(Integer rejectInterval) {
        this.rejectInterval = rejectInterval;
    }
    public Integer getFailedAttempts() {
        return failedAttempts;
    }
    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
    public Integer getFailedInterval() {
        return failedInterval;
    }
    public void setFailedInterval(Integer failedInterval) {
        this.failedInterval = failedInterval;
    }
    public List<DependencyEntry> getDependencyList() {
        return dependencyList;
    }
    public void setDependencyList(List<DependencyEntry> dependencyList) {
        this.dependencyList = dependencyList;
    }
    public ScheduleExpressionEntry getScheduleExpressionEntry() {
        return scheduleExpressionEntry;
    }
    public void setScheduleExpressionEntry(ScheduleExpressionEntry scheduleExpressionEntry) {
        this.scheduleExpressionEntry = scheduleExpressionEntry;
    }

    public Integer getActiveStartTime(Integer defaultValue) {
        return (activeStartTime != null) ? activeStartTime : defaultValue;
    }
    public Integer getActiveEndTime(Integer defaultValue) {
        return (activeEndTime != null) ? activeEndTime : defaultValue;
    }
    public Integer getPriority(Integer defaultValue) {
        return (priority != null) ? priority : defaultValue;
    }
    public Integer getRejectAttempts(Integer defaultValue) {
        return (rejectAttempts != null) ? rejectAttempts : defaultValue;
    }
    public Integer getRejectInterval(Integer defaultValue) {
        return (rejectInterval != null) ? rejectInterval : defaultValue;
    }
    public Integer getFailedAttempts(Integer defaultValue) {
        return (failedAttempts != null) ? failedAttempts : defaultValue;
    }
    public Integer getFailedInterval(Integer defaultValue) {
        return (failedInterval != null) ? failedInterval : defaultValue;
    }


}
