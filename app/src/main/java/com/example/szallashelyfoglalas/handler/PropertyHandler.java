package com.example.szallashelyfoglalas.handler;

import com.example.szallashelyfoglalas.model.Picture;
import com.example.szallashelyfoglalas.model.Property;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.CollectionReference;

import java.util.Map;
import java.util.Objects;

public class PropertyHandler {
    private static final String collection = "Property";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Task<QuerySnapshot> readByLoggedInUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection)
                .whereEqualTo("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get();
    }
    public static Task<QuerySnapshot> readAll(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection)
                .get();
    }

    public static Task<QuerySnapshot> readById(String propertyId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(collection)
                .whereEqualTo("propertyId", propertyId )
                .get();
    }

    public static Task<Void> create(Property model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(model.getPropertyId() == null || model.getPropertyId().isEmpty()){
            model.setPropertyId(db.collection(collection).document().getId());
        }
        return db.collection(collection)
                .document(model.getPropertyId())
                .set(model);

    }


    public static void delete(Property model){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(!Objects.equals(model.getImageId(), "")){
            PictureHandler.delete(model.getImageId());
        }
        db.collection(collection).document(model.getPropertyId()).delete().addOnFailureListener(Throwable::printStackTrace);
    }

}
