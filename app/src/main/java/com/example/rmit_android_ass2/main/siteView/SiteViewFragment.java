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
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
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
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SiteViewFragment extends Fragment {

    private ArrayList<CleaningSite> cleaningSiteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView createSite;
    private ListView listSite;
    private SiteListAdapter siteListAdapter;



    public SiteViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cleaningSiteList = new ArrayList<>();

        getSites(new FirestoreCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                siteListAdapter = new SiteListAdapter(cleaningSiteList);
                listSite = getView().findViewById(R.id.listSite);
                listSite.setAdapter(siteListAdapter);
                listSite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d("TEST ON ITEM CLICK","WORK");
                        Toast.makeText(getActivity(),"Clicked!",Toast.LENGTH_SHORT).show();
                        CleaningSite cleaningSite = (CleaningSite) siteListAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), MySiteActivity.class);
                        intent.putExtra("cleaningSite", cleaningSite);
                        startActivity(intent);
                    }
                });
            }
        });



        createSite = getView().findViewById(R.id.createSite);
        createSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SiteCreateFragment());
            }
        });



    }

    private void getSites(FirestoreCallBack firestoreCallBack) {
        currentUser = mAuth.getCurrentUser();

        db.collection("cleaningSites")
                .whereEqualTo("owner", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(getTag(), document.getId() + " => " + document.getData());
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                cleaningSiteList.add(cleaningSite);
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up);
        fragmentTransaction.replace(R.id.frameContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}