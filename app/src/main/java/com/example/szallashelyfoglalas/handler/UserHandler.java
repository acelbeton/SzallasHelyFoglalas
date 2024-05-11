package com.example.szallashelyfoglalas.handler;

import com.example.szallashelyfoglalas.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;

import java.util.Map;

public class UserHandler {
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
