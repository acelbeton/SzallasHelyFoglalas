package com.example.szallashelyfoglalas;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.szallashelyfoglalas.model.Booking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class BookingActivity extends AppCompatActivity {

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

    private FirebaseFirestore db;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextGuests;
    private Button buttonConfirmBooking;
    private String propertyId;
    private double pricePerNight;
    private int maxGuests;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayoutBooking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextGuests = findViewById(R.id.editTextGuestCount);
        buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);

        propertyId = getIntent().getStringExtra("propertyId");
        pricePerNight = getIntent().getDoubleExtra("pricePerNight", 0.0);
        maxGuests = getIntent().getIntExtra("maxGuests", 0);

        buttonConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    public void showStartDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editTextStartDate.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void showEndDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editTextEndDate.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void confirmBooking() {
        try {
            LocalDate startDate = LocalDate.parse(editTextStartDate.getText().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate = LocalDate.parse(editTextEndDate.getText().toString(), DateTimeFormatter.ISO_LOCAL_DATE);

            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_LONG).show();
                return;
            }
            if (editTextGuests.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter the number of guests", Toast.LENGTH_LONG).show();
                return;
            }
            if (Integer.parseInt(editTextGuests.getText().toString()) > maxGuests) {
                Toast.makeText(this, "Maximum number of guests is " + maxGuests, Toast.LENGTH_LONG).show();
                return;
            }

            int guests = Integer.parseInt(editTextGuests.getText().toString());
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            double totalPrice = days * pricePerNight;
            Booking newBooking = new Booking(UUID.randomUUID().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), propertyId, startDate, endDate, totalPrice, guests);
            saveBookingToFirestore(newBooking);
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Please enter valid dates", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number of guests", Toast.LENGTH_LONG).show();
        }
    }

    private void saveBookingToFirestore(Booking booking) {
        db.collection("Booking").document(booking.getBookingId()).set(booking).
                addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving booking", Toast.LENGTH_SHORT).show();
                });
    }

}