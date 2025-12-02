package com.project.data.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.SessionRequest;

import java.util.Date;

public class SessionRequestRepository {
    private static final String TAG = "SessionRequestRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference repo = db.collection("sessionRequests");

    public Task<Void> updateSessionRequest(String sessionID, SessionRequest session) {
        return repo.document(sessionID).set(session);
    }

    // get all sessions associated to a single tutor
    public Task<QuerySnapshot> getSessionRequestByTutorID(String tutorID) {
        return repo.whereEqualTo("tutorID", tutorID).get();
    }

    public Task<QuerySnapshot> getUpcomingTutorSessions(String tutorID, Date date) {
        return repo
                .whereEqualTo("tutorID", tutorID)
                .whereGreaterThan("startDate", date)
                .get();
    }

    public Task<QuerySnapshot> getPastTutorSessions(String tutorID, Date date) {
        return repo
                .whereEqualTo("tutorID", tutorID)
                .whereLessThan("endDate", date)
                .get();
    }

    public Task<QuerySnapshot> getPendingTutorSessions(String tutorID) {
        return repo
                .whereEqualTo("tutorID", tutorID)
                .whereEqualTo("status", "pending")
                .get()
                .addOnFailureListener(error -> {
                   Log.e(TAG, error.toString());
                });
    }

    public Task<QuerySnapshot> getSessionsByStudentID(String studentID) {
        return repo.whereEqualTo("studentID", studentID).get();
    }

    public Task<QuerySnapshot> getUpcomingStudentSessions(String studentID, Date date) {
        return repo
                .whereEqualTo("studentID", studentID)
                .whereGreaterThan("startDate", date)
                .get();
    }

    public Task<QuerySnapshot> getPastStudentSessions(String studentID, Date date) {
        return repo
                .whereEqualTo("studentID", studentID)
                .whereLessThan("endDate", date)
                .get();
    }

    public Task<QuerySnapshot> getPendingStudentSessions(String studentID) {
        return repo
                .whereEqualTo("studentID", studentID)
                .whereEqualTo("status", "pending")
                .get()
                .addOnFailureListener(error -> {
                    Log.e(TAG, error.toString());
                });
    }

    public Task<QuerySnapshot> getSessionsBySlotID(String slotID) {
        return repo.whereEqualTo("slotID", slotID).get();
    }

    public boolean canCancelSession(SessionRequest session) {
        if (session == null || session.getStartDate() == null) {
            return false;
        }
        long timeUntilStart = session.getStartDate().getTime() - System.currentTimeMillis();
        return timeUntilStart > 24 * 60 * 60 * 1000;
    }
}
