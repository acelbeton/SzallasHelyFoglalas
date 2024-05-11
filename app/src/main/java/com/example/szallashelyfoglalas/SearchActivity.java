package com.example.szallashelyfoglalas;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.szallashelyfoglalas.model.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextLocation;
    private EditText editTextMaxPrice;
    private ListView listViewResults;
    private List<Property> properties = new ArrayList<>();

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
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        db = FirebaseFirestore.getInstance();

        editTextLocation = findViewById(R.id.editTextLocation);
        editTextMaxPrice = findViewById(R.id.editTextMaxPrice);
        listViewResults = findViewById(R.id.listViewResults);

        Button searchButton = findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(v -> performSearch());
        EdgeToEdge.enable(this);
        loadPropertiesFromFirestore();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPropertiesFromFirestore();
    }

    private void loadPropertiesFromFirestore() {
        db.collection("Property")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        properties.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Property property = document.toObject(Property.class);
                            Log.d("Property Load", "Loaded: " + property.toString());
                            properties.add(property);
                        }
                        updateListView(properties);
                    } else {
                        Toast.makeText(this, "Failed to load properties: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Firestore Error", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateListView(List<Property> properties) {
        ArrayAdapter<Property> adapter = new ArrayAdapter<Property>(this, R.layout.list_item_property, properties) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.list_item_property, parent, false);
                }
                Property property = getItem(position);

                TextView textViewAddress = view.findViewById(R.id.textViewAddress);
                TextView textViewPrice = view.findViewById(R.id.textViewPrice);
                TextView textViewGuests = view.findViewById(R.id.textViewGuests);
                TextView textViewType = view.findViewById(R.id.textViewType);

                textViewAddress.setText(property.getAddress() != null ? property.getAddress().get("city") + ", " + property.getAddress().get("street") : "No address");
                textViewPrice.setText(String.format(Locale.US, "$%.2f per night", property.getPricePerNight()));
                textViewGuests.setText(String.format(Locale.US, "Max guests: %d", property.getMaxGuests()));
                textViewType.setText("Type: " + property.getType());

                Button viewButton = view.findViewById(R.id.buttonView);
                viewButton.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, PropertyViewActivity.class);
                    intent.putExtra("propertyId", Objects.requireNonNull(getItem(position)).getPropertyId());
                    startActivity(intent);
                });

                return view;
            }
        };
        listViewResults.setAdapter(adapter);
    }



    private void performSearch() {
        String location = editTextLocation.getText().toString();
        String maxPriceString = editTextMaxPrice.getText().toString();
        double maxPrice;

        try {
            maxPrice = Double.parseDouble(maxPriceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_LONG).show();
            return;
        }

        List<Property> filteredProperties = properties.stream()
                .filter(property -> property.getAddress() != null && property.getAddress().get("city") != null && property.getAddress().get("city").equalsIgnoreCase(location) && property.getPricePerNight() <= maxPrice)

                .collect(Collectors.toList());

        updateListView(filteredProperties);
    }
}
