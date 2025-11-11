package com.project.data.model;

import java.io.Serializable;

public class SessionRequest implements Serializable {
    private String sessionID;
    private String tutorID;
    private String studentID;
    private String slotID;
    private String status;

    public SessionRequest() {}

    public SessionRequest(String sessionID, String tutorID, String studentID, String slotID, String status) {
        this.sessionID = sessionID;
        this.tutorID = tutorID;
        this.studentID = studentID;
        this.slotID = slotID;
        this.status = status;
    }

    // getters
    public String getSessionID() {
        return sessionID;
    }

    public String getTutorID() {
        return tutorID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getSlotID() {
        return slotID;
    }

    public String getStatus() {
        return status;
    }

    // setters
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }

    public void setStatus(String status) {
        this.slotID = status;
    }
}
