package com.example.szallashelyfoglalas.dao;

import com.example.szallashelyfoglalas.model.Property;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class PropertyDao {
    private static final String collection = "Property";

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
            PictureDao.delete(model.getImageId());
        }
        db.collection(collection).document(model.getPropertyId()).delete().addOnFailureListener(Throwable::printStackTrace);
    }

}
