package com.example.zoomagymmanagement.model;

public class AttendanceModel {
    String attendanceId, userId, currentDate, dateTime;

    public AttendanceModel() {
    }

    public AttendanceModel(String attendanceId, String userId, String currentDate, String dateTime) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.currentDate = currentDate;
        this.dateTime = dateTime;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
