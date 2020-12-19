package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class MySiteDetailFragment extends Fragment {

    private Button editButton, deleteButton;
    private TextView followerView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;


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

        editButton = getView().findViewById(R.id.editSite);
        deleteButton = getView().findViewById(R.id.deleteSite);
        followerView = getView().findViewById(R.id.editViewFollower);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getActivity().getIntent();
        CleaningSite cleaningSite = (CleaningSite) intent.getExtras().get("cleaningSite");
        String cleaningSiteId = (String) cleaningSite.get_id();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new SiteEditFragment());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Confirm Delete")
                        .setMessage("Do you want to delete this site?")
                        .setMessage("All information included followers would be deleted!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSite(cleaningSiteId);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private void deleteSite(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DELETE SITE", "DocumentSnapshot successfully deleted!");
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DELETE SITE", "Error deleting document", e);
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
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up);
        fragmentTransaction.replace(R.id.editFrameContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}