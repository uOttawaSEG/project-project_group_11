package com.project.data.model;

public class Student extends User {

    public Student() {
        super(); // no arg constructor needed for firebase
    }

    public Student(String firstName, String lastName, String email, String phoneNumber, String program) {
        super(firstName, lastName, email, phoneNumber, program, "Student");
    }
}
