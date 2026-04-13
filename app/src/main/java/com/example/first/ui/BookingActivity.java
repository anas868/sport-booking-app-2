package com.example.first.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.first.R;
import com.example.first.model.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100);
            }
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("bookings");

        TextView tvField = findViewById(R.id.tvField);
        EditText etTime = findViewById(R.id.etTime);

        String fieldName = getIntent().getStringExtra("field");
        tvField.setText(fieldName);

        findViewById(R.id.btnBook).setOnClickListener(v -> {
            String time = etTime.getText().toString().trim();

            if (time.isEmpty()) {
                Toast.makeText(this, "ادخل الوقت", Toast.LENGTH_SHORT).show();
                return;
            }

            int newTimeMinutes = parseTimeToMinutes(time);
            if (newTimeMinutes == -1) {
                Toast.makeText(this, "اكتب الوقت بهذا الشكل: 5:00 أو 18:30", Toast.LENGTH_LONG).show();
                return;
            }

            database.get().addOnSuccessListener(snapshot -> {
                boolean isBooked = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking existingBooking = dataSnapshot.getValue(Booking.class);

                    if (existingBooking != null
                            && existingBooking.getField() != null
                            && existingBooking.getTime() != null
                            && existingBooking.getField().equals(fieldName)) {

                        int existingTimeMinutes = parseTimeToMinutes(existingBooking.getTime());

                        if (existingTimeMinutes != -1) {
                            int difference = Math.abs(newTimeMinutes - existingTimeMinutes);

                            // يمنع الحجز إذا كان الفرق أقل من 90 دقيقة
                            if (difference < 90) {
                                isBooked = true;
                                break;
                            }
                        }
                    }
                }

                if (isBooked) {
                    Toast.makeText(this, "هذا الوقت محجوز، يرجى اختيار وقت آخر", Toast.LENGTH_LONG).show();
                } else {
                    String id = database.push().getKey();
                    Booking booking = new Booking(id, fieldName, time);

                    database.child(id).setValue(booking)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "تم حفظ الحجز", Toast.LENGTH_SHORT).show();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                                        .setContentTitle("تم الحجز")
                                        .setContentText("تم حجز " + fieldName + " الساعة " + time)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat.from(this).notify(1, builder.build());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "فشل: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "فشل في التحقق من الحجوزات", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private int parseTimeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            if (parts.length != 2) return -1;

            int hour = Integer.parseInt(parts[0].trim());
            int minute = Integer.parseInt(parts[1].trim());

            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) return -1;

            return hour * 60 + minute;
        } catch (Exception e) {
            return -1;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "booking_channel",
                    "Booking Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for booking notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}