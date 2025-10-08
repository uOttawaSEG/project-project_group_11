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

                loginButton.setEnabled(false); // disable to stop lots of clicking

                // convert editable text objects to strings
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); // small popup at bottom of screen
                    loginButton.setEnabled(true);
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim()) // Task operation to sign in user
                        .addOnSuccessListener(result -> { // runs if auth succeeds, then user is signed in

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // retrieves signed-in user's uid

                            UserRepository userRepository = new UserRepository();

                            userRepository.getUserProfile(uid) // async firestore read of /users/uid
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) { // checks if user profile document exists, a document in firestore is like a row in sql i think
                                            User currentUser = snapshot.toObject(User.class);
                                            // add toasts for feedback
                                            startActivity(new Intent(LoginActivity.this, HomepageActivity.class)); // navigate to home creen
                                            finish(); // closes login so cant go back without signing out
                                        }
                                        else { // document not found for uid
                                            Toast.makeText(LoginActivity.this, "Profile not found in database", Toast.LENGTH_SHORT).show();
                                        }
                                        loginButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(error -> { // firestore read failed
                                        Log.w("Firestore", "Failed to fetch user profile", error);
                                        Toast.makeText(LoginActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                                        loginButton.setEnabled(true);
                                    });
                        })
                        .addOnFailureListener(error -> { // auth failed (bad password, no user etc.)
                            Log.w("Auth", "Login failed", error);
                            loginButton.setEnabled(true);
                        });
        });

        createAccountButton.setOnClickListener(view -> { // create account button
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // go to register
        });

    }
}
