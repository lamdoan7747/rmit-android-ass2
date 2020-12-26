package com.example.rmit_android_ass2.main.siteView.mySiteView;

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
import com.example.rmit_android_ass2.main.siteView.SiteListAdapter;
import com.example.rmit_android_ass2.main.siteView.SiteViewFragment;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

        backButton = getView().findViewById(R.id.backButtonToolbarMySiteFollower);
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

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void displayFollowerList(String cleaningSiteId) {
        getFollowers(cleaningSiteId, new FirestoreCallBack() {
            @Override
            public void onCallBack(List<User> followers) {
                followerListAdapter = new FollowerListAdapter(followerList);
                listFollower = getView().findViewById(R.id.listFollower);
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

    private void getFollowers(String cleaningSiteId, FirestoreCallBack firestoreCallBack) {

        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()){
                                Log.d("GET FOLLOWER", document.getId() + " => " + document.getData());
                                User follower = document.toObject(User.class);
                                followerList.add(follower);

                            }
                            firestoreCallBack.onCallBack(followerList);
                        } else {
                            Log.d("GET FOLLOWER", "Error getting documents: " + task.getException());
                        }
                    }
                });

    }

    private interface FirestoreCallBack{
        void onCallBack(List<User> followers);
    }
}