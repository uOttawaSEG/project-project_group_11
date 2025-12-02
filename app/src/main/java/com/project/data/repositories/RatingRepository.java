package com.project.data.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.Rating;

public class RatingRepository {
    private static final String TAG = "RatingRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference repo = db.collection("ratings");

    public Task<Void> saveRating(String ratingId, Rating rating) {
        return repo.document(ratingId).set(rating);
    }

    public Task<QuerySnapshot> getRatingBySessionId(String sessionId) {
        return repo
                .whereEqualTo("sessionId", sessionId)
                .get()
                .addOnFailureListener(error -> {
                    Log.e(TAG, error.toString());
                });
    }

    public Task<QuerySnapshot> getRatingsByTutorId(String tutorId) {
        return repo
                .whereEqualTo("tutorId", tutorId)
                .get()
                .addOnFailureListener(error -> {
                    Log.e(TAG, error.toString());
                });
    }
}
