package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;
import com.project.backend.RegistrationRequest;
import com.project.database.repositories.UserRepository;
import com.project.database.repositories.RegistrationRequestRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;



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

                loginButton.setEnabled(false); // disable to stop lots of clicking
                createAccountButton.setEnabled(false);

                // convert editable text objects to strings
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                // validate email
                if (email.isEmpty()) {
                    emailField.setError("Email is required");
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailField.setError("Please enter a valid email address");
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    return;
                }

                // validate password
                if (password.isEmpty()) {
                    passwordField.setError("Password is required");
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) // Task operation to sign in user
                        .addOnSuccessListener(result -> { // runs if auth succeeds, then user is signed in

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // retrieves signed-in user's uid

                            RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

                            requestRepository.getRequest(uid) // check registration request status first
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            RegistrationRequest request = snapshot.toObject(RegistrationRequest.class);
                                            String status = request.getStatus();

                                            if (status.equals("approved")) {
                                                // user is approved, proceed to get user profile and go to homepage
                                                UserRepository userRepository = new UserRepository();
                                                userRepository.getUserProfile(uid)
                                                        .addOnSuccessListener(userSnapshot -> {
                                                            if (userSnapshot.exists()) {
                                                                User currentUser = userSnapshot.toObject(User.class);

                                                                Intent homepageIntent = new Intent(LoginActivity.this, HomepageActivity.class);
                                                                homepageIntent.putExtra("userInfo", currentUser);

                                                                startActivity(homepageIntent);

                                                                finish(); // closes login so cant go back without signing out
                                                            } else {
                                                                Toast.makeText(LoginActivity.this, "Profile not found in database", Toast.LENGTH_SHORT).show();
                                                            }
                                                            loginButton.setEnabled(true);
                                                            createAccountButton.setEnabled(true);
                                                        })
                                                        .addOnFailureListener(error -> {
                                                            Log.w("Firestore", "Failed to fetch user profile", error);
                                                            Toast.makeText(LoginActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                                                            loginButton.setEnabled(true);
                                                            createAccountButton.setEnabled(true);
                                                        });
                                            } else if (status.equals("rejected")) {
                                                // registration was rejected
                                                Toast.makeText(LoginActivity.this, "Your registration was rejected. Please contact admin at 613-555-0100", Toast.LENGTH_LONG).show();
                                                loginButton.setEnabled(true);
                                                createAccountButton.setEnabled(true);
                                            } else if (status.equals("pending")) {
                                                // registration is pending approval
                                                Toast.makeText(LoginActivity.this, "Your registration is awaiting administrator approval", Toast.LENGTH_LONG).show();
                                                loginButton.setEnabled(true);
                                                createAccountButton.setEnabled(true);
                                            }
                                        } else {
                                            // request not found
                                            Toast.makeText(LoginActivity.this, "Registration request not found", Toast.LENGTH_SHORT).show();
                                            loginButton.setEnabled(true);
                                            createAccountButton.setEnabled(true);
                                        }
                                    })
                                    .addOnFailureListener(error -> { // firestore read failed
                                        Log.w("Firestore", "Failed to fetch registration request", error);
                                        Toast.makeText(LoginActivity.this, "Error checking registration status", Toast.LENGTH_SHORT).show();
                                        loginButton.setEnabled(true);
                                        createAccountButton.setEnabled(true);
                                    });
                        })
                        .addOnFailureListener(error -> { // auth failed (bad password, no user etc.)
                            Log.w("Auth", "Login failed", error);
                            passwordField.setText(""); // clear password text if failed
                            loginButton.setEnabled(true);
                            createAccountButton.setEnabled(true);
                        });
        });

        createAccountButton.setOnClickListener(view -> { // create account button
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // go to register
        });

    }
}
