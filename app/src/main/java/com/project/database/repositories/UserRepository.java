package com.project.database.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.backend.User;

public class UserRepository {

    // firebase auth is for email/password security
    // firestore is database for all extra details (name, phone, role etc.)
    private static final String TAG = "UserRepository"; // tag for logcat
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // firestore instance

    public Task<Void> createUserProfile(String userId, User user) {
        return db.collection("users").document(userId).set(user) // link firebase auth uid to a user document
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "User added with ID: " + userId))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error creating profile", error));
    }

    public Task<DocumentSnapshot> getUserProfile(String userId) {
        return db.collection("users").document(userId).get();
    }

    public Task<Void> updateUserProfile(String userId, User user) {
        return db.collection("users").document(userId).set(user);
    }

    public Task<Void> deleteUserProfile(String userId) {
        return db.collection("users").document(userId).delete();
    }
}