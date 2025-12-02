package com.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.data.model.AvailabilitySlot;
import com.project.data.model.Tutor;
import com.project.ui.viewmodels.AvailabilityViewModel;

import java.util.List;

public class AvailabilityFragment extends Fragment {

    private Spinner courseSpinner;
    private EditText dateField;
    private EditText startTimeField;
    private EditText endTimeField;
    private SwitchCompat autoApproveSwitch;
    private Button createSlotButton;
    private LinearLayout slotsContainer;

    private AvailabilityViewModel availabilityViewModel;
    private String tutorId;

    public static AvailabilityFragment newInstance(Tutor tutor) {
        AvailabilityFragment fragment = new AvailabilityFragment();
        Bundle args = new Bundle();
        args.putSerializable("tutor", tutor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_availability, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Tutor tutor = (Tutor) getArguments().getSerializable("tutor");
        if (tutor == null) {
            Toast.makeText(getContext(), "Error: No user information", Toast.LENGTH_SHORT).show();
            return;
        }

        tutorId = tutor.getUserId();

        courseSpinner = view.findViewById(R.id.courseSpinner);
        dateField = view.findViewById(R.id.dateField);
        startTimeField = view.findViewById(R.id.startTimeField);
        endTimeField = view.findViewById(R.id.endTimeField);
        autoApproveSwitch = view.findViewById(R.id.autoApproveSwitch);
        createSlotButton = view.findViewById(R.id.createSlotButton);
        slotsContainer = view.findViewById(R.id.slotsContainer);

        if (tutor.getCoursesOffered() == null || tutor.getCoursesOffered().isEmpty()) {
            Toast.makeText(getContext(), "Error: No courses offered", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tutor.getCoursesOffered());
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

        createSlotButton.setOnClickListener(this::onCreateSlotClicked);

        availabilityViewModel = new ViewModelProvider(this).get(AvailabilityViewModel.class);
        availabilityViewModel.getAvailabilitySlots().observe(getViewLifecycleOwner(), this::onSlotsLoaded);
        availabilityViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::onError);
        availabilityViewModel.getOperationSuccess().observe(getViewLifecycleOwner(), this::onOperationSuccess);

        availabilityViewModel.loadTutorSlots(tutorId);
    }

    private void onCreateSlotClicked(View view) {
        createSlotButton.setEnabled(false);

        String course = courseSpinner.getSelectedItem().toString();
        String date = dateField.getText().toString().trim();
        String startTime = startTimeField.getText().toString().trim();
        String endTime = endTimeField.getText().toString().trim();
        boolean autoApprove = autoApproveSwitch.isChecked();

        if (!validateSlotForm(date, startTime, endTime)) {
            createSlotButton.setEnabled(true);
            return;
        }

        availabilityViewModel.createAvailabilitySlot(tutorId, course, date, startTime, endTime, autoApprove);
    }

    private boolean validateSlotForm(String date, String startTime, String endTime) {
        boolean formValid = true;

        if (date.isEmpty()) {
            dateField.setError("Date is required");
            formValid = false;
        }

        if (startTime.isEmpty()) {
            startTimeField.setError("Start time is required");
            formValid = false;
        }

        if (endTime.isEmpty()) {
            endTimeField.setError("End time is required");
            formValid = false;
        }

        return formValid;
    }

    private void onSlotsLoaded(List<AvailabilitySlot> slots) {
        slotsContainer.removeAllViews();

        if (slots.isEmpty()) {
            TextView emptyText = new TextView(getContext());
            emptyText.setText("No availability slots created yet");
            emptyText.setTextSize(16);
            emptyText.setPadding(16, 16, 16, 16);
            slotsContainer.addView(emptyText);
        } else {
            for (AvailabilitySlot slot : slots) {
                View card = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_availability_slot, slotsContainer, false);

                TextView courseText = card.findViewById(R.id.courseText);
                courseText.setText("Course: " + slot.getCourse());

                TextView dateText = card.findViewById(R.id.dateText);
                dateText.setText("Date: " + slot.getDate());

                TextView timeText = card.findViewById(R.id.timeText);
                timeText.setText("Time: " + slot.getStartTime() + " - " + slot.getEndTime());

                TextView autoApproveText = card.findViewById(R.id.autoApproveText);
                autoApproveText.setText("Auto-approve: " + (slot.isAutoApprove() ? "Yes" : "No"));

                Button deleteButton = card.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(v -> {
                    availabilityViewModel.deleteAvailabilitySlot(slot.getSlotId());
                });

                slotsContainer.addView(card);
            }
        }
    }

    private void onError(String message) {
        if (message != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            createSlotButton.setEnabled(true);
        }
    }

    private void onOperationSuccess(Boolean success) {
        if (success != null && success) {
            Toast.makeText(getContext(), "Operation completed successfully", Toast.LENGTH_SHORT).show();
            clearForm();
            createSlotButton.setEnabled(true);
        }
    }

    private void clearForm() {
        dateField.setText("");
        startTimeField.setText("");
        endTimeField.setText("");
        autoApproveSwitch.setChecked(false);
    }
}
