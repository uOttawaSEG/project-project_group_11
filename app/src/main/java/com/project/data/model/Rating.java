package com.project.data.model;

import java.io.Serializable;

public class Rating implements Serializable {
    private String ratingId;
    private String sessionId;
    private String tutorId;
    private String studentId;
    private int rating;
    private long timestamp;

    public Rating() {
    }

    public Rating(String ratingId, String sessionId, String tutorId, String studentId, int rating, long timestamp) {
        this.ratingId = ratingId;
        this.sessionId = sessionId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
