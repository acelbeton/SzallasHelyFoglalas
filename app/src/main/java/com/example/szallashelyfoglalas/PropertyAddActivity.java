package com.example.szallashelyfoglalas;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.szallashelyfoglalas.handler.PictureHandler;
import com.example.szallashelyfoglalas.handler.PropertyHandler;
import com.example.szallashelyfoglalas.model.Picture;
import com.example.szallashelyfoglalas.util.ServerUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import com.example.szallashelyfoglalas.model.Property;
import com.squareup.picasso.Picasso;

public class PropertyAddActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", "Creating options menu");
        super.onCreateOptionsMenu(menu);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.guest_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.signed_in_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.login){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if(itemId == R.id.home){
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (itemId == R.id.new_property) {
            startActivity(new Intent(this, PropertyAddActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (itemId == R.id.my_bookings) {
            startActivity(new Intent(this, MyBookingsViewActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (itemId == R.id.searchLayout) {
            startActivity(new Intent(this, SearchActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
        return true;
    }

    private EditText editTextCity, editTextStreet, etPricePerNight, etMaxGuests, editPropertyType;
    private ImageView imageProperty;
    private Button btnSave, btnUploadImage;
    private Uri imageUri;
    private Property currentProperty;
    private ServerUtil su;
    private final ActivityResultLauncher<PickVisualMediaRequest> requestPermissionLauncher =
            registerForActivityResult(new PickCorrectPic(), uri ->{
                if(uri != null){
                    Picasso.get().load(uri).into(imageProperty);
                }
                imageUri = uri;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_add);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adder), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        su = new ServerUtil();
        initializeUI();

        btnSave.setOnClickListener(v -> saveProperty());

        if(currentProperty != null && currentProperty.getImageId() != null && !currentProperty.getImageId().isEmpty()){
            su.getDownloadUrl(currentProperty.getImageId(), imageProperty);
        }
    }

    private void initializeUI() {
        editTextCity = findViewById(R.id.editTextCity);
        editTextStreet = findViewById(R.id.editTextStreet);
        editPropertyType = findViewById(R.id.editPropertyType);
        etPricePerNight = findViewById(R.id.etPricePerNight);
        etMaxGuests = findViewById(R.id.etMaxGuests);
        btnSave = findViewById(R.id.btnSave);
        imageProperty = findViewById(R.id.imageProperty);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture(v);
            }
        });
    }

    private void saveProperty() {
        if (currentProperty == null) {
            currentProperty = new Property();
        }
        setPropertyDataFromForm();

        if (imageUri != null) {
            Picture newPicture = new Picture();
            newPicture.setExtension(getFileExtension(imageUri));
            newPicture.setUploader(FirebaseAuth.getInstance().getCurrentUser().getUid());

            PictureHandler.create(newPicture, imageUri, this, new PictureHandler.PictureUploadCallback() {
                @Override
                public void onUploadSuccess(String imageId) {
                    currentProperty.setImageId(imageId);
                    savePropertyToFirestore();
                }

                @Override
                public void onUploadFailure(Exception e) {
                    Toast.makeText(PropertyAddActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Please select an image to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePropertyToFirestore() {
        PropertyHandler.create(currentProperty).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Property and image saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save property", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPropertyDataFromForm() {
        currentProperty.setAddress(new HashMap<String, String>() {{
            put("city", editTextCity.getText().toString().trim());
            put("street", editTextStreet.getText().toString().trim());
        }});
        currentProperty.setPricePerNight(Double.parseDouble(etPricePerNight.getText().toString()));
        currentProperty.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentProperty.setMaxGuests(Integer.parseInt(etMaxGuests.getText().toString()));
        currentProperty.setType(editPropertyType.getText().toString().trim());
    }

    public void selectPicture(View v){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            selectPicPicSelectPart();
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                requestPermissions( new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 100);
            } else {
                requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    selectPicPicSelectPart();
                }else{
                    Toast.makeText(this, "Access denied, cannot add picture", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void selectPicPicSelectPart(){
        requestPermissionLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
}
