package com.project.data.model;

public class AvailabilitySlot {
    private String slotId;
    private String tutorId;
    private String course;
    private String date; // yyyy-mm-dd format
    private String startTime; // hh:mm format
    private String endTime; // hh:mm format
    private boolean autoApprove;
    private boolean isBooked;
    private long timestamp;

    public AvailabilitySlot() {} // no arg constructor needed for firebase

    public AvailabilitySlot(String slotId, String tutorId, String course, String date, String startTime, String endTime, boolean autoApprove) {
        this.slotId = slotId;
        this.tutorId = tutorId;
        this.course = course;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.autoApprove = autoApprove;
        this.isBooked = false;
        this.timestamp = System.currentTimeMillis();
    }

    // getters
    public String getSlotId() { return slotId; }
    public String getTutorId() { return tutorId; }
    public String getCourse() { return course; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean isAutoApprove() { return autoApprove; }
    public boolean isBooked() { return isBooked; }
    public long getTimestamp() { return timestamp; }

    // setters

    public void setSlotId(String slotId) { this.slotId = slotId; }
    public void setTutorId(String tutorId) { this.tutorId = tutorId; }
    public void setCourse(String course) { this.course = course; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setAutoApprove(boolean autoApprove) { this.autoApprove = autoApprove; }
    public void setBooked(boolean booked) { isBooked = booked; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
