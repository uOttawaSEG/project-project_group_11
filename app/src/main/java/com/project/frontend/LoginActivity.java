package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.project.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // find ids of buttons and text fields from the xml
        EditText emailField = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordField = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button2);
        Button createAccountButton = findViewById(R.id.button7);

        loginButton.setOnClickListener(view -> { // login button

                // convert editable text objects to strings
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
        });

        createAccountButton.setOnClickListener(view -> { // create account button
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // go to register
        });

    }
}
