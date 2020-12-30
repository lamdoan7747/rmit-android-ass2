package com.example.rmit_android_ass2.main.siteView;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.adapter.SiteListAdapter;
import com.example.rmit_android_ass2.main.siteView.mySiteView.MySiteActivity;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MySiteListViewFragment extends Fragment {
    // Constant declaration
    private final String TAG = "SITE_VIEW_FRAGMENT";

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Android view declaration
    private TextView createSite, viewNoRecord;
    private ListView listSite;

    // Adapter
    private SiteListAdapter siteListAdapter;

    // Array list declaration
    private ArrayList<CleaningSite> cleaningSiteList;


    public MySiteListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_site_list_view, container, false);
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
         *   getSites callback to return the list of all sites to setup listview with adapter
         *   -> OnItemClick will save the CleaningSite object to Intent and starting new activity
         *   -> setup transition slide right
         */
        cleaningSiteList = new ArrayList<>();
        getSites(new OnSiteCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                Log.d(TAG, "Size list: " + cleaningSiteList.size());
                viewNoRecord = getView().findViewById(R.id.viewNoRecordMySiteListView);
                if (cleaningSites.size() < 1) {
                    viewNoRecord.setVisibility(View.VISIBLE);
                }


                // Setup adapter
                siteListAdapter = new SiteListAdapter(cleaningSiteList);
                listSite = requireView().findViewById(R.id.listMySite);
                listSite.setAdapter(siteListAdapter);
                listSite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "Item: " + position);
                        CleaningSite cleaningSite = (CleaningSite) siteListAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), MySiteActivity.class);
                        intent.putExtra("cleaningSite", cleaningSite);
                        startActivity(intent);
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
            }
        });

        // Create site load new fragment
        createSite = getView().findViewById(R.id.createSite);
        createSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SiteCreateFragment());
            }
        });
    }

    /**
     * Function to get all sites display to UI listView
     * if Success, add all CleaningSite object to a list
     * assign function onSiteCallBack
     * if Failure, display Log debug
     *
     * @param onSiteCallBack callBack to get list of sites
     */
    private void getSites(OnSiteCallBack onSiteCallBack) {
        currentUser = mAuth.getCurrentUser();
        db.collection("cleaningSites")
                .whereEqualTo("owner", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                cleaningSiteList.add(cleaningSite);
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                            Log.d(TAG, "Site list => " + cleaningSiteList.size());

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

    /**
     * Start a new transaction to add fragment
     *
     * @param fragment: init fragment to load
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}