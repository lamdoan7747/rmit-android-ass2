package com.example.rmit_android_ass2;

import android.content.Intent;
import android.os.Bundle;

import com.example.rmit_android_ass2.auth.AuthActivity;
import com.example.rmit_android_ass2.main.admin.AdminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null){
                    String userId = currentUser.getUid();
                    checkAdmin(userId);
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                }
                finish();
            }
        }, 1500);
    }

    private void checkAdmin(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If document check having a boolean field admin is true
                            if (document.getBoolean("isAdmin") != null){
                                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            }
                        }
                    }
                });
    }
}