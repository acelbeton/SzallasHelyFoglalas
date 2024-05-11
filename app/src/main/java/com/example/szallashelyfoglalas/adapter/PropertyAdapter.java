package com.example.szallashelyfoglalas.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.szallashelyfoglalas.R;
import com.example.szallashelyfoglalas.model.Booking;
import com.example.szallashelyfoglalas.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PropertyAdapter extends ArrayAdapter<Property> {


    public PropertyAdapter(Context context, List<Property> properties) {
        super(context, 0, properties);
    }


}

