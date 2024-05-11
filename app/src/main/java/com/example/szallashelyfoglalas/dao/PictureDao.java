package com.example.szallashelyfoglalas.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.szallashelyfoglalas.model.Picture;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class PictureDao {

    private static final String collection = "Picture";

    private static final String bucketName = "pictures/";

    public static Task<QuerySnapshot> readById(String imageId){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        return db.collection(collection)
                .whereEqualTo("id", imageId )
                .get();
    }

    public static Task<Uri> readDownloadUri(Picture image) {
        return FirebaseStorage.getInstance()
                .getReference()
                .child(bucketName + image.getId() + "." + image.getExtension()).getDownloadUrl();
    }

    public interface PictureUploadCallback {
        void onUploadSuccess(String imageId);
        void onUploadFailure(Exception e);
    }

    public static void create(Picture model, Uri picUri, Context context, PictureUploadCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String documentId = db.collection(collection).document().getId();
        model.setId(documentId);

        StorageReference fileRef = storage.getReference().child(bucketName + model.getId() + "." + model.getExtension());
        UploadTask uploadTask = fileRef.putFile(picUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            db.collection(collection).document(documentId).set(model)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FirestoreSuccess", "DocumentSnapshot successfully written!");
                        callback.onUploadSuccess(documentId);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("FirestoreError", "Error writing document", e);
                        callback.onUploadFailure(e);
                    });
        }).addOnFailureListener(e -> {
            Log.e("UploadError", "Upload failed: " + e.toString());
            callback.onUploadFailure(e);
        });
    }



    public static String update(Picture model, Uri picUri) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage dbSt = FirebaseStorage.getInstance();

        db.collection(collection)
                .document(model.getId())
                .set(model)
                .addOnFailureListener(Throwable::printStackTrace);
        dbSt.getReference()
                .child(bucketName + model.getId() + "." + model.getExtension())
                .putFile(picUri)
                .addOnFailureListener(Throwable::printStackTrace);
        return model.getId();
    }


    public static void delete(String picId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage dbSt = FirebaseStorage.getInstance();
        db.collection(collection)
                .whereEqualTo("id", picId)
                .get()
                .addOnCompleteListener(pictureObj -> {
                    if (pictureObj.isSuccessful()) {
                        if(!pictureObj.getResult().toObjects(Picture.class).isEmpty()) {
                            Picture picture = pictureObj.getResult().toObjects(Picture.class).get(0);

                            dbSt.getReference().child(bucketName + picture.getId() + "." + picture.getExtension()).delete().addOnFailureListener(Throwable::printStackTrace);
                            db.collection(collection).document(picture.getId()).delete().addOnFailureListener(Throwable::printStackTrace);
                        }
                    }else {
                        Objects.requireNonNull(pictureObj.getException()).printStackTrace();
                    }
                });
    }
}
