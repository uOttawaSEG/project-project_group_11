package com.project.data.repositories;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.data.model.AvailabilitySlot;

public class AvailabilitySlotRepository {

    private static final String TAG = "AvailabilitySlotRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> createSlot(AvailabilitySlot slot) {
        return db.collection("availabilitySlots").document(slot.getSlotId()).set(slot)
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "Availability slot created: " + slot.getSlotId()))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error creating availability slot", error));
    }

    public Task<QuerySnapshot> getTutorSlots(String tutorId) {
        return db.collection("availabilitySlots")
                .whereEqualTo("tutorId", tutorId)
                .get();
    }

    public Task<Void> deleteSlot(String slotId) {
        return db.collection("availabilitySlots").document(slotId).delete()
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "Availability slot deleted: " + slotId))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error deleting availability slot", error));
    }

    public Task<QuerySnapshot> checkOverlappingSlots(String tutorId, String date, String startTime, String endTime) {
        return db.collection("availabilitySlots")
                .whereEqualTo("tutorId", tutorId)
                .whereEqualTo("date", date)
                .get();
    }

    public Task<Void> updateSlotBookingStatus(String slotId, boolean isBooked) {
        return db.collection("availabilitySlots").document(slotId)
                .update("booked", isBooked)
                .addOnSuccessListener(doc ->
                        Log.d(TAG, "Slot booking status updated: " + slotId))
                .addOnFailureListener(error ->
                        Log.w(TAG, "Error updating booking status", error));
    }
}
