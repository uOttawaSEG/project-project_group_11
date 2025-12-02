package com.project.ui.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.backend.LoginResult;
import com.project.ui.viewmodels.LoginViewModel;
import com.project.data.model.User;

import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // ui elements
    private EditText emailField;
    private EditText passwordField;

    private Button loginButton;
    private Button createAccountButton;

    // view model
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find ids of buttons and text fields from the xml
        emailField = findViewById(R.id.login_emailField);
        passwordField = findViewById(R.id.login_passwordField);

        loginButton = findViewById(R.id.login_loginButton);
        createAccountButton = findViewById(R.id.login_createAccountButton);

        // attach button callbacks
        loginButton.setOnClickListener(this::onLoginInClicked);
        createAccountButton.setOnClickListener(this::onCreateAccountClicked);

        // setup view model
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getLoginResult().observe(this, this::onLoginResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void enableButtons(boolean value) {
        loginButton.setEnabled(value);
        createAccountButton.setEnabled(value);
    }

    public boolean validateLoginForm(String email, String password) {
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please enter a valid email address");
            return false;
        }

        // validate password
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            return false;
        }

        return true;
    }

    private void switchToHomepage(User user) {
        Intent homepageIntent = new Intent(LoginActivity.this, HomepageActivity.class);
        homepageIntent.putExtra("userInfo", user);

        startActivity(homepageIntent);

        finish(); // closes login so cant go back without signing out
    }

    private void onLoginInClicked(View view) {
        enableButtons(false);

        // convert editable text objects to strings
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (!validateLoginForm(email, password)) {
            enableButtons(true);
            return;
        }

        loginViewModel.signInUser(email, password);
    }

    private void onCreateAccountClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void onLoginResult(LoginResult result) {
        if (result.getType() == LoginResult.LOGIN_SUCCESS) {
            switchToHomepage(result.getUser());
        }
        else {
            Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
            enableButtons(true);
        }
    }
}
