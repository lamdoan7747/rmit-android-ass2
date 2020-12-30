package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.adapter.FollowerListAdapter;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("LongLogTag")
public class MySiteFollowerFragment extends Fragment {
    // Constant declaration
    private final String TAG = "MY_SITE_FOLLOWER_FRAGMENT";
    private final String SITE_ID = "cleaningSiteId";

    // Google Firebase declaration
    private FirebaseFirestore db;

    // Android view declaration
    private ListView listFollower;
    private ImageButton backButton;
    private TextView viewNoRecord;

    // Array list declaration
    private ArrayList<User> followerList;

    // Adapter
    private FollowerListAdapter followerListAdapter;

    // Utils variable declaration
    private String cleaningSiteId;


    public MySiteFollowerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get message cleaningSiteId from previous fragment
        if (getArguments() != null) {
            cleaningSiteId = getArguments().getString(SITE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_site_follower, container, false);
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

        // Back button to return the previous fragment
        backButton = requireView().findViewById(R.id.backButtonToolbarMySiteFollower);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Get follower size to display on listView
        followerList = new ArrayList<>();
        getFollowers(cleaningSiteId, new FollowerCallBack() {
            @Override
            public void onCallBack(List<User> followers) {
                viewNoRecord = requireView().findViewById(R.id.viewNoRecordMySiteFollower);
                if (followers.size() < 1) {
                    viewNoRecord.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "Follower list => " + followerList.size());
                followerListAdapter = new FollowerListAdapter(followerList);
                listFollower = requireView().findViewById(R.id.listFollower);
                listFollower.setAdapter(followerListAdapter);
                listFollower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "Item click!");
                        Toast.makeText(getActivity(), "DISPLAY FOLLOWER PROFILE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Function to get all followers of specific site
     * if Success, add all Follower object to a list
     * if Failure, display Log debug
     *
     * @param cleaningSiteId   cleaning siteId to get document
     * @param followerCallBack callBack interface to get list of followers
     */
    private void getFollowers(String cleaningSiteId, FollowerCallBack followerCallBack) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User follower = document.toObject(User.class);
                                followerList.add(follower);
                            }
                            followerCallBack.onCallBack(followerList);
                        } else {
                            Log.d(TAG, "Error getting documents: " + task.getException());
                        }
                    }
                });
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of followers from Firestore.
     */
    private interface FollowerCallBack {
        void onCallBack(List<User> followers);
    }
}