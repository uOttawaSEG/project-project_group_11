package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;
import com.project.database.repositories.UserRepository;

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
        MaterialButtonToggleGroup roleSelect = findViewById(R.id.roleToggleGroup);

        Button accountCreated = findViewById(R.id.button3);

        accountCreated.setOnClickListener(view -> { // for future this is a lambda expression

            accountCreated.setEnabled(false); // disable button so user can't keep clicking

            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String phoneNumber = phoneNumberField.getText().toString().trim();
            String program = programField.getText().toString().trim();
            String role = ((MaterialButton) findViewById(roleSelect.getCheckedButtonId())).getText().toString(); // get id of selected button (student or tutor), cast to MaterialButton, then get text

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()
                    || phoneNumber.isEmpty() || program.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); // small popup at bottom of screen
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

                        User newUser = new User(firstName, lastName, email, phoneNumber, program, role); // create profile object
                        newUser.setUserId(uid); // stores uid inside model

                        UserRepository userRepository = new UserRepository(); // helper
                        userRepository.createUserProfile(uid, newUser) // create profile with uid and user model
                                .addOnSuccessListener(task -> { // runs when firestore writes the porifle
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // goes to login page
                                    finish(); // close registration
                                    accountCreated.setEnabled(true);
                                })
                                .addOnFailureListener(error -> { // if firestore fails write
                                    Log.w("Firestore", "Error creating profile", error);
                                    Toast.makeText(RegisterActivity.this, "Error creating profile", Toast.LENGTH_SHORT).show();
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
