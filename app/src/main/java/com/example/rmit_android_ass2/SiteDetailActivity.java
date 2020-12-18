package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiteDetailActivity extends AppCompatActivity {

    private Button register, unfollow;
    private TextView follower;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final String REGISTER = "REGISTER";
    private final String UNFOLLOW = "UNFOLLOW";

    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        register = findViewById(R.id.siteRegister);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        register = findViewById(R.id.siteRegister);
        unfollow = findViewById(R.id.siteUnfollow);

        follower = findViewById(R.id.viewFollower);

        userList = new ArrayList<>();

        Intent intent = getIntent();
        String cleaningSiteId = (String) intent.getExtras().get("siteId");

        Log.d("ACTIVITY_SITE_DETAIL", "Document data: " + cleaningSiteId);
        Log.d("ACTIVITY_SITE_DETAIL", "Document data: " + userId);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, REGISTER);
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, UNFOLLOW);
            }
        });

        getFolowers(cleaningSiteId);

        getSites(cleaningSiteId, new FirestoreCallBack() {
            @Override
            public void onCallBack(CleaningSite cleaningSite) {
                Log.d("CLEANING SITE", "Document data: " + cleaningSite.get_id());
            }
        });



    }

    private void getFolowers(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FOLLOWER SIZE", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            follower.setText(String.format("%s followers", userList.size()));
                        } else {
                            Log.d("FOLLOWER SIZE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void actionSite(String userId,String cleaningSiteId, String action) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        switch (action){
                            case REGISTER:
                                registerSite(cleaningSiteId, user);
                                break;
                            case UNFOLLOW:
                                unfollowSite(cleaningSiteId, user);
                                break;
                        }
                    } else {
                        Log.d("CLEANING SITE", "Cannot get any document:" + task.getException());
                    }
                } else {
                    Log.d("ON FAILURE", "get failed with ", task.getException());
                }
            }
        });
        }


    private void unfollowSite(String cleaningSiteId, User user) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.collection("followers")
                .document(user.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UNFOLLOW", "DocumentSnapshot successfully deleted!");
                        Toast.makeText(SiteDetailActivity.this, "Unfollowed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UNFOLLOW", "Error deleting document", e);
                    }
                });
    }

    private void registerSite(String cleaningSiteId, User user) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        User newUser = new User(user.getFname(),user.getEmail());

        docRef.collection("followers")
                .document(user.getId())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REGISTER", "DocumentSnapshot successfully deleted!");
                        Toast.makeText(SiteDetailActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("REGISTER","Fail!");
                    }
                });


    }

    private void getSites(String cleaningSiteId, FirestoreCallBack firestoreCallBack) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                        Log.d("CLEANING SITE", "Document data: " + cleaningSite.get_id());

                    } else {
                        Log.d("CLEANING SITE", "Cannot get any document:" + task.getException());
                    }
                } else {
                    Log.d("ON FAILURE", "get failed with ", task.getException());
                }
            }
        });
    }

    private interface FirestoreCallBack{
        void onCallBack(CleaningSite cleaningSite);
    }
}