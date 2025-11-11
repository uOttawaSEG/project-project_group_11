package com.project.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.R;
import com.project.data.model.SessionRequest;
import com.project.data.model.Tutor;
import com.project.data.repositories.AvailabilitySlotRepository;
import com.project.data.repositories.SessionRequestRepository;

import java.util.Date;
import java.util.List;

public class UpcomingSessionActivity extends AppCompatActivity {

    private LinearLayout slotsContainer;

    private Tutor tutor;

    private final SessionRequestRepository sessionRequests = new SessionRequestRepository();
    private final AvailabilitySlotRepository availabilitySlots = new AvailabilitySlotRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_sessions);

        // set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.upcoming_sessions_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // get ui elements
        slotsContainer = findViewById(R.id.upcoming_sessions_linearLayout);

        // get tutor object
        Intent intent = getIntent();
        tutor = (Tutor)intent.getSerializableExtra("userInfo");

        try {
            tutor = (Tutor) intent.getSerializableExtra("userInfo");
        } catch (ClassCastException e) {
            Toast.makeText(this, "Error: User is not a tutor", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (tutor == null) {
            Toast.makeText(this, "Error: No user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sessionRequests.getUpcomingTutorSessions(tutor.getUserId(), new Date())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);

                    slotsContainer.removeAllViews();

                    if (sessions.isEmpty()) {
                        TextView emptyText = new TextView(UpcomingSessionActivity.this);
                        emptyText.setText("No upcoming sessions");
                        emptyText.setTextSize(16);
                        emptyText.setPadding(16, 16, 16, 16);
                        slotsContainer.addView(emptyText);
                        return;
                    }

                    for (SessionRequest session : sessions) {
                        View card = LayoutInflater.from(UpcomingSessionActivity.this).inflate(R.layout.item_session_card, slotsContainer, false);

                        TextView courseName = card.findViewById(R.id.item_session_courseName);
                        TextView startTime = card.findViewById(R.id.item_session_startTime);
                        TextView endTime = card.findViewById(R.id.item_session_endTime);

                        courseName.setText(session.getCourseName());
                        startTime.setText(session.getStartDate().toString());
                        endTime.setText(session.getEndDate().toString());

                        card.setOnClickListener(view -> {
                            Intent sessionIntent = new Intent(UpcomingSessionActivity.this, SessionActivity.class);
                            sessionIntent.putExtra("studentID", session.getStudentID());
                            startActivity(sessionIntent);
                        });

                        slotsContainer.addView(card);
                    }
                });
    }

}
