package com.mogujie.jarvis.dto.generate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDependExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TaskDependExample() {
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

        public Criteria andTaskIdIsNull() {
            addCriterion("taskId is null");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNotNull() {
            addCriterion("taskId is not null");
            return (Criteria) this;
        }

        public Criteria andTaskIdEqualTo(Long value) {
            addCriterion("taskId =", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotEqualTo(Long value) {
            addCriterion("taskId <>", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThan(Long value) {
            addCriterion("taskId >", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThanOrEqualTo(Long value) {
            addCriterion("taskId >=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThan(Long value) {
            addCriterion("taskId <", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThanOrEqualTo(Long value) {
            addCriterion("taskId <=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIn(List<Long> values) {
            addCriterion("taskId in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotIn(List<Long> values) {
            addCriterion("taskId not in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdBetween(Long value1, Long value2) {
            addCriterion("taskId between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotBetween(Long value1, Long value2) {
            addCriterion("taskId not between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsIsNull() {
            addCriterion("dependTaskIds is null");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsIsNotNull() {
            addCriterion("dependTaskIds is not null");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsEqualTo(String value) {
            addCriterion("dependTaskIds =", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsNotEqualTo(String value) {
            addCriterion("dependTaskIds <>", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsGreaterThan(String value) {
            addCriterion("dependTaskIds >", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsGreaterThanOrEqualTo(String value) {
            addCriterion("dependTaskIds >=", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsLessThan(String value) {
            addCriterion("dependTaskIds <", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsLessThanOrEqualTo(String value) {
            addCriterion("dependTaskIds <=", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsLike(String value) {
            addCriterion("dependTaskIds like", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsNotLike(String value) {
            addCriterion("dependTaskIds not like", value, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsIn(List<String> values) {
            addCriterion("dependTaskIds in", values, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsNotIn(List<String> values) {
            addCriterion("dependTaskIds not in", values, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsBetween(String value1, String value2) {
            addCriterion("dependTaskIds between", value1, value2, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andDependTaskIdsNotBetween(String value1, String value2) {
            addCriterion("dependTaskIds not between", value1, value2, "dependTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsIsNull() {
            addCriterion("childTaskIds is null");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsIsNotNull() {
            addCriterion("childTaskIds is not null");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsEqualTo(String value) {
            addCriterion("childTaskIds =", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsNotEqualTo(String value) {
            addCriterion("childTaskIds <>", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsGreaterThan(String value) {
            addCriterion("childTaskIds >", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsGreaterThanOrEqualTo(String value) {
            addCriterion("childTaskIds >=", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsLessThan(String value) {
            addCriterion("childTaskIds <", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsLessThanOrEqualTo(String value) {
            addCriterion("childTaskIds <=", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsLike(String value) {
            addCriterion("childTaskIds like", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsNotLike(String value) {
            addCriterion("childTaskIds not like", value, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsIn(List<String> values) {
            addCriterion("childTaskIds in", values, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsNotIn(List<String> values) {
            addCriterion("childTaskIds not in", values, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsBetween(String value1, String value2) {
            addCriterion("childTaskIds between", value1, value2, "childTaskIds");
            return (Criteria) this;
        }

        public Criteria andChildTaskIdsNotBetween(String value1, String value2) {
            addCriterion("childTaskIds not between", value1, value2, "childTaskIds");
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