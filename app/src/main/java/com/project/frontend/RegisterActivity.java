package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;
import com.project.backend.Student;
import com.project.backend.Tutor;
import com.project.backend.RegistrationRequest;
import com.project.database.repositories.UserRepository;
import com.project.database.repositories.RegistrationRequestRepository;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // find ids of buttons and text fields from the xml

        EditText firstNameField = findViewById(R.id.editTextText);
        EditText lastNameField = findViewById(R.id.editTextText3);
        EditText emailField = findViewById(R.id.editTextTextEmailAddress2);
        EditText passwordField = findViewById(R.id.editTextTextPassword2);
        EditText phoneNumberField = findViewById(R.id.editTextText6);
        EditText programField = findViewById(R.id.editTextText7);
        EditText coursesOfferedField = findViewById(R.id.editTextCoursesOffered);
        MaterialButtonToggleGroup roleSelect = findViewById(R.id.roleToggleGroup);

        Button accountCreated = findViewById(R.id.button3);

        // show/hide courses offered field based on role selection
        roleSelect.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.buttonTutor && isChecked) {
                coursesOfferedField.setAlpha(0f);
                coursesOfferedField.setVisibility(View.VISIBLE);
                coursesOfferedField.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null);
            } else if (checkedId == R.id.buttonStudent && isChecked) {
                coursesOfferedField.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            coursesOfferedField.setVisibility(View.INVISIBLE);
                            coursesOfferedField.setText("");
                        });
            }
        });

        accountCreated.setOnClickListener(view -> { // for future this is a lambda expression

            accountCreated.setEnabled(false); // disable button so user can't keep clicking

            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String phoneNumber = phoneNumberField.getText().toString().trim();
            String program = programField.getText().toString().trim();
            String coursesOffered = coursesOfferedField.getText().toString().trim();

            String role = ((MaterialButton) findViewById(roleSelect.getCheckedButtonId())).getText().toString(); // get id of selected button (student or tutor), cast to MaterialButton, then get text

            // quick initial check
            if (firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && password.isEmpty()
                && phoneNumber.isEmpty() && program.isEmpty() && roleSelect.getCheckedButtonId() == View.NO_ID) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                accountCreated.setEnabled(true);
                return;
            }
            // validate first name
            if (firstName.isEmpty()) {
                firstNameField.setError("First name is required");
                accountCreated.setEnabled(true); // this is the create account button btw
                return;
            }

            // validate last name
            if (lastName.isEmpty()) {
                lastNameField.setError("Last name is required");
                accountCreated.setEnabled(true);
                return;
            }

            // validate email
            if (email.isEmpty()) {
                emailField.setError("Email is required");
                accountCreated.setEnabled(true);
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Please enter a valid email address");
                accountCreated.setEnabled(true);
                return;
            }

            // validate password
            if (password.isEmpty()) {
                passwordField.setError("Password is required");
                accountCreated.setEnabled(true);
                return;
            }
            if (password.length() < 6) {
                passwordField.setError("Password must be at least 6 characters");
                accountCreated.setEnabled(true);
                return;
            }

            // validate phone number
            if (phoneNumber.isEmpty()) {
                phoneNumberField.setError("Phone number is required");
                accountCreated.setEnabled(true);
                return;
            }
            if (!phoneNumber.matches("\\d{10}")) {
                phoneNumberField.setError("Please enter a valid 10-digit phone number");
                accountCreated.setEnabled(true);
                return;
            }

            // validate program
            if (program.isEmpty()) {
                programField.setError("Program is required");
                accountCreated.setEnabled(true);
                return;
            }

            // validate role selection
            if (roleSelect.getCheckedButtonId() == View.NO_ID) {
                Toast.makeText(this, "Please select a role (Student or Tutor)", Toast.LENGTH_SHORT).show();
                accountCreated.setEnabled(true);
                return;
            }

            // validate courses offered for tutors
            if (role.equals("Tutor") && coursesOffered.isEmpty()) {
                coursesOfferedField.setError("Courses offered is required for tutors, please select one or more");
                accountCreated.setEnabled(true);
                return;
            }

            // the reason that we need success and failure things is cause the methods that use them call Tasks, these tasks return a background task, and don't start immediately
            // so once the task finishes, either the success or failure code runs

            // exterior success/fail: for auth being created (email,password)
            // interior success/fail: for firestore profile being saved (other user data stored in database)

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password) // Task operation to create an auth account for email/password (uid)
                    .addOnSuccessListener(result -> { // runs if auth successfully created account
                        String uid = result.getUser().getUid(); // gets new users uid, it will be used as firestore document id

                        // create registration request object
                        List<String> courses = null;
                        if (role.equals("Tutor")) {
                            courses = Arrays.asList(coursesOffered.split(","));
                        }

                        RegistrationRequest request = new RegistrationRequest(
                                uid, firstName, lastName, email, phoneNumber, program, role, courses, "pending", System.currentTimeMillis()
                        );

                        RegistrationRequestRepository requestRepository = new RegistrationRequestRepository(); // helper
                        requestRepository.createRequest(request) // create request with uid and request model
                                .addOnSuccessListener(task -> { // runs when firestore writes the request
                                    Toast.makeText(RegisterActivity.this, "Registration submitted. Awaiting administrator approval.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // goes to login page
                                    finish(); // close registration
                                    accountCreated.setEnabled(true);
                                })
                                .addOnFailureListener(error -> { // if firestore fails write
                                    Log.w("Firestore", "Error creating registration request", error);
                                    Toast.makeText(RegisterActivity.this, "Error submitting registration", Toast.LENGTH_SHORT).show();
                                    accountCreated.setEnabled(true);
                        });
                    })
                    .addOnFailureListener(error -> {
                        Log.w("Auth", "User creation failed", error); // if auth creation fails (later implement weak password stuff or dupe emails)
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        passwordField.setText(""); // clear password text if failed
                        accountCreated.setEnabled(true);
                    });
        });
    }
}
