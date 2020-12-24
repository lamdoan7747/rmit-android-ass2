package com.example.rmit_android_ass2.main.listView;

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
import android.widget.ProgressBar;

import com.example.rmit_android_ass2.R;
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

    private RecyclerView recyclerView;
    private CleaningSiteRecyclerViewAdapter adapter;
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

        recyclerView = getView().findViewById(R.id.recycleListView);

        cleaningSiteList = new ArrayList<>();

        getAllSites(new FirestoreCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                CleaningSiteRecyclerViewAdapter adapter = new CleaningSiteRecyclerViewAdapter(cleaningSiteList,getContext());
                Log.d(TAG,"Size list: " + cleaningSiteList.size());
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void getAllSites(FirestoreCallBack firestoreCallBack) {
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
                            firestoreCallBack.onCallBack(cleaningSiteList);
                        } else {
                            Log.d(getTag(), "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private interface FirestoreCallBack{
        void onCallBack(List<CleaningSite> cleaningSites);
    }
}