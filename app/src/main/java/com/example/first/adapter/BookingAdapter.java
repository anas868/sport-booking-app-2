package com.example.first.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.first.R;
import com.example.first.model.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BookingAdapter extends ArrayAdapter<Booking> {

    private final Activity context;
    private final ArrayList<Booking> bookingList;

    public BookingAdapter(Activity context, ArrayList<Booking> bookingList) {
        super(context, R.layout.item_booking, bookingList);
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.item_booking, parent, false);

        TextView tvField = listViewItem.findViewById(R.id.tvField);
        TextView tvTime = listViewItem.findViewById(R.id.tvTime);
        Button btnDelete = listViewItem.findViewById(R.id.btnDelete);

        Booking booking = bookingList.get(position);

        tvField.setText(booking.getField());
        tvTime.setText(booking.getTime());

        // 🔥 زر الحذف
        btnDelete.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookings");
            ref.child(booking.getId()).removeValue();
        });

        return listViewItem;
    }
}