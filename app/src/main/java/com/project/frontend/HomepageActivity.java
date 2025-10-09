package com.project.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
