package com.example.szallashelyfoglalas.dao;


import android.util.Log;

import com.example.szallashelyfoglalas.model.Booking;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingDao {
    private static final String collection = "Booking";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Task<List<Booking>> fetchBookingsByUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<List<Booking>> taskCompletionSource = new TaskCompletionSource<>();

        db.collection(collection)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        bookings.add(documentToObject(document));
                    }
                    taskCompletionSource.setResult(bookings);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching bookings", e);
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }


    public static Booking documentToObject(DocumentSnapshot document) {
        Booking booking = new Booking();
        booking.setBookingId(document.getId());
        booking.setUserId(document.getString("userId"));
        booking.setPropertyId(document.getString("propertyId"));
        booking.setTotalPrice(document.getDouble("totalPrice"));
        booking.setGuestCount(Math.toIntExact(document.getLong("guestCount")));

        Map<String, Object> startDateMap = (Map<String, Object>) document.get("startDate");
        Map<String, Object> endDateMap = (Map<String, Object>) document.get("endDate");

        booking.setStartDate(parseDateFromMap(startDateMap));
        booking.setEndDate(parseDateFromMap(endDateMap));

        return booking;
    }

    public static LocalDate parseDateFromMap(Map<String, Object> dateMap) {
        if (dateMap == null) return null;

        int year = ((Number) dateMap.get("year")).intValue();
        int month = ((Number) dateMap.get("monthValue")).intValue();
        int dayOfMonth = ((Number) dateMap.get("dayOfMonth")).intValue();

        return LocalDate.of(year, month, dayOfMonth);
    }

    public static Task<Void> update(Booking booking) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (booking.getBookingId() == null || booking.getBookingId().isEmpty()) {
            Log.e("UpdateError", "Booking ID is null or empty");
            return Tasks.forException(new IllegalArgumentException("Booking ID cannot be null or empty"));
        }
        return db.collection(collection)
                .document(booking.getBookingId())
                .set(booking);
    }

    public static Task<QuerySnapshot> readAll(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection)
                .get();
    }

    public static Task<Booking> readById(String bookingId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<Booking> taskCompletionSource = new TaskCompletionSource<>();

        db.collection(collection).document(bookingId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Booking booking = documentToObject(documentSnapshot);
                        taskCompletionSource.setResult(booking);
                    } else {
                        taskCompletionSource.setException(new Exception("No booking found with ID: " + bookingId));
                    }
                })
                .addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }



    public static Task<Void> create(Booking model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(model.getBookingId() == null || model.getBookingId().isEmpty()){
            model.setBookingId(db.collection(collection).document().getId());
        }
        return db.collection(collection)
                .document(model.getBookingId())
                .set(model);

    }


    public static Task<Void> delete(Booking booking) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection).document(booking.getBookingId()).delete();
    }
}
