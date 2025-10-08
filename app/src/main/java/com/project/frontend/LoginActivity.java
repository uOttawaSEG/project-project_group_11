package com.project.frontend;
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

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // convert editable text objects to strings
                String emailInput = emailField.getText().toString();
                String passwordInput = passwordField.getText().toString();

            }
        });
    }
}
