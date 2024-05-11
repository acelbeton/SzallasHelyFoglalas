package com.example.szallashelyfoglalas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.szallashelyfoglalas.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.example.szallashelyfoglalas.dao.BookingDao;
import org.threeten.bp.format.DateTimeParseException;


import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Locale;

public class UpdateBookingActivity extends AppCompatActivity {
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

    private EditText editStartDate, editEndDate, editGuestCount, editTotalPrice;
    private Button btnUpdateBooking;
    private String bookingId;
    private String userId;
    private String propertyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.updateBooking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        editGuestCount = findViewById(R.id.editGuestCount);
        editTotalPrice = findViewById(R.id.editTotalPrice);
        btnUpdateBooking = findViewById(R.id.btnUpdateBooking);

        bookingId = getIntent().getStringExtra("bookingId");
        loadBookingData();

        btnUpdateBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBooking();
            }
        });
    }

    public void showStartDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    editStartDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    public void showEndDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    editEndDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadBookingData() {
        BookingDao.readById(bookingId)
                .addOnSuccessListener(booking -> {
                    if (booking != null) {
                        propertyId = booking.getPropertyId();
                        userId = booking.getUserId();
                        editStartDate.setText(booking.getStartDate().toString());
                        editEndDate.setText(booking.getEndDate().toString());
                        editGuestCount.setText(String.valueOf(booking.getGuestCount()));
                        editTotalPrice.setText(String.format(Locale.getDefault(), "%.2f", booking.getTotalPrice()));
                    } else {
                        Toast.makeText(this, "No booking found with the provided ID.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load booking data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Invalid date format. Please use YYYY-MM-DD.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void updateBooking() {
        LocalDate startDate = parseDate(editStartDate.getText().toString());
        LocalDate endDate = parseDate(editEndDate.getText().toString());

        if (startDate == null || endDate == null) {
            return;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice = days * Double.parseDouble(editTotalPrice.getText().toString());

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setGuestCount(Integer.parseInt(editGuestCount.getText().toString()));
        booking.setTotalPrice(totalPrice);
        booking.setUserId(userId);
        booking.setPropertyId(propertyId);

        BookingDao.update(booking)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}
