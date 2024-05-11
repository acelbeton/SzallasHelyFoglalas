package com.example.szallashelyfoglalas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szallashelyfoglalas.R;
import com.example.szallashelyfoglalas.model.Property;

import java.util.List;

public class MainPropertyAdapter extends RecyclerView.Adapter<MainPropertyAdapter.PropertyViewHolder> {

    private Context context;
    private List<Property> properties;

    public MainPropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.textViewPropertyName.setText(property.getType() + " in " + property.getAddress().get("city"));
        holder.textViewPropertyType.setText("Type: " + property.getType());
        holder.textViewPrice.setText("$" + property.getPricePerNight() + " per night");
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPropertyName;
        TextView textViewPropertyType;
        TextView textViewPrice;

        public PropertyViewHolder(View itemView) {
            super(itemView);
            textViewPropertyName = itemView.findViewById(R.id.textViewPropertyName);
            textViewPropertyType = itemView.findViewById(R.id.textViewPropertyType);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}
