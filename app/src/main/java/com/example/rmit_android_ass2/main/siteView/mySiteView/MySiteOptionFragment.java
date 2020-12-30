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
    // Constant declaration
    private static final String TAG = "MY_SITE_OPTION_FRAGMENT";
    private static final String SITE_ID = "cleaningSiteId";

    // Android View declaration
    private TextView editSiteOption, viewFollowerOption, deleteSiteOption;
    private ImageButton backButton;

    // Google Firebase declaration
    private FirebaseFirestore db;

    // Utils variable declaration
    private String cleaningSiteId;


    public MySiteOptionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get message cleaningSiteId from previous fragment
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

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();

        // Initiate view for the activity
        renderView(requireView());

        // Set all events on touchable
        onClickListener();
    }

    private void onClickListener() {
        // Edit site option to load edit fragment
        editSiteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteEditFragment());
            }
        });

        // View follower option to load site follower fragment
        viewFollowerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(cleaningSiteId, new MySiteFollowerFragment());
            }
        });

        /*
         *   Delete site option will popup dialog to confirm if User want to delete
         *   if Ok, trigger deleteSite() function
         *   if Cancel, cancel dialog
         * */
        deleteSiteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Confirm Delete")
                        .setMessage("Do you want to delete this site?")
                        .setMessage("All information included followers and activities would be deleted!")
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

        // Back button to return the previous fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Function to initiate all the view with the right id
     *
     * @param view view get from UI
     */
    private void renderView(View view) {
        editSiteOption = view.findViewById(R.id.editSiteMySiteOption);
        viewFollowerOption = view.findViewById(R.id.viewFollowerMySiteOption);
        deleteSiteOption = view.findViewById(R.id.deleteSiteMySiteOption);
        backButton = view.findViewById(R.id.backButtonToolbarMySiteOption);
    }

    /**
     * Function to delete Site document in Firebase
     * if Success, finish activity
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
    private void deleteSite(String cleaningSiteId) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Delete option: SUCCESS");
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Delete option: FAILURE", e);
                    }
                });
    }

    /**
     * Start a new transaction to add fragment
     * -> Save message to transfer to other fragment
     * -> Setup transition slide up, slide down
     *
     * @param cleaningSiteId: cleaningSiteId to transfer to other fragment
     * @param fragment:       init fragment to load
     */
    private void loadFragment(String cleaningSiteId, Fragment fragment) {
        // Set message for new fragment
        Bundle bundle = new Bundle();
        bundle.putString("cleaningSiteId", cleaningSiteId);
        fragment.setArguments(bundle);

        // Start transaction with new fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        fragmentTransaction.replace(R.id.editFrameContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}