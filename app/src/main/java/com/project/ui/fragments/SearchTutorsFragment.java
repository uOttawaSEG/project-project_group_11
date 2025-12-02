package com.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.R;
import com.project.data.model.AvailabilitySlot;
import com.project.data.model.Student;
import com.project.data.model.Tutor;
import com.project.ui.viewmodels.SearchTutorsViewModel;

import java.util.List;

public class SearchTutorsFragment extends Fragment {

    private SearchTutorsViewModel viewModel;
    private String studentId;

    private EditText courseCodeField;
    private Button searchButton;
    private LinearLayout searchResultsContainer;

    public static SearchTutorsFragment newInstance(Student student) {
        SearchTutorsFragment fragment = new SearchTutorsFragment();
        Bundle args = new Bundle();
        args.putSerializable("student", student);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_tutors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Student student = (Student) getArguments().getSerializable("student");
        if (student == null) {
            return;
        }

        studentId = student.getUserId();

        courseCodeField = view.findViewById(R.id.courseCodeField);
        searchButton = view.findViewById(R.id.searchButton);
        searchResultsContainer = view.findViewById(R.id.searchResultsContainer);

        viewModel = new ViewModelProvider(this).get(SearchTutorsViewModel.class);
        viewModel.getTutors().observe(getViewLifecycleOwner(), this::displayTutors);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
        viewModel.getBookingSuccess().observe(getViewLifecycleOwner(), this::onBookingSuccess);

        searchButton.setOnClickListener(v -> {
            String courseName = courseCodeField.getText().toString().trim();
            viewModel.searchTutorsByCourse(courseName);
        });
    }

    private void displayTutors(List<Tutor> tutors) {
        searchResultsContainer.removeAllViews();

        for (Tutor tutor : tutors) {
            View tutorCard = LayoutInflater.from(getContext()).inflate(R.layout.item_tutor_card, searchResultsContainer, false);

            TextView tutorName = tutorCard.findViewById(R.id.tutorName);
            TextView tutorEmail = tutorCard.findViewById(R.id.tutorEmail);
            TextView tutorProgram = tutorCard.findViewById(R.id.tutorProgram);
            RatingBar tutorRatingBar = tutorCard.findViewById(R.id.tutorRatingBar);
            TextView tutorRatingText = tutorCard.findViewById(R.id.tutorRatingText);
            Button viewAvailabilityButton = tutorCard.findViewById(R.id.viewAvailabilityButton);
            LinearLayout availabilityContainer = tutorCard.findViewById(R.id.availabilityContainer);

            tutorName.setText(tutor.getFirstName() + " " + tutor.getLastName());
            tutorEmail.setText(tutor.getEmail());
            tutorProgram.setText(tutor.getProgram());

            viewModel.loadTutorRating(tutor.getUserId(), (averageRating, count) -> {
                if (count == 0) {
                    tutorRatingBar.setRating(0);
                    tutorRatingText.setText("No ratings");
                } else {
                    tutorRatingBar.setRating((float) averageRating);
                    tutorRatingText.setText(String.format("%.1f (%d)", averageRating, count));
                }
            });

            viewAvailabilityButton.setOnClickListener(v -> {
                if (availabilityContainer.getVisibility() == View.GONE) {
                    viewAvailabilityButton.setText("Hide Availability");
                    availabilityContainer.setVisibility(View.VISIBLE);
                    loadTutorAvailability(tutor.getUserId(), availabilityContainer);
                } else {
                    viewAvailabilityButton.setText("View Availability");
                    availabilityContainer.setVisibility(View.GONE);
                }
            });

            searchResultsContainer.addView(tutorCard);
        }
    }

    private void loadTutorAvailability(String tutorId, LinearLayout container) {
        container.removeAllViews();

        TextView loadingText = new TextView(getContext());
        loadingText.setText("Loading availability...");
        loadingText.setPadding(8, 8, 8, 8);
        container.addView(loadingText);

        viewModel.getAvailabilitySlots().observe(getViewLifecycleOwner(), slots -> {
            container.removeAllViews();
            displayAvailabilitySlots(slots, container, tutorId);
        });

        viewModel.loadTutorAvailability(tutorId);
    }

    private void displayAvailabilitySlots(List<AvailabilitySlot> slots, LinearLayout container, String tutorId) {
        if (slots.isEmpty()) {
            TextView emptyText = new TextView(getContext());
            emptyText.setText("No available slots");
            emptyText.setPadding(8, 8, 8, 8);
            container.addView(emptyText);
            return;
        }

        for (AvailabilitySlot slot : slots) {
            View slotCard = LayoutInflater.from(getContext()).inflate(R.layout.item_availability_slot_bookable, container, false);

            TextView slotDate = slotCard.findViewById(R.id.slotDate);
            TextView slotTime = slotCard.findViewById(R.id.slotTime);
            Button bookButton = slotCard.findViewById(R.id.bookButton);

            slotDate.setText(slot.getDate());
            slotTime.setText(slot.getStartTime() + " - " + slot.getEndTime());

            bookButton.setOnClickListener(v -> {
                viewModel.bookSession(studentId, slot, slot.isAutoApprove());
                bookButton.setEnabled(false);
                bookButton.setText("Booking...");
            });

            container.addView(slotCard);
        }
    }

    private void showError(String message) {
        if (message != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void onBookingSuccess(Boolean success) {
        if (success != null && success) {
            Toast.makeText(getContext(), "Session booked successfully!", Toast.LENGTH_LONG).show();
            searchResultsContainer.removeAllViews();
            courseCodeField.setText("");
        }
    }
}
