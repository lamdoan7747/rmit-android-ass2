package com.example.rmit_android_ass2.main.listView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.example.rmit_android_ass2.main.siteView.SiteListAdapter;
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

/**
 * A fragment representing a list of Items.
 */
public class ListViewFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static final String TAG = "RecycleView";

    private ListView cleaningSiteListView;
    private SiteListAdapter siteListAdapter;
    private ArrayList<CleaningSite> cleaningSiteList;
    private int page = 1, limit = 10;
    private ProgressBar loadingBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        cleaningSiteList = new ArrayList<>();

        getAllSites(new OnSiteCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {

                Log.d(TAG,"Size list: " + cleaningSiteList.size());

                cleaningSiteListView = getView().findViewById(R.id.cleaningSiteListView);

                siteListAdapter = new SiteListAdapter(cleaningSiteList);
                cleaningSiteListView.setAdapter(siteListAdapter);
                cleaningSiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d("TEST ON ITEM CLICK","WORK");
                        Toast.makeText(getActivity(),"Clicked!",Toast.LENGTH_SHORT).show();
                        CleaningSite cleaningSite = (CleaningSite) siteListAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
                        intent.putExtra("cleaningSite", cleaningSite);
                        startActivity(intent);
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
                                Log.d("Site", document.getId() + " => " + document.getData());
                                CleaningSite cloudSite = document.toObject(CleaningSite.class);
                                cleaningSiteList.add(cloudSite);
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                        } else {
                            Log.d(getTag(), "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private interface OnSiteCallBack{
        void onCallBack(List<CleaningSite> cleaningSites);
    }
}