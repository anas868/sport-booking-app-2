package com.example.first.model;

public class Booking {

    private String id;
    private String field;
    private String day;
    private String date;
    private String time;
    private String duration;
    private String ownerEmail;

    public Booking() {
    }

    public Booking(String id, String field, String day, String date, String time, String duration, String ownerEmail) {
        this.id = id;
        this.field = field;
        this.day = day;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.ownerEmail = ownerEmail;
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}