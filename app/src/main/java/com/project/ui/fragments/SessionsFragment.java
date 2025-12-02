package com.project.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.data.model.SessionRequest;
import com.project.data.model.Tutor;
import com.project.ui.activities.SessionActivity;
import com.project.ui.viewmodels.ManageSessionViewModel;

import java.util.Date;
import java.util.List;

public class SessionsFragment extends Fragment {

    private Spinner sessionFilterSpinner;
    private LinearLayout sessionContainer;

    private ManageSessionViewModel viewModel;
    private String tutorId;
    private String filterOption;
    private Date currentDate = new Date();

    public static SessionsFragment newInstance(Tutor tutor) {
        SessionsFragment fragment = new SessionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("tutor", tutor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Tutor tutor = (Tutor) getArguments().getSerializable("tutor");
        if (tutor == null) {
            return;
        }

        tutorId = tutor.getUserId();

        sessionFilterSpinner = view.findViewById(R.id.sessionFilterSpinner);
        sessionContainer = view.findViewById(R.id.sessionContainer);

        viewModel = new ViewModelProvider(this).get(ManageSessionViewModel.class);
        viewModel.getSessionRequests().observe(getViewLifecycleOwner(), this::buildSessionView);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showErrorMessage);

        List<String> filterOptions = List.of("Upcoming Sessions", "Past Sessions", "Session Requests");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, filterOptions);
        sessionFilterSpinner.setAdapter(filterAdapter);

        sessionFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterOption = parent.getItemAtPosition(position).toString();
                currentDate = new Date();

                if (filterOption.equals("Upcoming Sessions")) {
                    viewModel.getUpcomingTutorSessions(tutorId, currentDate);
                } else if (filterOption.equals("Past Sessions")) {
                    viewModel.getPastTutorSessions(tutorId, currentDate);
                } else if (filterOption.equals("Session Requests")) {
                    viewModel.getPendingTutorSessions(tutorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionContainer.removeAllViews();
            }
        });
    }

    private void buildSessionView(List<SessionRequest> sessions) {
        sessionContainer.removeAllViews();

        for (SessionRequest session : sessions) {
            String status = session.getStatus();
            if (status.equals("canceled") || status.equals("rejected")) {
                continue;
            }

            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_session_card, sessionContainer, false);

            TextView courseName = card.findViewById(R.id.item_session_courseName);
            TextView startTime = card.findViewById(R.id.item_session_startTime);
            TextView endTime = card.findViewById(R.id.item_session_endTime);
            LinearLayout buttonContainer = card.findViewById(R.id.item_session_buttonContainer);

            courseName.setText(session.getCourseName());
            startTime.setText(session.getStartDate().toString());
            endTime.setText(session.getEndDate().toString());

            if (session.getStatus().equals("approved") && !session.getEndDate().before(currentDate)) {
                Button cancelButton = new Button(getContext());
                cancelButton.setText("Cancel");
                cancelButton.setOnClickListener(view -> {
                    session.setStatus("canceled");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                buttonContainer.addView(cancelButton);
            } else if (session.getStatus().equals("pending")) {
                Button approveButton = new Button(getContext());
                approveButton.setText("Approve");
                approveButton.setOnClickListener(view -> {
                    session.setStatus("approved");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                Button rejectButton = new Button(getContext());
                rejectButton.setText("Reject");
                rejectButton.setOnClickListener(view -> {
                    session.setStatus("rejected");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                buttonContainer.addView(approveButton);
                buttonContainer.addView(rejectButton);
            }

            card.setOnClickListener(view -> {
                Intent sessionIntent = new Intent(getContext(), SessionActivity.class);
                sessionIntent.putExtra("studentID", session.getStudentID());
                startActivity(sessionIntent);
            });

            sessionContainer.addView(card);
        }
    }

    private void showErrorMessage(String message) {
        if (message == null) {
            return;
        }

        sessionContainer.removeAllViews();

        TextView emptyText = new TextView(getContext());
        emptyText.setText(message);
        emptyText.setTextSize(16);
        emptyText.setPadding(16, 16, 16, 16);
        sessionContainer.addView(emptyText);
    }

    public void refreshSessions() {
        currentDate = new Date();
        if (filterOption != null) {
            if (filterOption.equals("Upcoming Sessions")) {
                viewModel.getUpcomingTutorSessions(tutorId, currentDate);
            } else if (filterOption.equals("Past Sessions")) {
                viewModel.getPastTutorSessions(tutorId, currentDate);
            } else if (filterOption.equals("Session Requests")) {
                viewModel.getPendingTutorSessions(tutorId);
            }
        }
    }
}
