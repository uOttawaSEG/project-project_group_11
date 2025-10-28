package com.project.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.project.R;
import com.project.backend.User;

import android.widget.LinearLayout;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // retrieve the user object passed to this activity from the login activity
        Intent intent = getIntent();
        User user     = (User)intent.getSerializableExtra("userInfo");

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        ImageButton inboxButton = findViewById(R.id.inboxButton);

        // hide inbox button unless user is admin
        if (!user.getRole().equalsIgnoreCase("Admin")) {
            inboxButton.setVisibility(View.GONE);
        }

        // determine user role to know what to display in settings
        LinearLayout settingsPanel = findViewById(R.id.settingsPanel);
        settingsPanel.removeAllViews(); // clear old views

        settingsButton.setOnClickListener(v -> {
            if (settingsPanel.getVisibility() == View.GONE) {
                settingsPanel.setVisibility(View.VISIBLE);
                settingsPanel.setAlpha(0f);
                settingsPanel.setTranslationY(-40f);
                settingsPanel.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(250)
                        .start();
                settingsButton.setImageResource(R.drawable.baseline_close_24);
            } else {
                settingsPanel.animate()
                        .translationY(-40f)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> settingsPanel.setVisibility(View.GONE))
                        .start();
                settingsButton.setImageResource(R.drawable.baseline_settings_24);
            }
        });



        if (user.getRole().equalsIgnoreCase("Admin")) {
            Button requests = new Button(HomepageActivity.this);
            requests.setText("Rejected Requests");
            requests.setOnClickListener(v -> {
                Intent requestsIntent = new Intent(HomepageActivity.this, RejectedRequestsActivity.class);
                startActivity(requestsIntent);
            });
            settingsPanel.addView(requests);

        }

        Button logout = new Button(HomepageActivity.this);
        logout.setText("Logout");
        settingsPanel.addView(logout);



        // update homepage text
        TextView welcomeTextView = findViewById(R.id.homepageTextView);
        welcomeTextView.setText("Welcome! You are logged in as " + user.getRole());

        // logout just sends us back to the main activity
        //Button logoutButton = findViewById(R.id.homepageLogoutButton);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            Intent logoutIntent = new Intent(HomepageActivity.this, LoginActivity.class);
            startActivity(logoutIntent);

            finish();
        });

        inboxButton.setOnClickListener(v -> {
            Intent inboxIntent = new Intent(HomepageActivity.this, InboxActivity.class);
            startActivity(inboxIntent);
        });
    }
}
