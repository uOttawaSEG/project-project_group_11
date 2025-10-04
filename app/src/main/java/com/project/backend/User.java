package com.project.backend;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String role;
    private String status; // approved, pending, denied, implement in other deliverable

    // No-argument constructor required for Firestore serialization
    public User() {}

    public User(String firstName, String lastName, String email, String phoneNumber, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = "pending";
    }

    // Getters - required for Firestore
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    // Setters - required for Firestore
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}