package com.example.szallashelyfoglalas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szallashelyfoglalas.R;
import com.example.szallashelyfoglalas.model.Booking;

import java.util.List;
import org.threeten.bp.format.DateTimeFormatter;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> bookings;
    private OnBookingListener listener;

    public interface OnBookingListener {
        void onDeleteClicked(Booking booking);
        void onUpdateClicked(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvDates.setText("Dates: " + booking.getStartDate() + " - " + booking.getEndDate());
        holder.tvTotalPrice.setText("Total Price: $" + booking.getTotalPrice());

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClicked(booking));
        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateClicked(booking));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDates, tvTotalPrice;
        Button btnDelete, btnUpdate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}

