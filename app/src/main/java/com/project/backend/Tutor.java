package com.project.backend;

import java.util.List;

public class Tutor extends User {
    private List<String> coursesOffered;

    public Tutor() {
        super(); // no arg constructor needed for firebase
    }

    public Tutor(String firstName, String lastName, String email, String phoneNumber, String program, List<String> coursesOffered) {
        super(firstName, lastName, email, phoneNumber, program, "Tutor");
        this.coursesOffered = coursesOffered;
    }

    // getters
    public List<String> getCoursesOffered() {
        return coursesOffered;
    }

    // setters
    public void setCoursesOffered(List<String> coursesOffered) {
        this.coursesOffered = coursesOffered;
    }
}
