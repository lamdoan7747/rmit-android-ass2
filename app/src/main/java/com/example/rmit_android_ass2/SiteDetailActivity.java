package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit_android_ass2.model.CleaningResult;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class SiteDetailActivity extends AppCompatActivity {

    private Button register, unfollow;
    private ImageButton backButton;
    private TextView follower;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final String REGISTER = "REGISTER";
    private final String UNFOLLOW = "UNFOLLOW";

    private ArrayList<User> userList;
    private ArrayList<CleaningResult> cleaningResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        register = findViewById(R.id.siteRegister);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        register = (Button) findViewById(R.id.siteRegister);
        unfollow = findViewById(R.id.siteUnfollow);
        follower = findViewById(R.id.viewFollower);
        backButton = findViewById(R.id.backButtonToolbarSiteDetail);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SiteDetailActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        userList = new ArrayList<>();
        cleaningResults = new ArrayList<>();

        Intent intent = getIntent();
        CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
        String cleaningSiteId = cleaningSite.get_id();

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

        getSites(cleaningSiteId, new OnSiteCallBack() {
            @Override
            public void onCallBack(CleaningSite cleaningSite) {
                Log.d("CLEANING SITE", "Document data: " + cleaningSite.get_id());
            }
        });

        getResults(cleaningSiteId, new OnResultCallBack() {
            @Override
            public void onCallBack(List<CleaningResult> cleaningResult) {

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
                        unsubscribeToSite(cleaningSiteId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UNFOLLOW", "Error deleting document", e);
                    }
                });
    }

    private void unsubscribeToSite(String cleaningSiteId) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(cleaningSiteId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SiteDetailActivity.this, "Subscribed!", Toast.LENGTH_SHORT).show();
                        }
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
                        subscribeToSite(cleaningSiteId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("REGISTER","Fail!");
                    }
                });


    }

    private void subscribeToSite(String cleaningSiteId) {
        FirebaseMessaging.getInstance().subscribeToTopic(cleaningSiteId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SiteDetailActivity.this, "Subscribed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getSites(String cleaningSiteId, OnSiteCallBack onSiteCallBack) {
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

    private void getResults(String cleaningSiteId, OnResultCallBack onResultCallBack) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.collection("results")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()){
                                CleaningResult cleaningResult = document.toObject(CleaningResult.class);
                                Log.d("CLEANING SITE", "Document data: " + cleaningResult.getId());

                                cleaningResults.add(cleaningResult);
                            }
                            onResultCallBack.onCallBack(cleaningResults);
                        }
                    }
                });
    }

    private interface OnSiteCallBack{
        void onCallBack(CleaningSite cleaningSite);
    }

    private interface OnResultCallBack{
        void onCallBack(List<CleaningResult> cleaningResult);
    }
}