package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.adapter.ResultListAdapter;
import com.example.rmit_android_ass2.model.CleaningResult;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MySiteDetailFragment extends Fragment {
    // Constant declaration
    private static final String TAG = "MY_SITE_DETAIL_FRAGMENT";

    // Android view declaration
    private Button insertButton;
    private ImageButton backButton, buttonSiteOption;
    private TextView viewFollower, siteName, siteDate,
            siteTime, siteAddress, viewNoRecord;
    private ListView listResult;

    // Google Firebase declaration
    private FirebaseFirestore db;

    // Array list declaration
    private ArrayList<User> followerList;
    private ArrayList<CleaningResult> cleaningResults;

    // Adapter
    private ResultListAdapter resultListAdapter;


    public MySiteDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_site_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();

        // Get intent data from the previous activity
        Intent intent = requireActivity().getIntent();
        CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
        String cleaningSiteId = cleaningSite.getId();

        // Initiate view for the activity
        renderView(requireView());

        // Set all events on touchable with siteId variable
        onCLickListener(cleaningSiteId);

        // Get all site details to display
        getSiteDetail(cleaningSiteId);

        // Get follower size to display on textView
        followerList = new ArrayList<>();
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
                listResult = requireView().findViewById(R.id.listResultMySiteDetail);
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

    private void onCLickListener(String cleaningSiteId) {
        // Back button to return the previous activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().finish();
                requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Insert button to load new fragment
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteInsertDataFragment());
            }
        });

        // Option button to load new fragment
        buttonSiteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteOptionFragment());
            }
        });
    }

    /**
     * Function to initiate all the view with the right id
     *
     * @param view view get from UI
     */
    private void renderView(View view) {
        insertButton = view.findViewById(R.id.insertSiteMySiteDetail);
        buttonSiteOption = view.findViewById(R.id.optionSiteMySiteDetail);
        backButton = view.findViewById(R.id.backButtonToolbarMySiteDetail);

        siteName = view.findViewById(R.id.siteNameMySiteDetail);
        siteDate = view.findViewById(R.id.siteDateMySiteDetail);
        siteTime = view.findViewById(R.id.siteTimeMySiteDetail);
        siteAddress = view.findViewById(R.id.siteAddressMySiteDetail);

        viewFollower = view.findViewById(R.id.viewFollowerMySiteDetail);
        viewNoRecord = view.findViewById(R.id.viewNoRecordMySiteDetail);

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
                                followerList.add(user);
                            }
                            viewFollower.setText(String.format("%s followers", followerList.size()));
                            Log.d(TAG, "Size follower => " + followerList.size());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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
     * if Success, set textView for siteName, siteDate
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
    @SuppressLint("SimpleDateFormat")
    private void getSiteDetail(String cleaningSiteId) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                Log.d(TAG, "Site id: " + cleaningSite.getId());

                                if (cleaningSite.getDate() != null) {
                                    Date dateFormat = cleaningSite.getDate().toDate();
                                    String simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(dateFormat);
                                    siteDate.setText(simpleDateFormat);
                                }
                                siteName.setText(cleaningSite.getName());
                                siteAddress.setText(cleaningSite.getAddress());
                                siteTime.setText(String.format("%s : %s", cleaningSite.getStartTime(), cleaningSite.getEndTime()));


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
     * Start a new transaction to add fragment
     * -> Save message to transfer to other fragment
     * -> Setup transition slide up, slide down
     *
     * @param cleaningSiteId: cleaningSiteId to transfer to other fragment
     * @param fragment:       init fragment to load
     */
    private void loadFragment(String cleaningSiteId, Fragment fragment) {
        // Set message for new fragment
        Bundle bundle = new Bundle();
        bundle.putString("cleaningSiteId", cleaningSiteId);
        fragment.setArguments(bundle);

        // Start transaction with new fragment
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.editFrameContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of cleaningResult from Firestore.
     */
    private interface OnResultCallBack {
        void onCallBack(List<CleaningResult> cleaningResult);
    }
}