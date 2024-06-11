package com.example.zoomagymmanagement.model;

import java.io.Serializable;

public class FeedbackModel implements Serializable {
    String feedbackId, userId, subject, details, dateTime, image, adminReply, status;

    public FeedbackModel() {
    }

    public FeedbackModel(String feedbackId, String userId, String subject, String details, String dateTime, String image, String adminReply, String status) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.subject = subject;
        this.details = details;
        this.dateTime = dateTime;
        this.image = image;
        this.adminReply = adminReply;
        this.status = status;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
