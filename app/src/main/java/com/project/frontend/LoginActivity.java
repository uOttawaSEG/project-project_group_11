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
                createAccountButton.setEnabled(false);

                // convert editable text objects to strings
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show(); // small popup at bottom of screen
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    return;
                }

                // the reason that we need success and failure things is cause the methods that use them call Tasks, these tasks return a background task, and don't start immediately
                // so once the task finishes, either the success or failure code runs

                // exterior success/fail: for auth (email,password)
                // interior success/fail: for firestore (other user data stored in database)

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password) // Task operation to sign in user
                        .addOnSuccessListener(result -> { // runs if auth succeeds, then user is signed in

                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // retrieves signed-in user's uid

                            UserRepository userRepository = new UserRepository();

                            userRepository.getUserProfile(uid) // async firestore read of /users/uid
                                    .addOnSuccessListener(snapshot -> { // runs if firestore stuff succeeds
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
                                        createAccountButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(error -> { // firestore read failed
                                        Log.w("Firestore", "Failed to fetch user profile", error);
                                        Toast.makeText(LoginActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
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
