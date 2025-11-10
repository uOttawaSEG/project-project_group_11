package com.project.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.project.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // handler to schedule work done on main ui thread
            public void run() { // postDelayed to wait some ms, Runnable is the code block to run later, inside run defines the code to run
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); // start login screen after initial splash screen
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // fade animation for splash screen to login
                finish(); // close initial splash screen (MainActivity) so you can't go back to it
            }
        }, 3000); // 3 second wait time
    }
}