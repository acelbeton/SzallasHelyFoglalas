package com.example.szallashelyfoglalas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.szallashelyfoglalas.dao.PropertyDao;
import com.example.szallashelyfoglalas.model.Property;
import com.example.szallashelyfoglalas.util.ServerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyViewActivity extends AppCompatActivity {

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

    private ImageView image;
    private TextView address;
    private TextView propertyType;
    private TextView guestCapacity;
    private TextView pricePerNight;
    private Button buttonBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewProperty), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        image = findViewById(R.id.image);
        address = findViewById(R.id.address);
        propertyType = findViewById(R.id.propertyType);
        guestCapacity = findViewById(R.id.guestCapacity);
        pricePerNight = findViewById(R.id.etPricePerNight);
        buttonBook = findViewById(R.id.buttonBook);
        AtomicReference<Integer> maxGuestsTocheck = new AtomicReference<>();
        AtomicReference<Double> pricePerNightTocheck = new AtomicReference<>();


        String propertyId = getIntent().getStringExtra("propertyId");

        PropertyDao.readById(propertyId)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        Property property = document.toObject(Property.class);
                                        assert property != null;
                                        new ServerUtil().getDownloadUrl(property.getImageId(), image);
                                        address.setText("Address: " + property.getAddress().get("city") + ", " + property.getAddress().get("street"));
                                        propertyType.setText("Type: " + property.getType());
                                        guestCapacity.setText("Capacity: " + property.getMaxGuests());
                                        maxGuestsTocheck.set(property.getMaxGuests());
                                        pricePerNightTocheck.set(property.getPricePerNight());
                                        pricePerNight.setText("Price per night: $" + property.getPricePerNight());
                                    }
                                }
                            }
                        });


        buttonBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("propertyId", propertyId);
            intent.putExtra("maxGuests", maxGuestsTocheck.get());
            intent.putExtra("pricePerNight", pricePerNightTocheck.get());
            startActivity(intent);
        });
    }

}