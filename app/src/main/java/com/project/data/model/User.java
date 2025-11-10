package com.project.data.model;

import java.io.Serializable;

public class User implements Serializable {
    private String userId; // firebase auth uid
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String program;
    private String role;

    public User() {} // no arg constructor needed for firebase

    public User(String firstName, String lastName, String email, String phoneNumber, String program, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.program = program;
        this.role = role;
    }

    // getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProgram() { return program; }
    public String getRole() { return role; }

    // setters

    public void setUserId(String userId) { this.userId = userId;}
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProgram(String program) { this.program = program; }
    public void setRole(String role) { this.role = role; }
}