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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class HomeViewFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static final String TAG = "HOME_FRAGMENT";

    private ListView cleaningSiteListView;
    private SiteListAdapter siteListAdapter;
    private ArrayList<CleaningSite> cleaningSiteList;

    private TextView searchButton;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        searchButton = requireView().findViewById(R.id.searchHomeView);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        cleaningSiteList = new ArrayList<>();
        getAllSites(new OnSiteCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {

                Log.d(TAG,"Size list: " + cleaningSiteList.size());

                cleaningSiteListView = requireView().findViewById(R.id.cleaningSiteHomeView);

                siteListAdapter = new SiteListAdapter(cleaningSiteList);
                cleaningSiteListView.setAdapter(siteListAdapter);
                cleaningSiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG,"WORK");
                        Toast.makeText(getActivity(),"Clicked!",Toast.LENGTH_SHORT).show();
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

    private void getAllSites(OnSiteCallBack onSiteCallBack) {
        currentUser = mAuth.getCurrentUser();

        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CleaningSite cloudSite = document.toObject(CleaningSite.class);
                                cleaningSiteList.add(cloudSite);
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private interface OnSiteCallBack{
        void onCallBack(List<CleaningSite> cleaningSites);
    }
}