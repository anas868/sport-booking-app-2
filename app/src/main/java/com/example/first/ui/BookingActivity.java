package com.example.first.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first.R;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

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

            Toast.makeText(this, "تم حجز " + fieldName + " في " + time, Toast.LENGTH_LONG).show();

            String id = database.push().getKey();
            database.child(id).setValue(fieldName + " - " + time);

            Toast.makeText(this, "تم حفظ الحجز", Toast.LENGTH_SHORT).show();
        });
    }
}