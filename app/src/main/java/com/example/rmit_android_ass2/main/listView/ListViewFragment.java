package com.example.rmit_android_ass2.main.listView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        View view = inflater.inflate(R.layout.fragment_site_list_list, container, false);

        //loadingBar = view.findViewById(R.id.contentProgressLoadBar);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            cleaningSiteList = new ArrayList<>();
            getAllSites(new FirestoreCallBack() {
                @Override
                public void onCallBack(List<CleaningSite> cleaningSites) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

                    if (mColumnCount <=1) {
                        recyclerView.setLayoutManager(linearLayoutManager);
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }

                    recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

                    CleaningSiteRecyclerViewAdapter adapter = new CleaningSiteRecyclerViewAdapter(cleaningSiteList,context);

                    recyclerView.setAdapter(adapter);
                }
            });



//             Get data
//            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            //loadingBar.setVisibility(View.VISIBLE);
//
//                            Toast.makeText(view.getContext(), "Loading More ...",
//                                    Toast.LENGTH_SHORT).show();
//
//                            List<CleaningSite> list = new ArrayList<>();
//                            for (int i = 0; i <= 5; i++) {
//                                list.add(new CleaningSite("Mới "+ i, "1988"));
//                            }
//                            cleaningSiteList.addAll(list);
//                            adapter.notifyDataSetChanged();
//                            //loadingBar.setVisibility(View.GONE);
//                        }
//                    },1000);
//                }
//            });

        }
        return view;
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
                                Log.d(getTag(), document.getId() + " => " + document.getData());
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