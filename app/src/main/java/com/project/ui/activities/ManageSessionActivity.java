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
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.data.model.SessionRequest;
import com.project.data.model.Tutor;
import com.project.ui.viewmodels.ManageSessionViewModel;

import java.util.Date;
import java.util.List;

public class ManageSessionActivity extends AppCompatActivity {

    // ui elements
    private Spinner filterSpinner;
    private LinearLayout sessionContainer;

    // other state
    private Tutor tutor;
    private ManageSessionViewModel viewModel;
    private String filterOption;
    private Date currentDate = new Date();

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

        // setup filter spinner
        List<String> filterOptions = List.of("Upcoming Sessions", "Past Sessions", "Session Requests");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions);
        filterSpinner.setAdapter(filterAdapter);

        // refresh the list of request based on the selection
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterOption = parent.getItemAtPosition(position).toString();

                // refresh date
                currentDate = new Date();

                if (filterOption.equals("Upcoming Sessions")) {
                    viewModel.getUpcomingTutorSessions(tutor.getUserId(), currentDate);
                }
                else if (filterOption.equals("Past Sessions")) {
                    viewModel.getPastTutorSessions(tutor.getUserId(), currentDate);
                }
                else if (filterOption.equals("Session Requests")) {
                    viewModel.getPendingTutorSessions(tutor.getUserId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionContainer.removeAllViews();
            }
        });

        // setup view model
        viewModel = new ViewModelProvider(this).get(ManageSessionViewModel.class);
        viewModel.getSessionRequests().observe(this, this::buildSessionView);
        viewModel.getErrorMessage().observe(this, this::showErrorMessage);
    }

    private void buildSessionView(List<SessionRequest> sessions) {
        sessionContainer.removeAllViews();

        for (SessionRequest session : sessions) {
            View card = LayoutInflater.from(ManageSessionActivity.this).inflate(R.layout.item_session_card, sessionContainer, false);

            // get card ui elements
            TextView courseName = card.findViewById(R.id.item_session_courseName);
            TextView startTime = card.findViewById(R.id.item_session_startTime);
            TextView endTime = card.findViewById(R.id.item_session_endTime);
            LinearLayout buttonContainer = card.findViewById(R.id.item_session_buttonContainer);

            // set text fields
            courseName.setText(session.getCourseName());
            startTime.setText(session.getStartDate().toString());
            endTime.setText(session.getEndDate().toString());

            // add cancel button to approved sessions
            if (session.getStatus().equals("approved") && !session.getEndDate().before(currentDate)) {
                Button cancelButton = new Button(ManageSessionActivity.this);
                cancelButton.setText("Cancel");
                cancelButton.setOnClickListener(view -> {
                    session.setStatus("canceled");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                buttonContainer.addView(cancelButton);
            }
            // add approve and reject buttons to pending session requests
            else if (session.getStatus().equals("pending")) {
                Button approveButton = new Button(ManageSessionActivity.this);
                approveButton.setText("Approve");
                approveButton.setOnClickListener(view -> {
                    session.setStatus("approved");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                Button rejectButton = new Button(ManageSessionActivity.this);
                rejectButton.setText("Reject");
                rejectButton.setOnClickListener(view -> {
                    session.setStatus("rejected");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                buttonContainer.addView(approveButton);
                buttonContainer.addView(rejectButton);
            }

            card.setOnClickListener(view -> {
                Intent sessionIntent = new Intent(ManageSessionActivity.this, SessionActivity.class);
                sessionIntent.putExtra("studentID", session.getStudentID());
                startActivity(sessionIntent);
            });

            sessionContainer.addView(card);
        }
    }

    private void showErrorMessage(String message) {
        sessionContainer.removeAllViews();

        TextView emptyText = new TextView(ManageSessionActivity.this);
        emptyText.setText(message);
        emptyText.setTextSize(16);
        emptyText.setPadding(16, 16, 16, 16);
        sessionContainer.addView(emptyText);
    }
}
