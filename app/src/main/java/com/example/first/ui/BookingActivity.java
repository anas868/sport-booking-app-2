package com.example.first.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("bookings");

        TextView tvField = findViewById(R.id.tvField);
        EditText etDay = findViewById(R.id.etDay);
        EditText etDate = findViewById(R.id.etDate);
        EditText etTime = findViewById(R.id.etTime);
        EditText etDuration = findViewById(R.id.etDuration);

        String fieldName = getIntent().getStringExtra("field");
        tvField.setText(fieldName);

        findViewById(R.id.btnBook).setOnClickListener(v -> {
            String day = etDay.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            if (day.isEmpty() || date.isEmpty() || time.isEmpty() || duration.isEmpty()) {
                Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            int newStart = parseTimeToMinutes(time);
            if (newStart == -1) {
                Toast.makeText(this, "اكتب الساعة بهذا الشكل: 18:30", Toast.LENGTH_LONG).show();
                return;
            }

            int newDuration = parseDurationToMinutes(duration);
            int newEnd = newStart + newDuration;

            String ownerEmail = "";
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                ownerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }

            String finalOwnerEmail = ownerEmail;

            database.get().addOnSuccessListener(snapshot -> {
                boolean isBooked = false;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking existingBooking = dataSnapshot.getValue(Booking.class);

                    if (existingBooking != null
                            && existingBooking.getField() != null
                            && existingBooking.getDate() != null
                            && existingBooking.getTime() != null
                            && existingBooking.getDuration() != null
                            && existingBooking.getField().equals(fieldName)
                            && existingBooking.getDate().equals(date)) {

                        int existingStart = parseTimeToMinutes(existingBooking.getTime());
                        int existingDuration = parseDurationToMinutes(existingBooking.getDuration());

                        if (existingStart == -1) {
                            continue;
                        }

                        int existingEnd = existingStart + existingDuration;

                        // يوجد تداخل فقط إذا بدأ الجديد قبل نهاية القديم
                        // وانتهى الجديد بعد بداية القديم
                        if (newStart < existingEnd && newEnd > existingStart) {
                            isBooked = true;
                            break;
                        }
                    }
                }

                if (isBooked) {
                    Toast.makeText(this, "هذا الوقت محجوز، يرجى اختيار وقت آخر", Toast.LENGTH_LONG).show();
                } else {
                    String id = database.push().getKey();
                    Booking booking = new Booking(id, fieldName, day, date, time, duration, finalOwnerEmail);

                    database.child(id).setValue(booking)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "تم الحجز بنجاح", Toast.LENGTH_SHORT).show();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "booking_channel")
                                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                                        .setContentTitle("تم الحجز")
                                        .setContentText("تم حجز " + fieldName + " بتاريخ " + date + " الساعة " + time)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat.from(this).notify(1, builder.build());

                                scheduleReminder(fieldName, date, time);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "فشل: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "فشل في التحقق من الحجوزات", Toast.LENGTH_SHORT).show()
            );
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

    private int parseDurationToMinutes(String duration) {
        duration = duration.trim();

        if (duration.equals("1") || duration.equals("1 ساعة") || duration.equals("ساعة")) {
            return 60;
        }

        if (duration.equals("2") || duration.equals("2 ساعة") || duration.equals("ساعتين")) {
            return 120;
        }

        if (duration.equals("3") || duration.equals("3 ساعات")) {
            return 180;
        }

        try {
            return Integer.parseInt(duration) * 60;
        } catch (Exception e) {
            return 60; // افتراضي ساعة
        }
    }

    private void scheduleReminder(String field, String date, String time) {
        try {
            Intent intent = new Intent(this, BookingReminderReceiver.class);
            intent.putExtra("field", field);
            intent.putExtra("date", date);
            intent.putExtra("time", time);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager == null) {
                Toast.makeText(this, "فشل الوصول إلى AlarmManager", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();

            String[] dateParts = date.split("/");
            String[] timeParts = time.split(":");

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // التذكير قبل 30 دقيقة
            calendar.add(Calendar.MINUTE, -30);

            long triggerTime = calendar.getTimeInMillis();

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );

            Toast.makeText(this, "تم ضبط التذكير قبل الحجز بـ 30 دقيقة", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "فشل إعداد التذكير", Toast.LENGTH_SHORT).show();
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
