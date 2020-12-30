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

    // Google Firebase declaration
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        /*
        *   Start a new thread to delay when open the app
        *   delay 1,5s
        *   when delay, call checkAdmin function to start the right Activity
        *   if cannot get any user login, return to the Auth Activity to login
        */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    checkAdmin(userId);
                } else {
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                }
                finish();
            }
        }, 1500);
    }

    /**
     * Function to check if user is admin
     * if Success, start admin flow activity
     * if Failure, start user flow activity
     *
     * @param userId: get userId to query in Firebase
     */
    private void checkAdmin(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If document check having a boolean field admin is not null
                            if (document.getBoolean("isAdmin") != null) {
                                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                            }
                        }
                    }
                });
    }
}