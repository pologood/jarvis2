package com.mogujie.jarvis.dto;

import java.util.Date;

public class AppWorkerGroup extends AppWorkerGroupKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app_worker_group.createTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app_worker_group.updateTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column app_worker_group.updateUser
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    private String updateUser;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app_worker_group.createTime
     *
     * @return the value of app_worker_group.createTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app_worker_group.createTime
     *
     * @param createTime the value for app_worker_group.createTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app_worker_group.updateTime
     *
     * @return the value of app_worker_group.updateTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app_worker_group.updateTime
     *
     * @param updateTime the value for app_worker_group.updateTime
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column app_worker_group.updateUser
     *
     * @return the value of app_worker_group.updateUser
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column app_worker_group.updateUser
     *
     * @param updateUser the value for app_worker_group.updateUser
     *
     * @mbggenerated Mon Oct 19 10:04:12 CST 2015
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}