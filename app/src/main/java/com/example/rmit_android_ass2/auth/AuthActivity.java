package com.example.rmit_android_ass2.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rmit_android_ass2.HomeActivity;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        loadFragment(new LoginFragment());

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }
    }

    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.authContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}