package com.example.szallashelyfoglalas;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

public class PickCorrectPic extends ActivityResultContracts.PickVisualMedia {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, @NonNull PickVisualMediaRequest input) {
        Intent intent= super.createIntent(context, input);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
        return intent;

    }
}