package com.project.database.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.backend.models.User;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<DocumentReference> addUser(User user) { // task to register new user, gives path to new user
        return db.collection("users").add(user)
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "User added with ID: " + doc.getId()))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error adding user", e));
    }

    public Task<QuerySnapshot> getAllUsers() {
        return db.collection("users").get();
    }

    public Task<QuerySnapshot> loginCheck(String email, String password) { // does account exist already
        return db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get();

    }
}