package com.example.first.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        TextView tvLogin = findViewById(R.id.tvLogin);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();

            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "املأ جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            if (p.length() < 6) {
                Toast.makeText(this, "كلمة المرور يجب أن تكون 6 أحرف على الأقل", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(e, p)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "فشل إنشاء الحساب: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}