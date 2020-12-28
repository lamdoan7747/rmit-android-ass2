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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class MySiteDetailFragment extends Fragment {

    private static final String TAG = "MY_SITE_DETAIL_FRAGMENT";
    private Button insertButton;
    private ImageButton backButton, buttonSiteOption;
    private TextView viewFollower, siteName, siteDate,
            siteStartTime, siteEndTime, viewNoRecord;
    private ListView listResult;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private ArrayList<User> followerList;
    private ArrayList<CleaningResult> cleaningResults;
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

        Intent intent = requireActivity().getIntent();
        CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
        String cleaningSiteId = cleaningSite.get_id();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        renderView(requireView());
        onCLickListener(cleaningSiteId);

        // Get site detail
        getSiteDetail(cleaningSiteId);

        // Get number of followers
        followerList = new ArrayList<>();
        getFollowers(cleaningSiteId);

        // GET SITE RESULT
        cleaningResults = new ArrayList<>();
        getResults(cleaningSiteId, new OnResultCallBack() {
            @Override
            public void onCallBack(List<CleaningResult> cleaningResult) {
                if (cleaningResult.size() < 1){
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteInsertDataFragment());
            }
        });

        buttonSiteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteOptionFragment());
            }
        });
    }

    private void renderView(View view) {
        insertButton = view.findViewById(R.id.insertSiteMySiteDetail);
        buttonSiteOption = view.findViewById(R.id.optionSiteMySiteDetail);
        backButton = view.findViewById(R.id.backButtonToolbarMySiteDetail);

        siteName = view.findViewById(R.id.siteNameMySiteDetail);
        siteDate = view.findViewById(R.id.siteDateMySiteDetail);
        viewFollower = view.findViewById(R.id.viewFollowerMySiteDetail);
        viewNoRecord = view.findViewById(R.id.viewNoRecordMySiteDetail);

    }

    private void backToPrevious() {
        requireActivity().finish();
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

    @SuppressLint("SimpleDateFormat")
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

                        if (cleaningSite.getDate() != null){
                            Date dateFormat = cleaningSite.getDate().toDate();
                            String simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(dateFormat);
                            siteDate.setText(simpleDateFormat);
                        }
                        siteName.setText(cleaningSite.getName());


                    } else {
                        Log.d("CLEANING SITE", "Cannot get any document:" + task.getException());
                    }
                } else {
                    Log.d("ON FAILURE", "get failed with ", task.getException());
                }
            }
        });
    }

    private interface OnResultCallBack{
        void onCallBack(List<CleaningResult> cleaningResult);
    }

    private void loadFragment(String cleaningSiteId,Fragment fragment) {
        // Set message for new fragment
        Bundle bundle = new Bundle();
        bundle.putString("cleaningSiteId", cleaningSiteId);
        fragment.setArguments(bundle);

        // Start transaction with new fragment
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.editFrameContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}