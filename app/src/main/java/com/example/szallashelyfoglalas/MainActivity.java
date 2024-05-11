package com.example.szallashelyfoglalas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.szallashelyfoglalas.dao.UserDao;
import com.example.szallashelyfoglalas.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATION = 101;
    RecyclerView propertiesRV;
    private NotificationHelper mNotificationHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View viewToAnimate = findViewById(R.id.activity_main);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        viewToAnimate.startAnimation(fadeInAnimation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateWelcomeMessage();
        propertiesRV = findViewById(R.id.propertyPreviewList);

        mNotificationHelper = new NotificationHelper(this);
        if (!mNotificationHelper.areNotificationsEnabled()) {
            mNotificationHelper.requestNotificationPermission(this);
        }
        mNotificationHelper.send("Welcome to our Booking App!");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper notificationHelper = new NotificationHelper(this);
                notificationHelper.send("Welcome to our Booking App!");
            } else {
                // Permission was denied
            }
        }
    }

    private void updateWelcomeMessage() {
        TextView welcomeText = findViewById(R.id.welcomeText);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            UserDao.readForId(userId)
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            String fullName = user.getFirstName() + " " + user.getLastName();
                            setGreeting(welcomeText, fullName);
                        } else {
                            setGreeting(welcomeText, "Guest");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching user data", e);
                        setGreeting(welcomeText, "Guest");
                    });
        } else {
            setGreeting(welcomeText, "Guest");
        }
    }

    private void setGreeting(TextView welcomeText, String name) {
        String greeting;
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay > 6 && timeOfDay < 12) {
            greeting = "Good Morning";
        } else if (timeOfDay < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        welcomeText.setText(String.format("%s, %s!", greeting, name));
    }
}