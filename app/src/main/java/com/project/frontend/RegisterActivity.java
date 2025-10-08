package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

            String firstName = firstNameField.getText().toString();
            String lastName = lastNameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String phoneNumber = phoneNumberField.getText().toString();
            String program = programField.getText().toString();
            String role = ((MaterialButton) findViewById(roleSelect.getCheckedButtonId())).getText().toString(); // get id of selected button (student or tutor), cast to MaterialButton, then get text

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password) // Task operation to create a uid
                    .addOnSuccessListener(result -> {
                        String uid = result.getUser().getUid();

                        User newUser = new User(firstName, lastName, email, phoneNumber, program, role);
                        newUser.setUserId(uid);

                        UserRepository userRepository = new UserRepository();
                        userRepository.createUserProfile(uid, newUser)
                                .addOnSuccessListener(task -> {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(error -> {

                        });

                        accountCreated.setEnabled(true);
                    })
                    .addOnFailureListener(error -> {
                        Log.w("error", error);
                        accountCreated.setEnabled(true);
                    });
        });
    }
}
