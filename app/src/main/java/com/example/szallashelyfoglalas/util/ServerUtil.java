package com.example.szallashelyfoglalas.util;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.szallashelyfoglalas.model.Picture;
import com.squareup.picasso.Picasso;
import com.example.szallashelyfoglalas.handler.PictureHandler;

public class ServerUtil {
    public ServerUtil() {
    }
    public void getDownloadUrl(String imageId, ImageView pic) {

        PictureHandler.readById(imageId)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Picture image;
                        image = task.getResult().toObjects(Picture.class).get(0);


                        PictureHandler.readDownloadUri(image)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Uri downloadUrl = task1.getResult();
                                        Picasso.get().load(downloadUrl).into(pic);
                                    }else {
                                        Log.e("GetImageToShow", "getDownloadUrl: ",task1.getException() );
                                    }
                                });
                    }else{
                        Log.e("GetImageToShow", "populateImage: ", task.getException());
                    }
                });
    }
}
