package com.example.rmit_android_ass2;

import android.content.Intent;
import android.os.Bundle;

import com.example.rmit_android_ass2.auth.AuthActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                }
                finish();
            }
        }, 1500);
    }
}