package com.project.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.R;
import com.project.data.model.Rating;
import com.project.data.model.SessionRequest;
import com.project.data.model.User;
import com.project.data.repositories.RatingRepository;
import com.project.data.repositories.SessionRequestRepository;
import com.project.data.repositories.UserRepository;

import java.util.Date;

public class StudentSessionDetailActivity extends AppCompatActivity {

    private SessionRequest session;
    private RatingRepository ratingRepo = new RatingRepository();
    private SessionRequestRepository sessionRepo = new SessionRequestRepository();
    private UserRepository userRepo = new UserRepository();

    private TextView detailCourseName;
    private TextView detailTutorName;
    private TextView detailStartDate;
    private TextView detailEndDate;
    private TextView detailStatus;

    private LinearLayout ratingContainer;
    private LinearLayout alreadyRatedContainer;
    private RatingBar ratingBar;
    private RatingBar existingRatingBar;
    private Button submitRatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_session_detail);

        Toolbar toolbar = findViewById(R.id.session_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        String sessionID = getIntent().getStringExtra("sessionID");
        if (sessionID == null) {
            finish();
            return;
        }

        detailCourseName = findViewById(R.id.detailCourseName);
        detailTutorName = findViewById(R.id.detailTutorName);
        detailStartDate = findViewById(R.id.detailStartDate);
        detailEndDate = findViewById(R.id.detailEndDate);
        detailStatus = findViewById(R.id.detailStatus);

        ratingContainer = findViewById(R.id.ratingContainer);
        alreadyRatedContainer = findViewById(R.id.alreadyRatedContainer);
        ratingBar = findViewById(R.id.ratingBar);
        existingRatingBar = findViewById(R.id.existingRatingBar);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        fetchSessionDetails(sessionID);

        submitRatingButton.setOnClickListener(v -> submitRating());
    }

    private void fetchSessionDetails(String sessionID) {
        sessionRepo.getSessionRequestById(sessionID)
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        session = doc.toObject(SessionRequest.class);
                        if (session != null) {
                            displaySessionDetails();
                            fetchTutorInfo();
                            checkRatingEligibility();
                        } else {
                            Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Failed to load session", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displaySessionDetails() {
        detailCourseName.setText(session.getCourseName());
        detailStartDate.setText("Start: " + session.getStartDate().toDate().toString());
        detailEndDate.setText("End: " + session.getEndDate().toDate().toString());

        String status = session.getStatus();
        detailStatus.setText(status.toUpperCase());
        if (status.equals("approved")) {
            detailStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (status.equals("pending")) {
            detailStatus.setBackgroundColor(Color.parseColor("#FFC107"));
        } else if (status.equals("rejected")) {
            detailStatus.setBackgroundColor(Color.parseColor("#F44336"));
        } else if (status.equals("canceled")) {
            detailStatus.setBackgroundColor(Color.parseColor("#9E9E9E"));
        }
    }

    private void fetchTutorInfo() {
        userRepo.getUserProfile(session.getTutorID())
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User tutor = doc.toObject(User.class);
                        if (tutor != null) {
                            detailTutorName.setText("Tutor: " + tutor.getFirstName() + " " + tutor.getLastName());
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    detailTutorName.setText("Tutor: Unknown");
                });
    }

    private void checkRatingEligibility() {
        Date currentDate = new Date();
        boolean isPastSession = session.getEndDate() != null && session.getEndDate().toDate().before(currentDate);
        boolean isApproved = session.getStatus().equals("approved");

        if (isPastSession && isApproved) {
            ratingRepo.getRatingBySessionId(session.getSessionID())
                    .addOnSuccessListener(query -> {
                        if (query.isEmpty()) {
                            ratingContainer.setVisibility(View.VISIBLE);
                        } else {
                            Rating existingRating = query.toObjects(Rating.class).get(0);
                            existingRatingBar.setRating(existingRating.getRating());
                            alreadyRatedContainer.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void submitRating() {
        float ratingValue = ratingBar.getRating();

        if (ratingValue == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        String ratingId = session.getSessionID() + "_rating";
        Rating rating = new Rating(
                ratingId,
                session.getSessionID(),
                session.getTutorID(),
                session.getStudentID(),
                (int) ratingValue,
                System.currentTimeMillis()
        );

        ratingRepo.saveRating(ratingId, rating)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                    ratingContainer.setVisibility(View.GONE);
                    existingRatingBar.setRating(ratingValue);
                    alreadyRatedContainer.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                });
    }
}
