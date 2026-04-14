package com.example.first;

import android.content.Intent;
import android.os.Bundle;
import com.example.first.ui.BookingActivity;
import com.example.first.ui.FieldsInfoActivity;
import com.example.first.ui.BookingsActivity;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnFieldInfo).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FieldsInfoActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnField1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            intent.putExtra("field", "ملعب كرة قدم");
            startActivity(intent);
        });

        findViewById(R.id.btnField2).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            intent.putExtra("field", "ملعب كرة سلة");
            startActivity(intent);
        });
        findViewById(R.id.btnViewBookings).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingsActivity.class);
            startActivity(intent);
        });
    }
}