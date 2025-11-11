package com.project.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.R;
import com.project.data.model.Tutor;
import com.project.data.model.User;
import com.project.data.repositories.UserRepository;

public class SessionActivity extends AppCompatActivity {

    private TextView studentFirstName;
    private TextView studentLastName;
    private TextView studentEmail;
    private TextView studentPhoneNumber;
    private TextView studentProgram;

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_activity);

        // set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.session_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // get ui elements
        studentFirstName = findViewById(R.id.session_info_studentFirstName);
        studentLastName = findViewById(R.id.session_info_studentLastName);
        studentEmail = findViewById(R.id.session_info_studentEmail);
        studentPhoneNumber = findViewById(R.id.session_info_studentPhoneNumber);
        studentProgram = findViewById(R.id.session_info_studentProgram);

        // get tutor object
        Intent intent = getIntent();
        String studentID = (String)intent.getSerializableExtra("studentID");

        userRepo.getUserProfile(studentID)
                .addOnSuccessListener(document -> {
                    User student = document.toObject(User.class);

                    studentFirstName.setText("First Name: " + student.getFirstName());
                    studentLastName.setText("Last Name: " + student.getLastName());
                    studentEmail.setText("Email: " + student.getEmail());
                    studentPhoneNumber.setText("PhoneNumber: " + student.getPhoneNumber());
                    studentProgram.setText("Program: " + student.getProgram());
                });
    }
}
