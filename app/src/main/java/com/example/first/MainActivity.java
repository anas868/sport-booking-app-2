package com.example.first;


import android.content.Intent;
import com.example.first.ui.BookingActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnField1).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("field", "ملعب كرة قدم");
            startActivity(intent);
        });

        findViewById(R.id.btnField2).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("field", "ملعب كرة سلة");
            startActivity(intent);
        });
    }
}