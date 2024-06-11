package com.example.zoomagymmanagement.model;

import java.io.Serializable;

public class FitnessPlansModel implements Serializable {
    String planId, title, details, image;

    public FitnessPlansModel() {
    }

    public FitnessPlansModel(String planId, String title, String details, String image) {
        this.planId = planId;
        this.title = title;
        this.details = details;
        this.image = image;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
