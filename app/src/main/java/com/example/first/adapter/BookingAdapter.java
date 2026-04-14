package com.example.first.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.first.R;
import com.example.first.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
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
        TextView tvDay = listViewItem.findViewById(R.id.tvDay);
        TextView tvDate = listViewItem.findViewById(R.id.tvDate);
        TextView tvTime = listViewItem.findViewById(R.id.tvTime);
        TextView tvDuration = listViewItem.findViewById(R.id.tvDuration);
        Button btnDelete = listViewItem.findViewById(R.id.btnDelete);

        Booking booking = bookingList.get(position);

        tvField.setText(booking.getField());
        tvDay.setText("اليوم: " + booking.getDay());
        tvDate.setText("التاريخ: " + booking.getDate());
        tvTime.setText("الساعة: " + booking.getTime());
        tvDuration.setText("المدة: " + booking.getDuration());

        String currentUserEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        if (booking.getOwnerEmail() != null && booking.getOwnerEmail().equals(currentUserEmail)) {
            btnDelete.setVisibility(View.VISIBLE);

            btnDelete.setOnClickListener(v -> {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookings");
                ref.child(booking.getId()).removeValue()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(context, "تم حذف الحجز", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "فشل الحذف", Toast.LENGTH_SHORT).show());
            });

        } else {
            btnDelete.setVisibility(View.GONE);
        }

        return listViewItem;
    }
}