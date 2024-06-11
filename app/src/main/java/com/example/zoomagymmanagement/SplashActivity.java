package com.example.zoomagymmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.zoomagymmanagement.auth.LoginActivity;
import com.example.zoomagymmanagement.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Animation fromBottom, rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);
        binding.tvName.setAnimation(fromBottom);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Sleep for 3000 milliseconds (3 seconds)
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    // Handle any interruption exceptions and show a toast message
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        thread.start();

    }
}