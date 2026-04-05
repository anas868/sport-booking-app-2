package com.example.first.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.first.R;
import com.example.first.model.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        createNotificationChannel(); // إذا موجود عندك

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100);
            }
        }
        createNotificationChannel();

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

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        notificationManager.notify(1, builder.build());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "فشل: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
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