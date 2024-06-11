package com.example.zoomagymmanagement.model;

import java.io.Serializable;

public class GoalModel implements Serializable {
    String goalId, userId, type, title, targetDate, target, achieved, status;

    public GoalModel() {
    }

    public GoalModel(String goalId, String userId, String type, String title, String targetDate, String target, String achieved, String status) {
        this.goalId = goalId;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.targetDate = targetDate;
        this.target = target;
        this.achieved = achieved;
        this.status = status;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAchieved() {
        return achieved;
    }

    public void setAchieved(String achieved) {
        this.achieved = achieved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
