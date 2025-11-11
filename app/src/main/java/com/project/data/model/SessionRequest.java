package com.project.data.model;

import java.io.Serializable;
import java.util.Date;

public class SessionRequest implements Serializable {
    private String sessionID;
    private String tutorID;
    private String studentID;
    private String courseName;
    private Date startDate;
    private Date endDate;
    private String status;

    public SessionRequest() {}

    public SessionRequest(String sessionID, String tutorID, String studentID, String courseName, Date startDate, Date endDate, String status) {
        this.sessionID = sessionID;
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

    public String getTutorID() {
        return tutorID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getCourseName() {
        return courseName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
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

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
