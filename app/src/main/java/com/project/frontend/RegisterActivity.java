package com.project.frontend;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.project.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button accountCreated = findViewById(R.id.button3);

        accountCreated.setOnClickListener(view -> { // for future this is a lambda expression
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }
}
