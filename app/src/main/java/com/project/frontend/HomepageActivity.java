package com.project.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // retrieve the user object passed to this activity from the login activity
        Intent intent = getIntent();
        User user     = (User)intent.getSerializableExtra("userInfo");

        // update homepage text
        TextView welcomeTextView = findViewById(R.id.homepageTextView);
        welcomeTextView.setText("Welcome! You are logged in as " + user.getRole());

        // logout just sends us back to the main activity
        Button logoutButton = findViewById(R.id.homepageLogoutButton);

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            Intent logoutIntent = new Intent(HomepageActivity.this, MainActivity.class);
            startActivity(logoutIntent);

            finish();
        });
    }
}
