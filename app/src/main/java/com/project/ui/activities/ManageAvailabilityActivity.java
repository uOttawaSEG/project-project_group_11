package com.project.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.data.model.AvailabilitySlot;
import com.project.data.model.Tutor;
import com.project.ui.viewmodels.AvailabilityViewModel;

import java.util.List;

public class ManageAvailabilityActivity extends AppCompatActivity {

    // ui elements
    private Spinner courseSpinner;
    private EditText dateField;
    private EditText startTimeField;
    private EditText endTimeField;
    private SwitchCompat autoApproveSwitch;
    private Button createSlotButton;
    private LinearLayout slotsContainer;

    // view model
    private AvailabilityViewModel availabilityViewModel;

    // other data
    private String tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availability);

        // set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // retrieve the user object passed to this activity
        Intent intent = getIntent();
        Tutor tutor;

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

        tutorId = tutor.getUserId();

        // find ids of buttons and text fields from the xml
        courseSpinner = findViewById(R.id.courseSpinner);
        dateField = findViewById(R.id.dateField);
        startTimeField = findViewById(R.id.startTimeField);
        endTimeField = findViewById(R.id.endTimeField);
        autoApproveSwitch = findViewById(R.id.autoApproveSwitch);
        createSlotButton = findViewById(R.id.createSlotButton);
        slotsContainer = findViewById(R.id.slotsContainer);

        // setup course spinner with tutor's courses
        if (tutor.getCoursesOffered() == null || tutor.getCoursesOffered().isEmpty()) {
            Toast.makeText(this, "Error: No courses offered", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tutor.getCoursesOffered());
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

        // attach button callbacks
        createSlotButton.setOnClickListener(this::onCreateSlotClicked);

        // setup view model
        availabilityViewModel = new ViewModelProvider(this).get(AvailabilityViewModel.class);
        availabilityViewModel.getAvailabilitySlots().observe(this, this::onSlotsLoaded);
        availabilityViewModel.getErrorMessage().observe(this, this::onError);
        availabilityViewModel.getOperationSuccess().observe(this, this::onOperationSuccess);

        // load existing slots
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
            TextView emptyText = new TextView(ManageAvailabilityActivity.this);
            emptyText.setText("No availability slots created yet");
            emptyText.setTextSize(16);
            emptyText.setPadding(16, 16, 16, 16);
            slotsContainer.addView(emptyText);
        } else {
            for (AvailabilitySlot slot : slots) {
                // inflate the card layout
                View card = LayoutInflater.from(ManageAvailabilityActivity.this)
                        .inflate(R.layout.item_availability_slot, slotsContainer, false);

                // set slot details
                TextView courseText = card.findViewById(R.id.courseText);
                courseText.setText("Course: " + slot.getCourse());

                TextView dateText = card.findViewById(R.id.dateText);
                dateText.setText("Date: " + slot.getDate());

                TextView timeText = card.findViewById(R.id.timeText);
                timeText.setText("Time: " + slot.getStartTime() + " - " + slot.getEndTime());

                TextView autoApproveText = card.findViewById(R.id.autoApproveText);
                autoApproveText.setText("Auto-approve: " + (slot.isAutoApprove() ? "Yes" : "No"));

                // delete button
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
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            createSlotButton.setEnabled(true);
        }
    }

    private void onOperationSuccess(Boolean success) {
        if (success != null && success) {
            Toast.makeText(this, "Operation completed successfully", Toast.LENGTH_SHORT).show();
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
