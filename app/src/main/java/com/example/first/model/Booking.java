package com.example.first.model;

public class Booking {

    private String id;
    private String field;
    private String time;

    public Booking() {
        // مطلوب لفيربيس
    }

    public Booking(String id, String field, String time) {
        this.id = id;
        this.field = field;
        this.time = time;
    }

    public String getField() {
        return field;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(String id) {
        this.id = id;
    }
}