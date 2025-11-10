package com.project.data.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.RegistrationRequest;
import com.project.data.model.User;

public class RegistrationRequestRepository {

    // handles registration requests collection in firestore
    private static final String TAG = "RegistrationRequestRepository"; // tag for logcat
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // firestore instance
    private final UserRepository userRepository = new UserRepository(); // needed for approval flow

    public Task<Void> createRequest(RegistrationRequest request) {
        return db.collection("registrationRequests").document(request.getUserId()).set(request) // link firebase auth uid to request document
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "Registration request created for user: " + request.getUserId()))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error creating registration request", error));
    }

    // document is one document, query is multiple documents
    public Task<DocumentSnapshot> getRequest(String userId) {
        return db.collection("registrationRequests").document(userId).get();
    }

    public Task<QuerySnapshot> getRequestsByStatus(String status) { // for rejected requests tab
        return db.collection("registrationRequests")
                .whereEqualTo("status", status) // filter by status field
                .get();
    }

    public Task<QuerySnapshot> getAllRequests() {
        return db.collection("registrationRequests").get(); // fetch all requests regardless of status
    }

    public Task<Void> updateRequestStatus(String userId, String newStatus) {
        return db.collection("registrationRequests").document(userId)
                .update("status", newStatus) // update only the status field
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "Request status updated to: " + newStatus))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error updating request status", error));
    }

    public Task<Void> approveAndMoveToUsers(String userId) {
        // fetch request, convert to user, save to users collection, update status
        return getRequest(userId)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DocumentSnapshot doc = task.getResult();
                    if (!doc.exists()) {
                        throw new Exception("Registration request not found");
                    }

                    RegistrationRequest request = doc.toObject(RegistrationRequest.class);
                    User user = request.toUser(); // backend handles conversion logic

                    return userRepository.createUserProfile(userId, user);
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return updateRequestStatus(userId, "approved");
                })
                .addOnSuccessListener(result ->
                        Log.d(TAG, "Request approved and user created"))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error approving request", error));
    }
}
