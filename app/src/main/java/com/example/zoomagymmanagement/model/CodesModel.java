package com.example.zoomagymmanagement.model;

import java.io.Serializable;

public class CodesModel implements Serializable {
    String codeId, code, createdDate, bookedDate, userId, status;

    public CodesModel() {
    }

    public CodesModel(String codeId, String code, String createdDate, String bookedDate, String userId, String status) {
        this.codeId = codeId;
        this.code = code;
        this.createdDate = createdDate;
        this.bookedDate = bookedDate;
        this.userId = userId;
        this.status = status;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
