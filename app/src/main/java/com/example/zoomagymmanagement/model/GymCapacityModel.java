package com.example.zoomagymmanagement.model;

import java.io.Serializable;

public class GymCapacityModel implements Serializable {
    String capacity;

    public GymCapacityModel() {
    }

    public GymCapacityModel(String capacity) {
        this.capacity = capacity;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
