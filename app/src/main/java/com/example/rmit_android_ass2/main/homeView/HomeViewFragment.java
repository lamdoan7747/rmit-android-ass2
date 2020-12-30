package com.example.rmit_android_ass2.main.homeView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit_android_ass2.HomeActivity;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SearchActivity;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.example.rmit_android_ass2.main.adapter.SiteListAdapter;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class HomeViewFragment extends Fragment {

    // Constant declaration
    private static final String TAG = "HOME_FRAGMENT";

    // Android view declaration
    private TextView searchView, userName;
    private ListView cleaningSiteListView;

    // Adapter declaration
    private SiteListAdapter siteListAdapter;

    // Array list declaration
    private ArrayList<CleaningSite> cleaningSiteList;


    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeViewFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_view, container, false);
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
        mAuth = FirebaseAuth.getInstance();

        /*
         *   Search button is default, when clicked on search button
         *   -> starting new activity
         *   -> setup transition slide right
         */
        searchView = requireView().findViewById(R.id.searchHomeView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Get userId from FirebaseAuth to get user name
        currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        getUser(userId);

        /*
         *   getSites callback to return the list of all sites to setup listview with adapter
         *   -> OnItemClick will save the CleaningSite object to Intent and starting new activity
         *   -> setup transition slide right
         */
        cleaningSiteList = new ArrayList<>();
        getSites(new OnSiteCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                Log.d(TAG, "Size list: " + cleaningSiteList.size());
                cleaningSiteListView = requireView().findViewById(R.id.cleaningSiteHomeView);

                // Setup adapter
                siteListAdapter = new SiteListAdapter(cleaningSiteList);
                cleaningSiteListView.setAdapter(siteListAdapter);

                // Setup onItemClick
                cleaningSiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        CleaningSite cleaningSite = (CleaningSite) siteListAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
                        intent.putExtra("cleaningSite", cleaningSite);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
        });
    }

    /**
     * Function to get user detail from Firebase and display to UI
     *
     * @param userId document id of user
     */
    private void getUser(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User currentUser = document.toObject(User.class);
                            userName = requireView().findViewById(R.id.userNameHomeView);
                            userName.setText(currentUser.getName());
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }


    /**
     * Function to get all sites display to UI listView except current user's sites
     * if Success, add all CleaningSite object to a list
     * assign function onSiteCallBack
     * if Failure, display Log debug
     *
     * @param onSiteCallBack callBack to get list of sites
     */
    private void getSites(OnSiteCallBack onSiteCallBack) {
        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())) {
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of siteSuggestion from getSites().
     */
    private interface OnSiteCallBack {
        void onCallBack(List<CleaningSite> cleaningSites);
    }
}