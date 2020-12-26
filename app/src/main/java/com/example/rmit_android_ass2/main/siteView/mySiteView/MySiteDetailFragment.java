package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MySiteDetailFragment extends Fragment {

    private Button editButton, deleteButton, getFollowerButton, insertButton;
    private ImageButton backButton, buttonSiteOption;
    private TextView followerView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private ArrayList<User> followerList;


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

        insertButton = getView().findViewById(R.id.insertSiteMySiteDetail);
        followerView = getView().findViewById(R.id.editViewFollowerMySiteDetail);
        buttonSiteOption = getView().findViewById(R.id.optionSiteMySiteDetail);
        backButton = getView().findViewById(R.id.backButtonToolbarMySiteDetail);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getActivity().getIntent();
        CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
        String cleaningSiteId = (String) cleaningSite.get_id();

        // Get number of followers
        followerList = new ArrayList<>();
        getFollowers(cleaningSiteId);

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

    private void backToPrevious() {
        getActivity().finish();
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
                                Log.d("FOLLOWER SIZE", document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                followerList.add(user);
                            }
                            followerView.setText(String.format("%s followers", followerList.size()));
                        } else {
                            Log.d("FOLLOWER SIZE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadFragment(String cleaningSiteId,Fragment fragment) {
        // Set message for new fragment
        Bundle bundle = new Bundle();
        bundle.putString("cleaningSiteId", cleaningSiteId);
        fragment.setArguments(bundle);

        // Start transaction with new fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.editFrameContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}