package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.siteView.GetLocationActivity;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SiteEditFragment extends Fragment {

    private static int REQUEST_CODE = 200;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText editName, editAddress, editLatitude, editLongitude;
    private Button editSiteButton, editSiteBack, editSiteLocation;

    private String cleaningSiteId;


    public SiteEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            cleaningSiteId = getArguments().getString("cleaningSiteId");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null){
                Double latitude = (Double) data.getExtras().get("lat");
                Double longitude = (Double) data.getExtras().get("lng");
                editLatitude.setText(String.format("%s", latitude));
                editLongitude.setText(String.format("%s",longitude));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_edit, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editName = (EditText) getView().findViewById(R.id.editSiteName);
        editAddress = (EditText) getView().findViewById(R.id.editSiteAddress);
        editLatitude = (EditText) getView().findViewById(R.id.editSiteLatitude);
        editLongitude = (EditText) getView().findViewById(R.id.editSiteLongitude);

        editSiteButton = (Button) getView().findViewById(R.id.editSiteButton);
        editSiteBack = (Button) getView().findViewById(R.id.editSiteBack);
        editSiteLocation = (Button) getView().findViewById(R.id.editSiteLocation);

        displayCurrentSite(cleaningSiteId);

        editSiteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPrevious();
            }
        });

        editSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Confirm Update")
                        .setMessage("Do you want to update this site?")
                        .setMessage("All information included followers would be edited!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateSite(cleaningSiteId);
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

        editSiteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GetLocationActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    private void displayCurrentSite(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("GET MY SITE", "DocumentSnapshot data: " + document.getData());
                                CleaningSite cleaningSite = (CleaningSite) document.toObject(CleaningSite.class);
                                editName.setText(cleaningSite.getName());
                                editAddress.setText(cleaningSite.getAddress());
                                editLatitude.setText(String.valueOf(cleaningSite.getLat()));
                                editLongitude.setText(String.valueOf(cleaningSite.getLng()));

                            } else {
                                Log.d("GET MY SITE", "No such document");
                            }
                        } else {
                            Log.d("GET MY SITE", "get failed with ", task.getException());
                        }
                    }
        });
    }

    private void updateSite(String cleaningSiteId) {
        String siteName = editName.getText().toString();
        String siteAddress = editAddress.getText().toString();
        Double latitude = Double.valueOf(editLatitude.getText().toString());
        Double longitude = Double.valueOf(editLongitude.getText().toString());

        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name",siteName);
        updates.put("address",siteAddress);
        updates.put("lat",latitude);
        updates.put("lng",longitude);
        updates.put("timestamp", FieldValue.serverTimestamp());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UPDATE SITE", "DocumentSnapshot successfully updated!");
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UPDATE SITE", "Error updating document", e);
                    }
                });

    }

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}