package com.project.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.R;
import com.project.data.model.SessionRequest;
import com.project.data.model.Tutor;
import com.project.data.repositories.SessionRequestRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageSessionActivity extends AppCompatActivity {

    private Spinner filterSpinner;
    private LinearLayout sessionContainer;

    private Tutor tutor;

    private SessionRequestRepository sessionRequests = new SessionRequestRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_session);

        // set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.manage_session_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // get tutor data
        Intent intent = getIntent();

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

        // get ui elements
        filterSpinner = findViewById(R.id.manage_session_filterSpinner);
        sessionContainer = findViewById(R.id.manage_session_container);

        List<String> filterOptions = List.of("Upcoming Sessions", "Past Sessions", "Session Requests");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions);
        filterSpinner.setAdapter(filterAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Upcoming Sessions")) {
                    buildUpcomingSessionsView();
                }
                else if (selectedItem.equals("Past Sessions")) {
                    buildPastSessionsView();
                }
                else if (selectedItem.equals("Session Requests")) {
                    buildSessionRequestsView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionContainer.removeAllViews();
            }
        });
    }

    private void buildUpcomingSessionsView() {
        sessionRequests.getUpcomingTutorSessions(tutor.getUserId(), new Date())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);

                    sessionContainer.removeAllViews();

                    if (sessions.isEmpty()) {
                        TextView emptyText = new TextView(ManageSessionActivity.this);
                        emptyText.setText("No upcoming sessions");
                        emptyText.setTextSize(16);
                        emptyText.setPadding(16, 16, 16, 16);
                        sessionContainer.addView(emptyText);
                        return;
                    }

                    for (SessionRequest session : sessions) {
                        View card = LayoutInflater.from(ManageSessionActivity.this).inflate(R.layout.item_session_card, sessionContainer, false);

                        TextView courseName = card.findViewById(R.id.item_session_courseName);
                        TextView startTime = card.findViewById(R.id.item_session_startTime);
                        TextView endTime = card.findViewById(R.id.item_session_endTime);

                        courseName.setText(session.getCourseName());
                        startTime.setText(session.getStartDate().toString());
                        endTime.setText(session.getEndDate().toString());

                        card.setOnClickListener(view -> {
                            Intent sessionIntent = new Intent(ManageSessionActivity.this, SessionActivity.class);
                            sessionIntent.putExtra("studentID", session.getStudentID());
                            startActivity(sessionIntent);
                        });

                        sessionContainer.addView(card);
                    }
                });
    }

    private void buildPastSessionsView() {
        sessionRequests.getPastTutorSessions(tutor.getUserId(), new Date())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);

                    sessionContainer.removeAllViews();

                    if (sessions.isEmpty()) {
                        TextView emptyText = new TextView(ManageSessionActivity.this);
                        emptyText.setText("No past sessions");
                        emptyText.setTextSize(16);
                        emptyText.setPadding(16, 16, 16, 16);
                        sessionContainer.addView(emptyText);
                        return;
                    }

                    for (SessionRequest session : sessions) {
                        View card = LayoutInflater.from(ManageSessionActivity.this).inflate(R.layout.item_session_card, sessionContainer, false);

                        TextView courseName = card.findViewById(R.id.item_session_courseName);
                        TextView startTime = card.findViewById(R.id.item_session_startTime);
                        TextView endTime = card.findViewById(R.id.item_session_endTime);

                        courseName.setText(session.getCourseName());
                        startTime.setText(session.getStartDate().toString());
                        endTime.setText(session.getEndDate().toString());

                        card.setOnClickListener(view -> {
                            Intent sessionIntent = new Intent(ManageSessionActivity.this, SessionActivity.class);
                            sessionIntent.putExtra("studentID", session.getStudentID());
                            startActivity(sessionIntent);
                        });

                        sessionContainer.addView(card);
                    }
                });
    }

    private void buildSessionRequestsView() {
        sessionRequests.getSessionRequestByTutorID(tutor.getUserId())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);

                    sessionContainer.removeAllViews();

                    if (sessions.isEmpty()) {
                        TextView emptyText = new TextView(ManageSessionActivity.this);
                        emptyText.setText("No session requests");
                        emptyText.setTextSize(16);
                        emptyText.setPadding(16, 16, 16, 16);
                        sessionContainer.addView(emptyText);
                        return;
                    }

                    for (SessionRequest session : sessions) {
                        if (session.getStatus().equals("canceled") || session.getEndDate().before(new Date())) {
                            continue;
                        }

                        View card = LayoutInflater.from(ManageSessionActivity.this).inflate(R.layout.item_session_card, sessionContainer, false);

                        TextView courseName = card.findViewById(R.id.item_session_courseName);
                        TextView startTime = card.findViewById(R.id.item_session_startTime);
                        TextView endTime = card.findViewById(R.id.item_session_endTime);
                        LinearLayout buttonContainer = card.findViewById(R.id.item_session_buttonContainer);

                        if (session.getStatus().equals("approved")) {
                            Button cancelButton = new Button(ManageSessionActivity.this);
                            cancelButton.setText("Cancel");
                            cancelButton.setOnClickListener(view -> {
                                session.setStatus("canceled");
                                sessionRequests.updateSessionRequest(session.getSessionID(), session)
                                        .addOnSuccessListener(v -> {
                                            buildSessionRequestsView();
                                        });
                            });

                            buttonContainer.addView(cancelButton);
                        }
                        else if (session.getStatus().equals("pending")) {
                            Button approveButton = new Button(ManageSessionActivity.this);
                            approveButton.setText("Approve");
                            approveButton.setOnClickListener(view -> {
                                session.setStatus("approved");
                                sessionRequests.updateSessionRequest(session.getSessionID(), session)
                                        .addOnSuccessListener(v -> {
                                            buildSessionRequestsView();
                                        });
                            });

                            Button rejectButton = new Button(ManageSessionActivity.this);
                            rejectButton.setText("Reject");
                            rejectButton.setOnClickListener(view -> {
                                session.setStatus("rejected");
                                sessionRequests.updateSessionRequest(session.getSessionID(), session)
                                        .addOnSuccessListener(v -> {
                                            buildSessionRequestsView();
                                        });
                            });

                            buttonContainer.addView(approveButton);
                            buttonContainer.addView(rejectButton);
                        }

                        courseName.setText(session.getCourseName());
                        startTime.setText(session.getStartDate().toString());
                        endTime.setText(session.getEndDate().toString());

                        card.setOnClickListener(view -> {
                            Intent sessionIntent = new Intent(ManageSessionActivity.this, SessionActivity.class);
                            sessionIntent.putExtra("studentID", session.getStudentID());
                            startActivity(sessionIntent);
                        });

                        sessionContainer.addView(card);
                    }
                });
    }
}
