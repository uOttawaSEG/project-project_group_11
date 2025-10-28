package com.project.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.project.R;
import com.project.backend.RegistrationRequest;
import com.project.database.repositories.RegistrationRequestRepository;

public class RejectedRequestsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_requests);

        // set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayout container = findViewById(R.id.rejectedRequestsContainer);
        RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

        // fetch only rejected requests from firestore
        requestRepository.getRequestsByStatus("rejected")
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // no rejected requests found
                        TextView emptyText = new TextView(RejectedRequestsActivity.this);
                        emptyText.setText("No rejected requests");
                        emptyText.setTextSize(16);
                        emptyText.setPadding(16, 16, 16, 16);
                        container.addView(emptyText);
                    } else {
                        // for each rejected request, create a card
                        for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            RegistrationRequest request = doc.toObject(RegistrationRequest.class);

                            // inflate the card layout
                            View card = LayoutInflater.from(RejectedRequestsActivity.this)
                                    .inflate(R.layout.item_rejected_request_card, container, false);

                            // set name and role
                            TextView nameRole = card.findViewById(R.id.nameRole);
                            nameRole.setText(request.getFirstName() + " " + request.getLastName() + " - " + request.getRole());

                            // set details
                            TextView emailText = card.findViewById(R.id.emailText);
                            emailText.setText("Email: " + request.getEmail());

                            TextView phoneText = card.findViewById(R.id.phoneText);
                            phoneText.setText("Phone: " + request.getPhoneNumber());

                            TextView programText = card.findViewById(R.id.programText);
                            programText.setText("Program: " + request.getProgram());

                            TextView coursesText = card.findViewById(R.id.coursesText);
                            if (request.getRole().equals("Tutor") && request.getCoursesOffered() != null) {
                                coursesText.setVisibility(View.VISIBLE);
                                coursesText.setText("Courses Offered: " + String.join(", ", request.getCoursesOffered()));
                            }

                            // toggle details on header click
                            LinearLayout header = card.findViewById(R.id.requestHeader);
                            LinearLayout details = card.findViewById(R.id.detailsPanel);
                            header.setOnClickListener(v -> {
                                if (details.getVisibility() == View.GONE) {
                                    details.setVisibility(View.VISIBLE);
                                } else {
                                    details.setVisibility(View.GONE);
                                }
                            });

                            // approve button
                            Button approveButton = card.findViewById(R.id.approveButton);
                            approveButton.setOnClickListener(v -> {
                                requestRepository.approveAndMoveToUsers(request.getUserId())
                                        .addOnSuccessListener(result -> {
                                            Toast.makeText(RejectedRequestsActivity.this, "Request approved", Toast.LENGTH_SHORT).show();
                                            container.removeView(card);
                                        })
                                        .addOnFailureListener(error -> {
                                            Toast.makeText(RejectedRequestsActivity.this, "Error approving request", Toast.LENGTH_SHORT).show();
                                        });
                            });

                            container.addView(card);
                        }
                    }
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(RejectedRequestsActivity.this, "Error loading rejected requests", Toast.LENGTH_SHORT).show();
                });
    }
}
