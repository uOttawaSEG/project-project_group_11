package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;
import com.project.backend.RegistrationRequest;
import com.project.database.repositories.UserRepository;
import com.project.database.repositories.RegistrationRequestRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // ui elements
    private EditText emailField;
    private EditText passwordField;

    private Button loginButton;
    private Button createAccountButton;

    // database data
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find ids of buttons and text fields from the xml
        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);

        loginButton = findViewById(R.id.login_loginButton);
        createAccountButton = findViewById(R.id.login_createAccountButton);

        loginButton.setOnClickListener(view -> {
            enableButtons(false);

            // convert editable text objects to strings
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (!validateLoginForm(email, password)) {
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this::signInSuccess)
                    .addOnFailureListener(this::signInFailure);
        });

        createAccountButton.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void enableButtons(boolean value) {
        loginButton.setEnabled(value);
        createAccountButton.setEnabled(value);
    }

    private boolean validateLoginForm(String email, String password) {
        if (email.isEmpty()) {
            emailField.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private void signInSuccess(AuthResult result) {
        userID = result.getUser().getUid();

        requestRepository.getRequest(userID)
                .addOnSuccessListener(this::fetchRegistrationSuccess)
                .addOnFailureListener(this::fetchRegistrationFailure);
    }

    private void signInFailure(Exception error) {
        Log.w("Auth", "Login failed", error);
        passwordField.setText(""); // clear password text if failed
        enableButtons(true);
    }

    private void fetchRegistrationSuccess(DocumentSnapshot document) {
        // no user registration found, try to sign in
        if (!document.exists()) {
            userRepository.getUserProfile(userID)
                    .addOnSuccessListener(this::fetchUserSuccess)
                    .addOnFailureListener(this::fetchUserFailure);

            return;
        }

        RegistrationRequest request = document.toObject(RegistrationRequest.class);

        if (request.getStatus().equals("approved")) {
            userRepository.getUserProfile(userID)
                    .addOnSuccessListener(this::fetchUserSuccess)
                    .addOnFailureListener(this::fetchUserFailure);
        }
        else if (request.getStatus().equals("rejected")) {
            Toast.makeText(LoginActivity.this, "Your registration was rejected. Please contact admin at 613-111-1111", Toast.LENGTH_LONG).show();
            enableButtons(true);
        }
        else if (request.getStatus().equals("pending")) {
            Toast.makeText(LoginActivity.this, "Your registration is awaiting administrator approval", Toast.LENGTH_LONG).show();
            enableButtons(true);
        }
    }

    private void fetchRegistrationFailure(Exception error) {
        Log.w("Firestore", "Failed to fetch registration request", error);
        Toast.makeText(LoginActivity.this, "Error checking registration status", Toast.LENGTH_SHORT).show();
        enableButtons(true);
    }

    private void fetchUserSuccess(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(LoginActivity.this, "Profile not found in database", Toast.LENGTH_SHORT).show();
            enableButtons(true);
            return;
        }

        User user = document.toObject(User.class);
        switchToHomepage(user);
    }

    private void fetchUserFailure(Exception error) {
        Log.w("Firestore", "Failed to fetch user profile", error);
        Toast.makeText(LoginActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
        enableButtons(false);
    }

    private void switchToHomepage(User user) {
        Intent homepageIntent = new Intent(LoginActivity.this, HomepageActivity.class);
        homepageIntent.putExtra("userInfo", user);

        startActivity(homepageIntent);

        finish(); // closes login so cant go back without signing out
    }
}
