package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SiteDetailActivity extends AppCompatActivity {
    // Constant declaration
    private static final String TAG = "ACTIVITY_SITE_DETAIL";
    private final String REGISTER = "REGISTER";
    private final String UNFOLLOW = "UNFOLLOW";

    // Android View declaration
    private Button registerButton, unfollowButton;
    private ImageButton backButton;
    private TextView viewFollower, siteName, siteDate,
            siteHost, siteStartTime, siteEndTime, viewNoRecord;
    private ListView listResult;

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Array list declaration
    private ArrayList<User> followers;
    private ArrayList<CleaningResult> cleaningResults;

    // Adapter declaration
    private ResultListAdapter resultListAdapter;

    // Utils variable declaration
    private String cleaningSiteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get user id from FirebaseUser
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // Get intent data from the previous activity
        Intent intent = getIntent();
        if (intent.getExtras().get("cleaningSite") != null) {
            CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
            cleaningSiteId = cleaningSite.getId();
        } else {
            cleaningSiteId = (String) intent.getExtras().get("cleaningSiteId");
        }

        // Initiate view for the activity
        renderView();

        // Set all events on touchable with siteId and userId variable
        onClickListener(cleaningSiteId, userId);

        // Get all site details to display
        getSiteDetail(cleaningSiteId);

        // Get follower size to display on textView
        followers = new ArrayList<>();
        getFollowers(cleaningSiteId);

        // Get all site results to display
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
        // Back button to return to the previous activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Register button start triggered actionSite() with REGISTER action
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, REGISTER);
            }
        });

        // Unfollow button start triggered actionSite() with UNFOLLOW action
        unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSite(userId, cleaningSiteId, UNFOLLOW);
            }
        });
    }

    private void renderView() {
        registerButton = findViewById(R.id.siteRegisterButtonSiteDetail);
        unfollowButton = findViewById(R.id.siteUnfollow);
        backButton = findViewById(R.id.backButtonToolbarSiteDetail);

        viewFollower = findViewById(R.id.viewFollowerSiteDetail);
        viewNoRecord = findViewById(R.id.viewNoRecordSiteDetail);
        siteName = findViewById(R.id.siteNameSiteDetail);
        siteDate = findViewById(R.id.siteDateSiteDetail);
        siteHost = findViewById(R.id.siteHostSiteDetail);

    }

    /**
     * Function to get all followers of specific site to
     * return total of followers
     *
     * @param cleaningSiteId cleaning siteId to query
     */
    private void getFollowers(String cleaningSiteId) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("followers")
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

    /**
     * Function to query to the user database to trigger registerSite or unfollowSite function
     * with the siteId and User object
     *
     * @param cleaningSiteId cleaning siteId to pass to new function
     * @param userId         userId to get document
     * @param action         action from UI
     */
    private void actionSite(String userId, String cleaningSiteId, String action) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                switch (action) {
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

    /**
     * Function to add new User to "follower" sub-collection from cleaningSites collection
     * set document id with User id
     * if Success,
     * -> trigger subscribeToSite() function from Cloud Messaging Firebase
     * -> able to receive notification from the topic named "cleaningSiteId"
     * -> trigger updateSite() to update site detail with REGISTER action
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     * @param user           User object to get name & email
     */
    private void registerSite(String cleaningSiteId, @NonNull User user) {
        // Get document query by cleaningSiteId
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);

        // Declare an object with name and email only
        User newUser = new User(user.getName(), user.getEmail());

        docRef.collection("followers")
                .document(user.getId())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Site successfully added new follower!");
                        Toast.makeText(SiteDetailActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                        subscribeToSite(cleaningSiteId);
                        updateSite(cleaningSiteId, REGISTER);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error add new follower!");
                    }
                });
    }

    /**
     * Function to subscribe to new topic named "cleaningSiteId"
     * if Success, display toast message
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
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

    /**
     * Function to delete new User from "follower" sub-collection from cleaningSites collection
     * get document id with User id
     * if Success,
     * -> trigger unsubscribeToSite() function from Cloud Messaging Firebase
     * -> able to refuse notification from the topic named "cleaningSiteId"
     * -> trigger updateSite() to update site detail with UNFOLLOW action
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     * @param user           User object to get name & email
     */
    private void unfollowSite(String cleaningSiteId, @NonNull User user) {
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
                        updateSite(cleaningSiteId, UNFOLLOW);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting follower", e);
                    }
                });
    }

    /**
     * Function to unsubscribe to registered topic named "cleaningSiteId"
     * if Success, display toast message
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
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

    /**
     * Function to update follower in "cleaningSites" collection
     * if REGISTER, update follower add 1
     * if UNFOLLOW, update follower minus 1
     * on Success, set new follower for textView
     *
     * @param cleaningSiteId cleaning siteId to get document
     * @param action         action from UI
     */
    private void updateSite(String cleaningSiteId, String action) {
        db.runTransaction(new Transaction.Function<Long>() {
            @Nullable
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
                DocumentSnapshot document = transaction.get(docRef);
                switch (action) {
                    case REGISTER:
                        long addFollower = document.getLong("follower") + 1;
                        transaction.update(docRef, "follower", addFollower);
                        return addFollower;
                    case UNFOLLOW:
                        long removeFollower = document.getLong("follower") - 1;
                        transaction.update(docRef, "follower", removeFollower);
                        return removeFollower;
                    default:
                        throw new IllegalStateException("Unexpected value: " + action);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                viewFollower.setText(String.format("%s followers", result));
            }
        });
    }

    /**
     * Function to get all site details to display to UI
     * if Success, set textView for siteName, siteDate, finally trigger setHostName function
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
    private void getSiteDetail(String cleaningSiteId) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    @SuppressLint("SimpleDateFormat")
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                Log.d(TAG, "Site id: " + cleaningSite.getId());

                                siteName.setText(cleaningSite.getName());

                                if (cleaningSite.getDate() != null) {
                                    Date dateFormat = cleaningSite.getDate().toDate();
                                    String simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(dateFormat);
                                    siteDate.setText(simpleDateFormat);
                                }

                                // Set host name
                                String hostId = cleaningSite.getOwner();
                                setHostName(hostId);


                            } else {
                                Log.d(TAG, "Cannot get any document:" + task.getException());
                            }
                        }
                    }
                });
    }

    /**
     * Function to get all results to add to listView
     * if Success, add all CleaningResult object to a list
     * -> assign function onResultCallBack
     * if Failure, display Log debug
     *
     * @param cleaningSiteId   cleaning siteId to get document
     * @param onResultCallBack callBack interface to get list of results
     */
    private void getResults(String cleaningSiteId, OnResultCallBack onResultCallBack) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("results")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CleaningResult cleaningResult = document.toObject(CleaningResult.class);
                                cleaningResults.add(cleaningResult);
                            }
                            onResultCallBack.onCallBack(cleaningResults);
                            Log.d(TAG, "Result data: " + cleaningResults.size());
                        }
                    }
                });
    }

    /**
     * Function to get all site details to display to UI
     * if Success, set textView for siteHost
     * if Failure, display Log debug
     *
     * @param userId userId to get document
     */
    private void setHostName(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            Log.d(TAG, "Host name: " + user.getName());
                            siteHost.setText(user.getName());
                        } else {
                            Log.d(TAG, "Cannot get any document:" + task.getException());
                        }
                    }
                });
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of cleaningResult from Firestore.
     */
    private interface OnResultCallBack {
        void onCallBack(List<CleaningResult> cleaningResult);
    }
}