package com.mogujie.jarvis.dto;

public class WorkerGroupRelationKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column worker_group_relation.workerId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    private Integer workerId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column worker_group_relation.wgroupId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    private Integer wgroupId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column worker_group_relation.workerId
     *
     * @return the value of worker_group_relation.workerId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    public Integer getWorkerId() {
        return workerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column worker_group_relation.workerId
     *
     * @param workerId the value for worker_group_relation.workerId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column worker_group_relation.wgroupId
     *
     * @return the value of worker_group_relation.wgroupId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    public Integer getWgroupId() {
        return wgroupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column worker_group_relation.wgroupId
     *
     * @param wgroupId the value for worker_group_relation.wgroupId
     *
     * @mbggenerated Fri Sep 11 17:27:51 CST 2015
     */
    public void setWgroupId(Integer wgroupId) {
        this.wgroupId = wgroupId;
    }
}