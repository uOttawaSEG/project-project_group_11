package com.project.ui.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.project.R;
import com.project.backend.RegisterResult;
import com.project.data.model.RegistrationRequest;
import com.project.ui.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    // ui elements
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText phoneNumberField;
    private EditText programField;
    private EditText coursesOfferedField;
    private MaterialButtonToggleGroup roleSelectButton;
    private Button createAccountButton;

    // view model
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // find ids of buttons and text fields from the xml

        firstNameField = findViewById(R.id.register_firstNameField);
        lastNameField = findViewById(R.id.register_lastNameField);
        emailField = findViewById(R.id.register_emailField);
        passwordField = findViewById(R.id.register_passwordField);
        phoneNumberField = findViewById(R.id.register_phoneNumberField);
        programField = findViewById(R.id.register_programField);
        coursesOfferedField = findViewById(R.id.register_coursesOfferedField);
        roleSelectButton = findViewById(R.id.roleToggleGroup);
        createAccountButton = findViewById(R.id.register_createAccountButton);

        // attach button callbacks
        roleSelectButton.addOnButtonCheckedListener(this::onRoleSelectedChecked);
        createAccountButton.setOnClickListener(this::onCreateAccountClicked);

        // setup view model
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.getRegisterResult().observe(this, this::onRegisterResult);
    }

    private void onRoleSelectedChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (checkedId == R.id.register_tutorButton && isChecked) {
            coursesOfferedField.setAlpha(0f);
            coursesOfferedField.setVisibility(View.VISIBLE);
            coursesOfferedField.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);
        } else if (checkedId == R.id.register_studentButton && isChecked) {
            coursesOfferedField.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        coursesOfferedField.setVisibility(View.INVISIBLE);
                        coursesOfferedField.setText("");
                    });
        }
    }

    private void onCreateAccountClicked(View view) {
        createAccountButton.setEnabled(false);

        if (roleSelectButton.getCheckedButtonId() == View.NO_ID) {
            Toast.makeText(this, "Please select a role (Student or Tutor)", Toast.LENGTH_SHORT).show();
            createAccountButton.setEnabled(true);
            return;
        }

        // convert editable text objects to strings
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();
        String program = programField.getText().toString().trim();
        String coursesOffered = coursesOfferedField.getText().toString().trim();
        String role = ((MaterialButton) findViewById(roleSelectButton.getCheckedButtonId())).getText().toString();

        // validate form fields
        if (!validateRegistrationForm(firstName, lastName, email, password, phoneNumber, program, role, coursesOffered)) {
            createAccountButton.setEnabled(true);
            return;
        }

        // split the courses offered field text
        List<String> courses = null;

        if (role.equals("Tutor")) {
            courses = Arrays.asList(coursesOffered.split(","));
        }

        RegistrationRequest request = new RegistrationRequest(null, firstName, lastName, email, phoneNumber, program, role, courses, "pending", System.currentTimeMillis());

        registerViewModel.createUser(email, password, request);
    }

    private boolean validateRegistrationForm(String firstName, String lastName, String email, String password, String phoneNumber, String program, String role, String coursesOffered) {
        boolean formValid = true;

        if (firstName.isEmpty()) {
            firstNameField.setError("First name is required");
            formValid = false;
        }

        if (lastName.isEmpty()) {
            lastNameField.setError("Last name is required");
            formValid = false;
        }

        if (email.isEmpty()) {
            emailField.setError("Email is required");
            formValid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please enter a valid email address");
            formValid = false;
        }

        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            formValid = false;
        }
        else if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            formValid = false;
        }

        // validate phone number
        if (phoneNumber.isEmpty()) {
            phoneNumberField.setError("Phone number is required");
            formValid = false;
        }
        else if (!phoneNumber.matches("\\d{10}")) {
            phoneNumberField.setError("Please enter a valid 10-digit phone number");
            formValid = false;
        }

        // validate program
        if (program.isEmpty()) {
            programField.setError("Program is required");
            formValid = false;
        }

        // validate courses offered for tutors
        if (role.equals("Tutor") && coursesOffered.isEmpty()) {
            coursesOfferedField.setError("Courses offered is required for tutors, please select one or more");
            formValid = false;
        }

        return formValid;
    }

    private void onRegisterResult(RegisterResult result) {
        if (result.getType() == RegisterResult.SUCCESS) {
            switchToLogin();
        }
        else {
            Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }

        createAccountButton.setEnabled(true);
    }

    private void switchToLogin() {
        Toast.makeText(RegisterActivity.this, "Registration submitted. Awaiting administrator approval.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
