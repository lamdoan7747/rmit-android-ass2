package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit_android_ass2.main.adapter.ResultListAdapter;
import com.example.rmit_android_ass2.model.CleaningResult;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SiteDetailActivity extends AppCompatActivity {

    private static final String TAG = "ACTIVITY_SITE_DETAIL";

    private Button registerButton, unfollowButton;
    private ImageButton backButton;
    private TextView viewFollower, siteName, siteDate,
            siteHost, siteStartTime, siteEndTime, viewNoRecord;
    private ListView listResult;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private final String REGISTER = "REGISTER";
    private final String UNFOLLOW = "UNFOLLOW";

    private ArrayList<User> followers;
    private ArrayList<CleaningResult> cleaningResults;

    private ResultListAdapter resultListAdapter;
    private String cleaningSiteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();

        Intent intent = getIntent();
        if (intent.getExtras().get("cleaningSite") != null) {
            CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
            cleaningSiteId = cleaningSite.get_id();
        } else {
            cleaningSiteId = (String) intent.getExtras().get("cleaningSiteId");
        }

        renderView();
        onClickListener(cleaningSiteId, userId);


        // GET FOLLOWER OF THE SITE
        followers = new ArrayList<>();
        getFollowers(cleaningSiteId);

        // GET SITE DETAIL
        getSiteDetail(cleaningSiteId);

        // GET SITE RESULT
        cleaningResults = new ArrayList<>();
        getResults(cleaningSiteId, new OnResultCallBack() {
            @Override
            public void onCallBack(List<CleaningResult> cleaningResult) {
                if (cleaningResult.size() < 1) {
                    viewNoRecord.setVisibility(View.VISIBLE);
                }

                resultListAdapter = new ResultListAdapter(cleaningResults);
                listResult = findViewById(R.id.listResultSiteDetail);
                listResult.setAdapter(resultListAdapter);
                listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "Position: " + position);
                    }
                });
            }
        });
    }

    private void onClickListener(String cleaningSiteId, String userId) {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, REGISTER);
            }
        });

        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, UNFOLLOW);
            }
        });
    }

    private void renderView(){
        registerButton = findViewById(R.id.siteRegisterButtonSiteDetail);
        unfollowButton = findViewById(R.id.siteUnfollow);
        backButton = findViewById(R.id.backButtonToolbarSiteDetail);

        viewFollower = findViewById(R.id.viewFollowerSiteDetail);
        viewNoRecord = findViewById(R.id.viewNoRecordSiteDetail);
        siteName = findViewById(R.id.siteNameSiteDetail);
        siteDate = findViewById(R.id.siteDateSiteDetail);
        siteHost = findViewById(R.id.siteHostSiteDetail);

    }

    private void getFollowers(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                followers.add(user);
                            }
                            viewFollower.setText(String.format("%s followers", followers.size()));
                            Log.d(TAG, "Follower size => " + followers.size());

                        } else {
                            Log.d(TAG, "Error getting follower: ", task.getException());
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
                        Log.d(TAG, "Cannot get any document:" + task.getException());
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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
                        Log.d(TAG, "Site successfully delete follower!");
                        Toast.makeText(SiteDetailActivity.this, "Unfollowed!", Toast.LENGTH_SHORT).show();
                        unsubscribeToSite(cleaningSiteId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting follower", e);
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
                        Log.d(TAG, "Site successfully added new follower!");
                        Toast.makeText(SiteDetailActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                        subscribeToSite(cleaningSiteId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Error add new follower!");
                    }
                });


    }

    private void subscribeToSite(String cleaningSiteId) {
        FirebaseMessaging.getInstance().subscribeToTopic(cleaningSiteId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Site successfully registered!");
                            Toast.makeText(SiteDetailActivity.this, "Subscribed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getSiteDetail(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                        Log.d(TAG, "Site id: " + cleaningSite.get_id());

                        siteName.setText(cleaningSite.getName());

                        if (cleaningSite.getDate() != null){
                            Date dateFormat = cleaningSite.getDate().toDate();
                            String simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(dateFormat);
                            siteDate.setText(simpleDateFormat);
                        }

                        // Set host name
                        String hostId = cleaningSite.getOwner();
                        setHostName(hostId);


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

                                cleaningResults.add(cleaningResult);
                            }
                            onResultCallBack.onCallBack(cleaningResults);
                            Log.d(TAG, "Result data: " + cleaningResults.size());
                        }
                    }
                });
    }

    private void setHostName(String userId){
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            Log.d(TAG,"Host name: " + user.getFname());
                            siteHost.setText(user.getFname());
                        }
                    }
                });
    }

    private interface OnResultCallBack{
        void onCallBack(List<CleaningResult> cleaningResult);
    }
}