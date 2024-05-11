package com.example.szallashelyfoglalas.dao;

import com.example.szallashelyfoglalas.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDao {
    private static final String collection = "User";
    public static Task<Void> create(User model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection).document(model.getUserId()).set(model);
    }

    public static Task<DocumentSnapshot> readForId(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection)
                .document(userId)
                .get();
    }
}
