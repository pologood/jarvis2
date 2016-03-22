package com.mogujie.jarvis.dto.generate;

import java.util.Date;

public class OperationLog {
    private Integer id;

    private String title;

    private String operator;

    private String refer;

    private Date opeDate;

    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public Date getOpeDate() {
        return opeDate;
    }

    public void setOpeDate(Date opeDate) {
        this.opeDate = opeDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}