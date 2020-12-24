package com.example.rmit_android_ass2.main.siteView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SiteCreateFragment extends Fragment {

    private static int REQUEST_CODE = 200;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText editName, editAddress, editLatitude, editLongitude;
    private Button aButton, bBack, aLocationButton;

    private static String ACTION = "";


    public SiteCreateFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_site_create, container, false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editName = (EditText) getView().findViewById(R.id.siteName);
        editAddress = (EditText) getView().findViewById(R.id.siteAddress);
        editLatitude = (EditText) getView().findViewById(R.id.siteLatitude);
        editLongitude = (EditText) getView().findViewById(R.id.siteLongitude);

        aButton = (Button) getView().findViewById(R.id.aButton);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String siteName = editName.getText().toString();
                String siteAddress = editAddress.getText().toString();
                Double latitude = Double.valueOf(editLatitude.getText().toString());
                Double longitude = Double.valueOf(editLongitude.getText().toString());

                CleaningSite cleaningSite = new CleaningSite();
                cleaningSite.setName(siteName);
                cleaningSite.setAddress(siteAddress);
                cleaningSite.setLat(latitude);
                cleaningSite.setLng(longitude);

                createSite(cleaningSite);
            }
        });
        
        bBack = (Button) getView().findViewById(R.id.aBack);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    backToPrevious();
            }
        });

        aLocationButton = getView().findViewById(R.id.aLocation);
        aLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GetLocationActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void createSite(CleaningSite cleaningSite) {
        currentUser = mAuth.getCurrentUser();
        cleaningSite.setOwner(currentUser.getUid());

        db.collection("cleaningSites")
                .add(cleaningSite)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(),"Add success: " + documentReference.getId(), Toast.LENGTH_LONG).show();
                        backToPrevious();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Failure: " + e.toString(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}