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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MySiteFollowerFragment extends Fragment {

    private static final String TAG = "MY_SITE_FOLLOWER_FRAGMENT";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SITE_ID = "cleaningSiteId";

    private String cleaningSiteId;

    private ArrayList<User> followerList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ListView listFollower;
    private FollowerListAdapter followerListAdapter;

    private ImageButton backButton;

    public MySiteFollowerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        backButton = requireView().findViewById(R.id.backButtonToolbarMySiteFollower);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });

        // Init follower list
        followerList = new ArrayList<>();
        // Display follower list view
        displayFollowerList(cleaningSiteId);
    }


    private void displayFollowerList(String cleaningSiteId) {
        getFollowers(cleaningSiteId, new FollowerCallBack() {
            @Override
            public void onCallBack(List<User> followers) {
                followerListAdapter = new FollowerListAdapter(followerList);
                listFollower = requireView().findViewById(R.id.listFollower);
                listFollower.setAdapter(followerListAdapter);
                listFollower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Toast.makeText(getActivity(),"DISPLAY FOLLOWER PROFILE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

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
                            for (QueryDocumentSnapshot document: task.getResult()){
                                User follower = document.toObject(User.class);
                                followerList.add(follower);

                            }
                            followerCallBack.onCallBack(followerList);
                            Log.d(TAG, "Follower list => " + followerList.size());
                        } else {
                        Log.d(TAG, "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private interface FollowerCallBack{
        void onCallBack(List<User> followers);
    }

    private void backToPrevious() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}