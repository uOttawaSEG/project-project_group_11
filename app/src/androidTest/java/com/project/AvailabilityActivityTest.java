package com.project;

import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import androidx.annotation.UiThread;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.project.ui.activities.ManageAvailabilityActivity;
import com.project.ui.viewmodels.AvailabilityViewModel;

import org.junit.Rule;
import org.junit.Test;

public class AvailabilityActivityTest {
    @Rule
    public ActivityScenarioRule<ManageAvailabilityActivity> testRule = new ActivityScenarioRule<>(ManageAvailabilityActivity.class);

    @Test
    @UiThread
    public void slotFormIsValid() {
        testRule.getScenario().onActivity((activity) -> {
            EditText dateField = activity.findViewById(R.id.dateField);
            EditText startTimeField = activity.findViewById(R.id.startTimeField);
            EditText endTimeField = activity.findViewById(R.id.endTimeField);

            dateField.setText("2025-11-15");
            startTimeField.setText("14:00");
            endTimeField.setText("14:30");

            boolean result = activity.validateSlotForm(dateField.getText().toString(), startTimeField.getText().toString(), endTimeField.getText().toString());

            assertTrue(result);
        });
    }

    @Test
    @UiThread
    public void validateDate() {
        testRule.getScenario().onActivity((activity) -> {
            AvailabilityViewModel viewModel = new ViewModelProvider(activity).get(AvailabilityViewModel.class);

            String date = "2025-11-15";
            boolean result = viewModel.validateDate(date);

            assertTrue(result);
        });
    }

    @Test
    @UiThread
    public void validateTimeFormat() {
        testRule.getScenario().onActivity((activity) -> {
            AvailabilityViewModel viewModel = new ViewModelProvider(activity).get(AvailabilityViewModel.class);

            String time = "14:00";
            boolean result = viewModel.validateTimeFormat(time);

            assertTrue(result);
        });
    }
}
