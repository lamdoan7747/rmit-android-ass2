package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button lLoginButton;
    private EditText lUsername, lPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lUsername = findViewById(R.id.lUsername);
        lPassword = findViewById(R.id.lPassword);


        lLoginButton = findViewById(R.id.lLogin);
        lLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = lUsername.getText().toString();
                String password = lPassword.getText().toString();

                submitAccount(username,password);
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void submitAccount(String username, String password) {
        db = FirebaseFirestore.getInstance();
        Map<String,String> user = new HashMap<>();
        user.put("username",username);
        user.put("password",password);


        // Add a new account to FireStore
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this,"Add success: " + documentReference.getId(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Failure: " + e.toString(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}