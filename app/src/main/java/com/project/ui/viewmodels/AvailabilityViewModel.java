package com.project.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.AvailabilitySlot;
import com.project.data.model.SessionRequest;
import com.project.data.repositories.AvailabilitySlotRepository;
import com.project.data.repositories.SessionRequestRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AvailabilityViewModel extends ViewModel {

    // database stuff
    private final AvailabilitySlotRepository repository = new AvailabilitySlotRepository();
    private final SessionRequestRepository sessionRequestRepository = new SessionRequestRepository();

    // observable data
    private final MutableLiveData<List<AvailabilitySlot>> availabilitySlots = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    // other data
    private String currentTutorId;
    private String course;
    private String date;
    private String startTime;
    private String endTime;
    private boolean autoApprove;

    public void loadTutorSlots(String tutorId) {
        this.currentTutorId = tutorId;
        repository.getTutorSlots(tutorId)
                .addOnSuccessListener(this::onLoadSlotsSuccess)
                .addOnFailureListener(this::onLoadSlotsFailure);
    }

    public void createAvailabilitySlot(String tutorId, String course, String date, String startTime, String endTime, boolean autoApprove) {
        this.currentTutorId = tutorId;
        this.course = course;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.autoApprove = autoApprove;

        // validate inputs
        if (!validateDate(date)) {
            postError("Cannot select a past date");
            return;
        }

        if (!validateTimeFormat(startTime) || !validateTimeFormat(endTime)) {
            postError("Times must be in 30-minute increments");
            return;
        }

        if (!validateTimeOrder(startTime, endTime)) {
            postError("End time must be after start time");
            return;
        }

        repository.checkOverlappingSlots(tutorId, date, startTime, endTime)
                .addOnSuccessListener(this::onCheckOverlapSuccess)
                .addOnFailureListener(this::onCheckOverlapFailure);
    }

    public void deleteAvailabilitySlot(String slotId) {
        sessionRequestRepository.getSessionsBySlotID(slotId)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);
                    boolean hasBookedSessions = false;

                    for (SessionRequest session : sessions) {
                        if (session.getStatus().equals("approved") || session.getStatus().equals("pending")) {
                            hasBookedSessions = true;
                            break;
                        }
                    }

                    if (hasBookedSessions) {
                        postError("Cannot delete slot with pending or approved sessions");
                    } else {
                        repository.deleteSlot(slotId)
                                .addOnSuccessListener(this::onDeleteSlotSuccess)
                                .addOnFailureListener(this::onDeleteSlotFailure);
                    }
                })
                .addOnFailureListener(this::onDeleteSlotFailure);
    }

    public LiveData<List<AvailabilitySlot>> getAvailabilitySlots() {
        return availabilitySlots;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    private void onCheckOverlapSuccess(QuerySnapshot querySnapshot) {
        if (hasOverlap(querySnapshot, startTime, endTime)) {
            postError("This slot overlaps with an existing slot");
            return;
        }

        String slotId = UUID.randomUUID().toString();
        AvailabilitySlot slot = new AvailabilitySlot(slotId, currentTutorId, course, date, startTime, endTime, autoApprove);

        repository.createSlot(slot)
                .addOnSuccessListener(this::onCreateSlotSuccess)
                .addOnFailureListener(this::onCreateSlotFailure);
    }

    private void onCreateSlotSuccess(Void aVoid) {
        postSuccess();
        loadTutorSlots(currentTutorId);
    }

    private void onDeleteSlotSuccess(Void aVoid) {
        postSuccess();
        loadTutorSlots(currentTutorId);
    }

    private void onLoadSlotsSuccess(QuerySnapshot querySnapshot) {
        List<AvailabilitySlot> slots = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            AvailabilitySlot slot = doc.toObject(AvailabilitySlot.class);
            if (slot != null) {
                slots.add(slot);
            }
        }
        availabilitySlots.postValue(slots);
    }

    private void onLoadSlotsFailure(Exception error) {
        Log.w("Firestore", "Error loading slots", error);
        postError("Error loading availability slots");
    }

    private void onCreateSlotFailure(Exception error) {
        Log.w("Firestore", "Error creating slot", error);
        postError("Error creating availability slot");
    }

    private void onCheckOverlapFailure(Exception error) {
        Log.w("Firestore", "Error checking overlaps", error);
        postError("Error validating slot");
    }

    private void onDeleteSlotFailure(Exception error) {
        Log.w("Firestore", "Error deleting slot", error);
        postError("Error deleting slot");
    }

    private boolean validateDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date slotDate = sdf.parse(date);

            SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todayString = dateOnly.format(new Date());
            Date today = dateOnly.parse(todayString);

            return slotDate != null && !slotDate.before(today);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateTimeFormat(String time) {
        String[] parts = time.split(":");
        if (parts.length != 2) return false;

        try {
            int minutes = Integer.parseInt(parts[1]);
            return minutes == 0 || minutes == 30;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateTimeOrder(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMin = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMin = Integer.parseInt(endParts[1]);

            int startTotal = startHour * 60 + startMin;
            int endTotal = endHour * 60 + endMin;

            return endTotal > startTotal;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasOverlap(QuerySnapshot querySnapshot, String newStartTime, String newEndTime) {
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            AvailabilitySlot slot = doc.toObject(AvailabilitySlot.class);
            if (slot != null && timesOverlap(slot.getStartTime(), slot.getEndTime(), newStartTime, newEndTime)) {
                return true;
            }
        }
        return false;
    }

    private boolean timesOverlap(String start1, String end1, String start2, String end2) {
        try {
            int start1Min = timeToMinutes(start1);
            int end1Min = timeToMinutes(end1);
            int start2Min = timeToMinutes(start2);
            int end2Min = timeToMinutes(end2);

            return (start2Min < end1Min && end2Min > start1Min);
        } catch (Exception e) {
            return false;
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private void postSuccess() {
        operationSuccess.postValue(true);
    }

    private void postError(String message) {
        errorMessage.postValue(message);
    }
}
