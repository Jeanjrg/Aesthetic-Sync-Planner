package com.example.aestheticsyncplanner.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    @SerializedName("summary")
    private String title;
    private String description;
    @SerializedName("start")
    private EventTime start;
    @SerializedName("end")
    private EventTime end;
    private String category;
    private boolean isCompleted;

    public Event(String id, String title, String description, String startDate, String endDate, String category, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = new EventTime(startDate);
        this.end = new EventTime(endDate);
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStartDate() { return start != null ? start.getDateTime() : ""; }
    public String getEndDate() { return end != null ? end.getDateTime() : ""; }
    public String getCategory() { return category != null ? category : "Work"; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setStartDate(String date) { this.start = new EventTime(date); }
    public void setEndDate(String date) { this.end = new EventTime(date); }

    public static class EventTime implements Serializable {
        @SerializedName("dateTime")
        private String dateTime;
        @SerializedName("date")
        private String date;

        public EventTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getDateTime() {
            return dateTime != null ? dateTime : date;
        }
    }
}
