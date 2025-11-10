package com.project.data.model;

import java.io.Serializable;
import java.util.List;

public class RegistrationRequest implements Serializable {
    private String userId; // firebase auth uid
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private String role;
    private List<String> coursesOffered; // only for tutors
    private String status; // pending, approved, or rejected
    private long timestamp; // when request was created

    public RegistrationRequest() {} // no arg constructor needed for firebase

    public RegistrationRequest(String userId, String firstName, String lastName, String email, String phoneNumber, String program, String role, List<String> coursesOffered, String status, long timestamp) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.role = role;
        this.coursesOffered = coursesOffered;
        this.status = status;
        this.timestamp = timestamp;
    }

    // getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProgram() { return program; }
    public String getRole() { return role; }
    public List<String> getCoursesOffered() { return coursesOffered; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    // setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProgram(String program) { this.program = program; }
    public void setRole(String role) { this.role = role; }
    public void setCoursesOffered(List<String> coursesOffered) { this.coursesOffered = coursesOffered; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // convert registration request to user object
    public User toUser() {
        User user;
        if (role.equals("Tutor")) {
            user = new Tutor(firstName, lastName, email, phoneNumber, program, coursesOffered);
        } else {
            user = new Student(firstName, lastName, email, phoneNumber, program);
        }
        user.setUserId(userId);
        return user;
    }
}
