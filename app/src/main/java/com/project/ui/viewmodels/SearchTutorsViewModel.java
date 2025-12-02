package com.project.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.AvailabilitySlot;
import com.project.data.model.Rating;
import com.project.data.model.SessionRequest;
import com.project.data.model.Tutor;
import com.project.data.repositories.AvailabilitySlotRepository;
import com.project.data.repositories.RatingRepository;
import com.project.data.repositories.SessionRequestRepository;
import com.project.data.repositories.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SearchTutorsViewModel extends ViewModel {

    private final UserRepository userRepo = new UserRepository();
    private final AvailabilitySlotRepository availabilityRepo = new AvailabilitySlotRepository();
    private final SessionRequestRepository sessionRequestRepo = new SessionRequestRepository();
    private final RatingRepository ratingRepo = new RatingRepository();

    private final MutableLiveData<List<Tutor>> tutors = new MutableLiveData<>();
    private final MutableLiveData<List<AvailabilitySlot>> availabilitySlots = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> bookingSuccess = new MutableLiveData<>();

    public void searchTutorsByCourse(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            errorMessage.postValue("Please enter a course name");
            return;
        }

        userRepo.getTutorsByCourseName(courseName.trim())
                .addOnSuccessListener(this::onSearchSuccess)
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error searching tutors");
                });
    }

    public void loadTutorAvailability(String tutorId) {
        availabilityRepo.getTutorSlots(tutorId)
                .addOnSuccessListener(this::onAvailabilityLoadSuccess)
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading availability");
                });
    }

    public void bookSession(String studentId, AvailabilitySlot slot, boolean autoApprove) {
        sessionRequestRepo.getSessionsBySlotID(slot.getSlotId())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> existingSessions = query.toObjects(SessionRequest.class);

                    for (SessionRequest session : existingSessions) {
                        if (session.getStatus().equals("approved") || session.getStatus().equals("pending")) {
                            if (session.getStudentID().equals(studentId)) {
                                errorMessage.postValue("You have already booked this slot");
                            } else {
                                errorMessage.postValue("This slot is already booked");
                            }
                            return;
                        }
                    }

                    createSessionRequest(studentId, slot, autoApprove);
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Failed to check existing bookings");
                });
    }

    private void createSessionRequest(String studentId, AvailabilitySlot slot, boolean autoApprove) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date slotDate = dateFormat.parse(slot.getDate());
            Date startTime = timeFormat.parse(slot.getStartTime());
            Date endTime = timeFormat.parse(slot.getEndTime());

            if (slotDate == null || startTime == null || endTime == null) {
                errorMessage.postValue("Invalid date or time format");
                return;
            }

            Date startDate = combineDateAndTime(slotDate, startTime);
            Date endDate = combineDateAndTime(slotDate, endTime);

            String sessionId = UUID.randomUUID().toString();
            String status = autoApprove ? "approved" : "pending";

            SessionRequest session = new SessionRequest(
                    sessionId,
                    slot.getSlotId(),
                    slot.getTutorId(),
                    studentId,
                    slot.getCourse(),
                    new Timestamp(startDate),
                    new Timestamp(endDate),
                    status
            );

            sessionRequestRepo.updateSessionRequest(sessionId, session)
                    .addOnSuccessListener(v -> {
                        bookingSuccess.postValue(true);
                    })
                    .addOnFailureListener(error -> {
                        errorMessage.postValue("Failed to book session");
                    });

        } catch (ParseException e) {
            errorMessage.postValue("Error processing date/time");
        }
    }

    private void onSearchSuccess(QuerySnapshot query) {
        List<Tutor> tutorList = query.toObjects(Tutor.class);
        if (tutorList.isEmpty()) {
            errorMessage.postValue("No tutors found for this course");
        }
        tutors.postValue(tutorList);
    }

    private void onAvailabilityLoadSuccess(QuerySnapshot query) {
        List<AvailabilitySlot> allSlots = query.toObjects(AvailabilitySlot.class);
        List<AvailabilitySlot> availableSlots = new ArrayList<>();
        Date now = new Date();

        for (AvailabilitySlot slot : allSlots) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date slotDate = dateFormat.parse(slot.getDate());
                if (slotDate != null && !slotDate.before(now)) {
                    checkSlotAvailability(slot, availableSlots);
                }
            } catch (ParseException e) {
            }
        }
    }

    private void checkSlotAvailability(AvailabilitySlot slot, List<AvailabilitySlot> availableSlots) {
        sessionRequestRepo.getSessionsBySlotID(slot.getSlotId())
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);
                    boolean isBooked = false;

                    for (SessionRequest session : sessions) {
                        if (session.getStatus().equals("approved") || session.getStatus().equals("pending")) {
                            isBooked = true;
                            break;
                        }
                    }

                    if (!isBooked) {
                        availableSlots.add(slot);
                    }

                    availabilitySlots.postValue(availableSlots);
                });
    }

    private Date combineDateAndTime(Date date, Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        String dateStr = dateFormat.format(date);
        String timeStr = timeFormat.format(time);
        String combined = dateStr + " " + timeStr;

        try {
            return fullFormat.parse(combined);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public LiveData<List<Tutor>> getTutors() {
        return tutors;
    }

    public LiveData<List<AvailabilitySlot>> getAvailabilitySlots() {
        return availabilitySlots;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getBookingSuccess() {
        return bookingSuccess;
    }

    public void loadTutorRating(String tutorId, RatingCallback callback) {
        ratingRepo.getRatingsByTutorId(tutorId)
                .addOnSuccessListener(query -> {
                    List<Rating> ratings = query.toObjects(Rating.class);
                    if (ratings.isEmpty()) {
                        callback.onRatingLoaded(0.0, 0);
                    } else {
                        int total = 0;
                        for (Rating rating : ratings) {
                            total += rating.getRating();
                        }
                        double average = (double) total / ratings.size();
                        callback.onRatingLoaded(average, ratings.size());
                    }
                });
    }

    public interface RatingCallback {
        void onRatingLoaded(double averageRating, int count);
    }
}
