package com.project.backend;

public class Tutor extends User {
    private String coursesOffered;

    public Tutor() {
        super(); // no arg constructor needed for firebase
    }

    public Tutor(String firstName, String lastName, String email, String phoneNumber, String program, String coursesOffered) {
        super(firstName, lastName, email, phoneNumber, program, "Tutor");
        this.coursesOffered = coursesOffered;
    }

    // getters
    public String getCoursesOffered() {
        return coursesOffered;
    }

    // setters
    public void setCoursesOffered(String coursesOffered) {
        this.coursesOffered = coursesOffered;
    }
}
