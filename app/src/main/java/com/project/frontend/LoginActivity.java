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
import com.project.database.repositories.UserRepository;
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

                loginButton.setEnabled(false);

                // convert editable text objects to strings
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); // small popup at bottom of screen
                    loginButton.setEnabled(true);
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) // Task operation to sign in user
                        .addOnSuccessListener(result -> {

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            UserRepository userRepository = new UserRepository();

                            userRepository.getUserProfile(uid)
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            User currentUser = snapshot.toObject(User.class);
                                            // add toasts for feedback
                                            startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(LoginActivity.this, "Profile not found in database", Toast.LENGTH_SHORT).show();
                                        }
                                        loginButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(error -> {
                                        Log.w("Firestore", "Failed to fetch user profile", error);
                                        Toast.makeText(LoginActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                                        loginButton.setEnabled(true);
                                    });
                        })
                        .addOnFailureListener(error -> {
                            Log.w("Auth", "Login failed", error);
                            loginButton.setEnabled(true);
                        });
        });

        createAccountButton.setOnClickListener(view -> { // create account button
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // go to register
        });

    }
}
