package com.project.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.project.data.model.SessionRequest;
import com.project.data.repositories.SessionRequestRepository;

import java.util.Date;
import java.util.List;

public class ManageSessionViewModel extends ViewModel {

    // database repos
    private final SessionRequestRepository sessionRequestRepo = new SessionRequestRepository();

    // observables
    private final MutableLiveData<List<SessionRequest>> sessionRequests = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void getUpcomingTutorSessions(String tutorID, Date date) {
        sessionRequestRepo.getUpcomingTutorSessions(tutorID, date)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> allSessions = query.toObjects(SessionRequest.class);
                    List<SessionRequest> upcomingSessions = new java.util.ArrayList<>();

                    for (SessionRequest session : allSessions) {
                        if (session.getEndDate() != null && session.getEndDate().toDate().after(date)) {
                            upcomingSessions.add(session);
                        }
                    }

                    sessionRequests.postValue(upcomingSessions);

                    if (upcomingSessions.isEmpty()) {
                        errorMessage.postValue("No upcoming sessions");
                    } else {
                        errorMessage.postValue(null);
                    }
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading upcoming sessions");
                });
    }

    public void getPastTutorSessions(String tutorID, Date date) {
        sessionRequestRepo.getPastTutorSessions(tutorID, date)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> allSessions = query.toObjects(SessionRequest.class);
                    List<SessionRequest> pastSessions = new java.util.ArrayList<>();

                    for (SessionRequest session : allSessions) {
                        if (session.getEndDate() != null && session.getEndDate().toDate().before(date)) {
                            pastSessions.add(session);
                        }
                    }

                    sessionRequests.postValue(pastSessions);

                    if (pastSessions.isEmpty()) {
                        errorMessage.postValue("No past sessions");
                    } else {
                        errorMessage.postValue(null);
                    }
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading past sessions");
                });
    }

    public void getPendingTutorSessions(String tutorID) {
        sessionRequestRepo.getPendingTutorSessions(tutorID)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);
                    sessionRequests.postValue(sessions);

                    if (sessions.isEmpty()) {
                        errorMessage.postValue("No session requests");
                    } else {
                        errorMessage.postValue(null);
                    }
                });
    }

    public void updateSessionRequest(SessionRequest session, String filterOption, Date date, String userId) {
        sessionRequestRepo.updateSessionRequest(session.getSessionID(), session)
                .addOnSuccessListener(v -> {
                    if (filterOption.equals("Upcoming Sessions")) {
                        if (userId.equals(session.getTutorID())) {
                            getUpcomingTutorSessions(userId, date);
                        } else {
                            getUpcomingStudentSessions(userId, date);
                        }
                    }
                    else if (filterOption.equals("Past Sessions")) {
                        if (userId.equals(session.getTutorID())) {
                            getPastTutorSessions(userId, date);
                        } else {
                            getPastStudentSessions(userId, date);
                        }
                    }
                    else if (filterOption.equals("Session Requests")) {
                        getPendingTutorSessions(userId);
                    }
                    else if (filterOption.equals("All Sessions")) {
                        getAllStudentSessions(userId);
                    }
                    else if (filterOption.equals("Pending Requests")) {
                        getPendingStudentSessions(userId);
                    }
                });
    }

    public void getAllStudentSessions(String studentID) {
        sessionRequestRepo.getSessionsByStudentID(studentID)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);
                    sessionRequests.postValue(sessions);

                    if (sessions.isEmpty()) {
                        errorMessage.postValue("No sessions found");
                    } else {
                        errorMessage.postValue(null);
                    }
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading sessions");
                });
    }

    public void getUpcomingStudentSessions(String studentID, Date date) {
        sessionRequestRepo.getUpcomingStudentSessions(studentID, date)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> allSessions = query.toObjects(SessionRequest.class);
                    List<SessionRequest> upcomingSessions = new java.util.ArrayList<>();

                    for (SessionRequest session : allSessions) {
                        if (session.getEndDate() != null && session.getEndDate().toDate().after(date)) {
                            upcomingSessions.add(session);
                        }
                    }

                    sessionRequests.postValue(upcomingSessions);

                    if (upcomingSessions.isEmpty()) {
                        errorMessage.postValue("No upcoming sessions");
                    } else {
                        errorMessage.postValue(null);
                    }
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading upcoming sessions");
                });
    }

    public void getPastStudentSessions(String studentID, Date date) {
        sessionRequestRepo.getPastStudentSessions(studentID, date)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> allSessions = query.toObjects(SessionRequest.class);
                    List<SessionRequest> pastSessions = new java.util.ArrayList<>();

                    for (SessionRequest session : allSessions) {
                        if (session.getEndDate() != null && session.getEndDate().toDate().before(date)) {
                            pastSessions.add(session);
                        }
                    }

                    sessionRequests.postValue(pastSessions);

                    if (pastSessions.isEmpty()) {
                        errorMessage.postValue("No past sessions");
                    } else {
                        errorMessage.postValue(null);
                    }
                })
                .addOnFailureListener(error -> {
                    errorMessage.postValue("Error loading past sessions");
                });
    }

    public void getPendingStudentSessions(String studentID) {
        sessionRequestRepo.getPendingStudentSessions(studentID)
                .addOnSuccessListener(query -> {
                    List<SessionRequest> sessions = query.toObjects(SessionRequest.class);
                    sessionRequests.postValue(sessions);

                    if (sessions.isEmpty()) {
                        errorMessage.postValue("No pending requests");
                    } else {
                        errorMessage.postValue(null);
                    }
                });
    }

    public boolean canCancelSession(SessionRequest session) {
        return sessionRequestRepo.canCancelSession(session);
    }

    public LiveData<List<SessionRequest>> getSessionRequests() {
        return sessionRequests;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
