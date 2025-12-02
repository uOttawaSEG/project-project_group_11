package com.project.data.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.User;

public class UserRepository {

    private static final String TAG = "UserRepository";

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

    public Task<QuerySnapshot> getTutorsByCourseName(String courseName) {
        return db.collection("users")
                .whereEqualTo("role", "Tutor")
                .whereArrayContains("coursesOffered", courseName)
                .get()
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error fetching tutors", error));
    }
}