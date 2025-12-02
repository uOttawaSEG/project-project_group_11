package com.project.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
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
import com.project.data.model.Student;
import com.project.ui.activities.StudentSessionDetailActivity;
import com.project.ui.viewmodels.ManageSessionViewModel;

import java.util.Date;
import java.util.List;

public class StudentSessionsFragment extends Fragment {

    private Spinner sessionFilterSpinner;
    private LinearLayout sessionContainer;

    private ManageSessionViewModel viewModel;
    private String studentId;
    private String filterOption;
    private Date currentDate = new Date();

    public static StudentSessionsFragment newInstance(Student student) {
        StudentSessionsFragment fragment = new StudentSessionsFragment();
        Bundle args = new Bundle();
        args.putSerializable("student", student);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Student student = (Student) getArguments().getSerializable("student");
        if (student == null) {
            return;
        }

        studentId = student.getUserId();

        sessionFilterSpinner = view.findViewById(R.id.sessionFilterSpinner);
        sessionContainer = view.findViewById(R.id.sessionContainer);

        List<String> filterOptions = List.of("All Sessions", "Upcoming Sessions", "Past Sessions", "Pending Requests");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, filterOptions);
        sessionFilterSpinner.setAdapter(filterAdapter);

        sessionFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterOption = parent.getItemAtPosition(position).toString();
                currentDate = new Date();

                if (filterOption.equals("All Sessions")) {
                    viewModel.getAllStudentSessions(studentId);
                } else if (filterOption.equals("Upcoming Sessions")) {
                    viewModel.getUpcomingStudentSessions(studentId, currentDate);
                } else if (filterOption.equals("Past Sessions")) {
                    viewModel.getPastStudentSessions(studentId, currentDate);
                } else if (filterOption.equals("Pending Requests")) {
                    viewModel.getPendingStudentSessions(studentId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sessionContainer.removeAllViews();
            }
        });

        viewModel = new ViewModelProvider(this).get(ManageSessionViewModel.class);
        viewModel.getSessionRequests().observe(getViewLifecycleOwner(), this::buildSessionView);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showErrorMessage);
    }

    private void buildSessionView(List<SessionRequest> sessions) {
        sessionContainer.removeAllViews();

        for (SessionRequest session : sessions) {
            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_student_session_card, sessionContainer, false);

            TextView courseName = card.findViewById(R.id.item_session_courseName);
            TextView startTime = card.findViewById(R.id.item_session_startTime);
            TextView endTime = card.findViewById(R.id.item_session_endTime);
            TextView statusBadge = card.findViewById(R.id.item_session_status);
            LinearLayout buttonContainer = card.findViewById(R.id.item_session_buttonContainer);

            courseName.setText(session.getCourseName());
            startTime.setText("Start: " + session.getStartDate().toString());
            endTime.setText("End: " + session.getEndDate().toString());

            String status = session.getStatus();
            statusBadge.setText(status.toUpperCase());
            if (status.equals("approved")) {
                statusBadge.setBackgroundColor(Color.parseColor("#4CAF50"));
            } else if (status.equals("pending")) {
                statusBadge.setBackgroundColor(Color.parseColor("#FFC107"));
            } else if (status.equals("rejected")) {
                statusBadge.setBackgroundColor(Color.parseColor("#F44336"));
            }

            if (status.equals("pending") || status.equals("approved")) {
                Button cancelButton = new Button(getContext());
                cancelButton.setText("Cancel");
                cancelButton.setBackgroundColor(Color.parseColor("#D32F2F"));
                cancelButton.setTextColor(Color.WHITE);

                boolean canCancel = viewModel.canCancelSession(session);
                if (!canCancel) {
                    cancelButton.setEnabled(false);
                    cancelButton.setAlpha(0.5f); // opacity
                }

                cancelButton.setOnClickListener(view -> {
                    session.setStatus("canceled");
                    viewModel.updateSessionRequest(session, filterOption, new Date());
                });

                buttonContainer.addView(cancelButton);
            }

            card.setOnClickListener(view -> {
                Intent sessionIntent = new Intent(getContext(), StudentSessionDetailActivity.class);
                sessionIntent.putExtra("session", session);
                startActivity(sessionIntent);
            });

            sessionContainer.addView(card);
        }
    }

    private void showErrorMessage(String message) {
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
            if (filterOption.equals("All Sessions")) {
                viewModel.getAllStudentSessions(studentId);
            } else if (filterOption.equals("Upcoming Sessions")) {
                viewModel.getUpcomingStudentSessions(studentId, currentDate);
            } else if (filterOption.equals("Past Sessions")) {
                viewModel.getPastStudentSessions(studentId, currentDate);
            } else if (filterOption.equals("Pending Requests")) {
                viewModel.getPendingStudentSessions(studentId);
            }
        }
    }
}
