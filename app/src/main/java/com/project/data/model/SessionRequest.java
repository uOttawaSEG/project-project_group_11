package com.project.data.model;

import java.util.Date;
import com.google.firebase.Timestamp;

public class SessionRequest {
    private String sessionID;
    private String slotID;
    private String tutorID;
    private String studentID;
    private String courseName;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status;

    public SessionRequest() {}

    public SessionRequest(String sessionID, String slotID, String tutorID, String studentID, String courseName, Timestamp startDate, Timestamp endDate, String status) {
        this.sessionID = sessionID;
        this.slotID = slotID;
        this.tutorID = tutorID;
        this.studentID = studentID;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }


    public String getSessionID() {
        return sessionID;
    }

    public String getSlotID() {
        return slotID;
    }

    public String getTutorID() {
        return tutorID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getCourseName() {
        return courseName;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }

    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
