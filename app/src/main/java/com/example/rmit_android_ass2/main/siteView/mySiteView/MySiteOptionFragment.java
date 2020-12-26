package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MySiteOptionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SITE_ID = "cleaningSiteId";

    private String cleaningSiteId;

    private TextView editSiteOption, viewFollowerOption, deleteSiteOption;
    private ImageButton backButton;

    private FirebaseFirestore db;

    public MySiteOptionFragment() {
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
        return inflater.inflate(R.layout.fragment_my_site_option, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        editSiteOption = getView().findViewById(R.id.editSiteMySiteOption);
        viewFollowerOption = getView().findViewById(R.id.viewFollowerMySiteOption);
        deleteSiteOption = getView().findViewById(R.id.deleteSiteMySiteOption);
        backButton = getView().findViewById(R.id.backButtonToolbarMySiteOption);

        editSiteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteEditFragment());
            }
        });

        viewFollowerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteFollowerFragment());
            }
        });

        deleteSiteOption.setOnClickListener(new View.OnClickListener() {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
    }

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
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
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.editFrameContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}