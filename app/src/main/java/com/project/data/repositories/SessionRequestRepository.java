package com.project.data.repositories;

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
                .whereEqualTo("status", "approved")
                .whereGreaterThan("startDate", date)
                .get();
    }

    public Task<QuerySnapshot> getPastTutorSessions(String tutorID, Date date) {
        return repo
                .whereEqualTo("tutorID", tutorID)
                .whereEqualTo("status", "approved")
                .whereLessThan("endDate", date)
                .get();
    }
}
