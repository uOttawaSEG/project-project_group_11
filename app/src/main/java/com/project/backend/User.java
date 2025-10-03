package com.project.backend;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private String status; // approved, pending, denied, implement in other deliverable
}

public User(){}

public User(String firstName, String lastName, String email, String phoneNumber, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.phoneNumber = phoneNumber;
    this.role = role;
}

public String getFirstName() { return firstName; }
public String getLastName() { return lastName; }
public String getEmail() { return email; }
public String getPassword() { return password; }
public String getPhoneNumber() { return phoneNumber; }
public String getRole() { return role; }