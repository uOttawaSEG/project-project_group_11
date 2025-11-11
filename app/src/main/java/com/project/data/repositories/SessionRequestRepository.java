package com.project.data.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.SessionRequest;

public class SessionRequestRepository {
    private static final String TAG = "SessionRequestRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference repo = db.collection("sessionRequests");

    public Task<Void> createSessionRequest(String sessionID, SessionRequest request) {
        return repo.document(sessionID).set(request);
    }

    public Task<Void> deleteSessionRequest(String sessionID) {
        return repo.document(sessionID).delete();
    }

    public Task<DocumentSnapshot> getSessionRequest(String sessionID) {
        return repo.document(sessionID).get();
    }

    // get all sessions associated to an availability slot
    public Task<QuerySnapshot> getSessionRequestsBySlotID(String slotID) {
        return repo.whereEqualTo("slotID", slotID).get();
    }

    // get all sessions associated to a single tutor
    public Task<QuerySnapshot> getSessionRequestByTutorID(String tutorID) {
        return repo.whereEqualTo("tutorID", tutorID).get();
    }
}
