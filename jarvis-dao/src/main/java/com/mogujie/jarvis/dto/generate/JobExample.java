package com.mogujie.jarvis.dto.generate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public JobExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andJobIdIsNull() {
            addCriterion("jobId is null");
            return (Criteria) this;
        }

        public Criteria andJobIdIsNotNull() {
            addCriterion("jobId is not null");
            return (Criteria) this;
        }

        public Criteria andJobIdEqualTo(Long value) {
            addCriterion("jobId =", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotEqualTo(Long value) {
            addCriterion("jobId <>", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThan(Long value) {
            addCriterion("jobId >", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdGreaterThanOrEqualTo(Long value) {
            addCriterion("jobId >=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThan(Long value) {
            addCriterion("jobId <", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdLessThanOrEqualTo(Long value) {
            addCriterion("jobId <=", value, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdIn(List<Long> values) {
            addCriterion("jobId in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotIn(List<Long> values) {
            addCriterion("jobId not in", values, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdBetween(Long value1, Long value2) {
            addCriterion("jobId between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobIdNotBetween(Long value1, Long value2) {
            addCriterion("jobId not between", value1, value2, "jobId");
            return (Criteria) this;
        }

        public Criteria andJobNameIsNull() {
            addCriterion("jobName is null");
            return (Criteria) this;
        }

        public Criteria andJobNameIsNotNull() {
            addCriterion("jobName is not null");
            return (Criteria) this;
        }

        public Criteria andJobNameEqualTo(String value) {
            addCriterion("jobName =", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameNotEqualTo(String value) {
            addCriterion("jobName <>", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameGreaterThan(String value) {
            addCriterion("jobName >", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameGreaterThanOrEqualTo(String value) {
            addCriterion("jobName >=", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameLessThan(String value) {
            addCriterion("jobName <", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameLessThanOrEqualTo(String value) {
            addCriterion("jobName <=", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameLike(String value) {
            addCriterion("jobName like", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameNotLike(String value) {
            addCriterion("jobName not like", value, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameIn(List<String> values) {
            addCriterion("jobName in", values, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameNotIn(List<String> values) {
            addCriterion("jobName not in", values, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameBetween(String value1, String value2) {
            addCriterion("jobName between", value1, value2, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobNameNotBetween(String value1, String value2) {
            addCriterion("jobName not between", value1, value2, "jobName");
            return (Criteria) this;
        }

        public Criteria andJobTypeIsNull() {
            addCriterion("jobType is null");
            return (Criteria) this;
        }

        public Criteria andJobTypeIsNotNull() {
            addCriterion("jobType is not null");
            return (Criteria) this;
        }

        public Criteria andJobTypeEqualTo(String value) {
            addCriterion("jobType =", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeNotEqualTo(String value) {
            addCriterion("jobType <>", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeGreaterThan(String value) {
            addCriterion("jobType >", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeGreaterThanOrEqualTo(String value) {
            addCriterion("jobType >=", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeLessThan(String value) {
            addCriterion("jobType <", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeLessThanOrEqualTo(String value) {
            addCriterion("jobType <=", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeLike(String value) {
            addCriterion("jobType like", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeNotLike(String value) {
            addCriterion("jobType not like", value, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeIn(List<String> values) {
            addCriterion("jobType in", values, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeNotIn(List<String> values) {
            addCriterion("jobType not in", values, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeBetween(String value1, String value2) {
            addCriterion("jobType between", value1, value2, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobTypeNotBetween(String value1, String value2) {
            addCriterion("jobType not between", value1, value2, "jobType");
            return (Criteria) this;
        }

        public Criteria andJobFlagIsNull() {
            addCriterion("jobFlag is null");
            return (Criteria) this;
        }

        public Criteria andJobFlagIsNotNull() {
            addCriterion("jobFlag is not null");
            return (Criteria) this;
        }

        public Criteria andJobFlagEqualTo(Integer value) {
            addCriterion("jobFlag =", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagNotEqualTo(Integer value) {
            addCriterion("jobFlag <>", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagGreaterThan(Integer value) {
            addCriterion("jobFlag >", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("jobFlag >=", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagLessThan(Integer value) {
            addCriterion("jobFlag <", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagLessThanOrEqualTo(Integer value) {
            addCriterion("jobFlag <=", value, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagIn(List<Integer> values) {
            addCriterion("jobFlag in", values, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagNotIn(List<Integer> values) {
            addCriterion("jobFlag not in", values, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagBetween(Integer value1, Integer value2) {
            addCriterion("jobFlag between", value1, value2, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andJobFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("jobFlag not between", value1, value2, "jobFlag");
            return (Criteria) this;
        }

        public Criteria andContentIsNull() {
            addCriterion("content is null");
            return (Criteria) this;
        }

        public Criteria andContentIsNotNull() {
            addCriterion("content is not null");
            return (Criteria) this;
        }

        public Criteria andContentEqualTo(String value) {
            addCriterion("content =", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotEqualTo(String value) {
            addCriterion("content <>", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThan(String value) {
            addCriterion("content >", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThanOrEqualTo(String value) {
            addCriterion("content >=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThan(String value) {
            addCriterion("content <", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThanOrEqualTo(String value) {
            addCriterion("content <=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLike(String value) {
            addCriterion("content like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotLike(String value) {
            addCriterion("content not like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentIn(List<String> values) {
            addCriterion("content in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotIn(List<String> values) {
            addCriterion("content not in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentBetween(String value1, String value2) {
            addCriterion("content between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotBetween(String value1, String value2) {
            addCriterion("content not between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andParamsIsNull() {
            addCriterion("params is null");
            return (Criteria) this;
        }

        public Criteria andParamsIsNotNull() {
            addCriterion("params is not null");
            return (Criteria) this;
        }

        public Criteria andParamsEqualTo(String value) {
            addCriterion("params =", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsNotEqualTo(String value) {
            addCriterion("params <>", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsGreaterThan(String value) {
            addCriterion("params >", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsGreaterThanOrEqualTo(String value) {
            addCriterion("params >=", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsLessThan(String value) {
            addCriterion("params <", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsLessThanOrEqualTo(String value) {
            addCriterion("params <=", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsLike(String value) {
            addCriterion("params like", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsNotLike(String value) {
            addCriterion("params not like", value, "params");
            return (Criteria) this;
        }

        public Criteria andParamsIn(List<String> values) {
            addCriterion("params in", values, "params");
            return (Criteria) this;
        }

        public Criteria andParamsNotIn(List<String> values) {
            addCriterion("params not in", values, "params");
            return (Criteria) this;
        }

        public Criteria andParamsBetween(String value1, String value2) {
            addCriterion("params between", value1, value2, "params");
            return (Criteria) this;
        }

        public Criteria andParamsNotBetween(String value1, String value2) {
            addCriterion("params not between", value1, value2, "params");
            return (Criteria) this;
        }

        public Criteria andSubmitUserIsNull() {
            addCriterion("submitUser is null");
            return (Criteria) this;
        }

        public Criteria andSubmitUserIsNotNull() {
            addCriterion("submitUser is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitUserEqualTo(String value) {
            addCriterion("submitUser =", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserNotEqualTo(String value) {
            addCriterion("submitUser <>", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserGreaterThan(String value) {
            addCriterion("submitUser >", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserGreaterThanOrEqualTo(String value) {
            addCriterion("submitUser >=", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserLessThan(String value) {
            addCriterion("submitUser <", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserLessThanOrEqualTo(String value) {
            addCriterion("submitUser <=", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserLike(String value) {
            addCriterion("submitUser like", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserNotLike(String value) {
            addCriterion("submitUser not like", value, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserIn(List<String> values) {
            addCriterion("submitUser in", values, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserNotIn(List<String> values) {
            addCriterion("submitUser not in", values, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserBetween(String value1, String value2) {
            addCriterion("submitUser between", value1, value2, "submitUser");
            return (Criteria) this;
        }

        public Criteria andSubmitUserNotBetween(String value1, String value2) {
            addCriterion("submitUser not between", value1, value2, "submitUser");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNull() {
            addCriterion("priority is null");
            return (Criteria) this;
        }

        public Criteria andPriorityIsNotNull() {
            addCriterion("priority is not null");
            return (Criteria) this;
        }

        public Criteria andPriorityEqualTo(Integer value) {
            addCriterion("priority =", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotEqualTo(Integer value) {
            addCriterion("priority <>", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThan(Integer value) {
            addCriterion("priority >", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityGreaterThanOrEqualTo(Integer value) {
            addCriterion("priority >=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThan(Integer value) {
            addCriterion("priority <", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityLessThanOrEqualTo(Integer value) {
            addCriterion("priority <=", value, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityIn(List<Integer> values) {
            addCriterion("priority in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotIn(List<Integer> values) {
            addCriterion("priority not in", values, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityBetween(Integer value1, Integer value2) {
            addCriterion("priority between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andPriorityNotBetween(Integer value1, Integer value2) {
            addCriterion("priority not between", value1, value2, "priority");
            return (Criteria) this;
        }

        public Criteria andSerialFlagIsNull() {
            addCriterion("serialFlag is null");
            return (Criteria) this;
        }

        public Criteria andSerialFlagIsNotNull() {
            addCriterion("serialFlag is not null");
            return (Criteria) this;
        }

        public Criteria andSerialFlagEqualTo(Integer value) {
            addCriterion("serialFlag =", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagNotEqualTo(Integer value) {
            addCriterion("serialFlag <>", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagGreaterThan(Integer value) {
            addCriterion("serialFlag >", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("serialFlag >=", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagLessThan(Integer value) {
            addCriterion("serialFlag <", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagLessThanOrEqualTo(Integer value) {
            addCriterion("serialFlag <=", value, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagIn(List<Integer> values) {
            addCriterion("serialFlag in", values, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagNotIn(List<Integer> values) {
            addCriterion("serialFlag not in", values, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagBetween(Integer value1, Integer value2) {
            addCriterion("serialFlag between", value1, value2, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andSerialFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("serialFlag not between", value1, value2, "serialFlag");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("appId is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("appId is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(Integer value) {
            addCriterion("appId =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(Integer value) {
            addCriterion("appId <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(Integer value) {
            addCriterion("appId >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("appId >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(Integer value) {
            addCriterion("appId <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(Integer value) {
            addCriterion("appId <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<Integer> values) {
            addCriterion("appId in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<Integer> values) {
            addCriterion("appId not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(Integer value1, Integer value2) {
            addCriterion("appId between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(Integer value1, Integer value2) {
            addCriterion("appId not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIsNull() {
            addCriterion("workerGroupId is null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIsNotNull() {
            addCriterion("workerGroupId is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdEqualTo(Integer value) {
            addCriterion("workerGroupId =", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotEqualTo(Integer value) {
            addCriterion("workerGroupId <>", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdGreaterThan(Integer value) {
            addCriterion("workerGroupId >", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("workerGroupId >=", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdLessThan(Integer value) {
            addCriterion("workerGroupId <", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdLessThanOrEqualTo(Integer value) {
            addCriterion("workerGroupId <=", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIn(List<Integer> values) {
            addCriterion("workerGroupId in", values, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotIn(List<Integer> values) {
            addCriterion("workerGroupId not in", values, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdBetween(Integer value1, Integer value2) {
            addCriterion("workerGroupId between", value1, value2, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotBetween(Integer value1, Integer value2) {
            addCriterion("workerGroupId not between", value1, value2, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateIsNull() {
            addCriterion("activeStartDate is null");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateIsNotNull() {
            addCriterion("activeStartDate is not null");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateEqualTo(Date value) {
            addCriterion("activeStartDate =", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateNotEqualTo(Date value) {
            addCriterion("activeStartDate <>", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateGreaterThan(Date value) {
            addCriterion("activeStartDate >", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateGreaterThanOrEqualTo(Date value) {
            addCriterion("activeStartDate >=", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateLessThan(Date value) {
            addCriterion("activeStartDate <", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateLessThanOrEqualTo(Date value) {
            addCriterion("activeStartDate <=", value, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateIn(List<Date> values) {
            addCriterion("activeStartDate in", values, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateNotIn(List<Date> values) {
            addCriterion("activeStartDate not in", values, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateBetween(Date value1, Date value2) {
            addCriterion("activeStartDate between", value1, value2, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveStartDateNotBetween(Date value1, Date value2) {
            addCriterion("activeStartDate not between", value1, value2, "activeStartDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateIsNull() {
            addCriterion("activeEndDate is null");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateIsNotNull() {
            addCriterion("activeEndDate is not null");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateEqualTo(Date value) {
            addCriterion("activeEndDate =", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateNotEqualTo(Date value) {
            addCriterion("activeEndDate <>", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateGreaterThan(Date value) {
            addCriterion("activeEndDate >", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateGreaterThanOrEqualTo(Date value) {
            addCriterion("activeEndDate >=", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateLessThan(Date value) {
            addCriterion("activeEndDate <", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateLessThanOrEqualTo(Date value) {
            addCriterion("activeEndDate <=", value, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateIn(List<Date> values) {
            addCriterion("activeEndDate in", values, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateNotIn(List<Date> values) {
            addCriterion("activeEndDate not in", values, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateBetween(Date value1, Date value2) {
            addCriterion("activeEndDate between", value1, value2, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andActiveEndDateNotBetween(Date value1, Date value2) {
            addCriterion("activeEndDate not between", value1, value2, "activeEndDate");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsIsNull() {
            addCriterion("rejectAttempts is null");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsIsNotNull() {
            addCriterion("rejectAttempts is not null");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsEqualTo(Integer value) {
            addCriterion("rejectAttempts =", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsNotEqualTo(Integer value) {
            addCriterion("rejectAttempts <>", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsGreaterThan(Integer value) {
            addCriterion("rejectAttempts >", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsGreaterThanOrEqualTo(Integer value) {
            addCriterion("rejectAttempts >=", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsLessThan(Integer value) {
            addCriterion("rejectAttempts <", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsLessThanOrEqualTo(Integer value) {
            addCriterion("rejectAttempts <=", value, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsIn(List<Integer> values) {
            addCriterion("rejectAttempts in", values, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsNotIn(List<Integer> values) {
            addCriterion("rejectAttempts not in", values, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsBetween(Integer value1, Integer value2) {
            addCriterion("rejectAttempts between", value1, value2, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectAttemptsNotBetween(Integer value1, Integer value2) {
            addCriterion("rejectAttempts not between", value1, value2, "rejectAttempts");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalIsNull() {
            addCriterion("rejectInterval is null");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalIsNotNull() {
            addCriterion("rejectInterval is not null");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalEqualTo(Integer value) {
            addCriterion("rejectInterval =", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalNotEqualTo(Integer value) {
            addCriterion("rejectInterval <>", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalGreaterThan(Integer value) {
            addCriterion("rejectInterval >", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalGreaterThanOrEqualTo(Integer value) {
            addCriterion("rejectInterval >=", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalLessThan(Integer value) {
            addCriterion("rejectInterval <", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalLessThanOrEqualTo(Integer value) {
            addCriterion("rejectInterval <=", value, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalIn(List<Integer> values) {
            addCriterion("rejectInterval in", values, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalNotIn(List<Integer> values) {
            addCriterion("rejectInterval not in", values, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalBetween(Integer value1, Integer value2) {
            addCriterion("rejectInterval between", value1, value2, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andRejectIntervalNotBetween(Integer value1, Integer value2) {
            addCriterion("rejectInterval not between", value1, value2, "rejectInterval");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsIsNull() {
            addCriterion("failedAttempts is null");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsIsNotNull() {
            addCriterion("failedAttempts is not null");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsEqualTo(Integer value) {
            addCriterion("failedAttempts =", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsNotEqualTo(Integer value) {
            addCriterion("failedAttempts <>", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsGreaterThan(Integer value) {
            addCriterion("failedAttempts >", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsGreaterThanOrEqualTo(Integer value) {
            addCriterion("failedAttempts >=", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsLessThan(Integer value) {
            addCriterion("failedAttempts <", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsLessThanOrEqualTo(Integer value) {
            addCriterion("failedAttempts <=", value, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsIn(List<Integer> values) {
            addCriterion("failedAttempts in", values, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsNotIn(List<Integer> values) {
            addCriterion("failedAttempts not in", values, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsBetween(Integer value1, Integer value2) {
            addCriterion("failedAttempts between", value1, value2, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedAttemptsNotBetween(Integer value1, Integer value2) {
            addCriterion("failedAttempts not between", value1, value2, "failedAttempts");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalIsNull() {
            addCriterion("failedInterval is null");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalIsNotNull() {
            addCriterion("failedInterval is not null");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalEqualTo(Integer value) {
            addCriterion("failedInterval =", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalNotEqualTo(Integer value) {
            addCriterion("failedInterval <>", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalGreaterThan(Integer value) {
            addCriterion("failedInterval >", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalGreaterThanOrEqualTo(Integer value) {
            addCriterion("failedInterval >=", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalLessThan(Integer value) {
            addCriterion("failedInterval <", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalLessThanOrEqualTo(Integer value) {
            addCriterion("failedInterval <=", value, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalIn(List<Integer> values) {
            addCriterion("failedInterval in", values, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalNotIn(List<Integer> values) {
            addCriterion("failedInterval not in", values, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalBetween(Integer value1, Integer value2) {
            addCriterion("failedInterval between", value1, value2, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andFailedIntervalNotBetween(Integer value1, Integer value2) {
            addCriterion("failedInterval not between", value1, value2, "failedInterval");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("createTime is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("createTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("createTime =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("createTime <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("createTime >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createTime >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("createTime <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("createTime <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("createTime in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("createTime not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("createTime between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("createTime not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("updateTime is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("updateTime is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("updateTime =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("updateTime <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("updateTime >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("updateTime >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("updateTime <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("updateTime <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("updateTime in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("updateTime not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("updateTime between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("updateTime not between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNull() {
            addCriterion("updateUser is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("updateUser is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(String value) {
            addCriterion("updateUser =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(String value) {
            addCriterion("updateUser <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(String value) {
            addCriterion("updateUser >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(String value) {
            addCriterion("updateUser >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(String value) {
            addCriterion("updateUser <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(String value) {
            addCriterion("updateUser <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLike(String value) {
            addCriterion("updateUser like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotLike(String value) {
            addCriterion("updateUser not like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<String> values) {
            addCriterion("updateUser in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<String> values) {
            addCriterion("updateUser not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(String value1, String value2) {
            addCriterion("updateUser between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(String value1, String value2) {
            addCriterion("updateUser not between", value1, value2, "updateUser");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}