package com.example.rmit_android_ass2.main.siteView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.adapter.CustomInfoWindowAdapter;
import com.example.rmit_android_ass2.main.mapView.MapsFragment;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback {
    // Constant declaration
    private final String TAG = "GET_LOCATION_ACTIVITY";
    private GoogleMap mMap;

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Array list declaration
    private ArrayList<CleaningSite> cleaningSiteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng rmit = new LatLng(10.729655, 106.693023);
        mMap.addMarker(new MarkerOptions().position(rmit).title("Marker in RMIT"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit, 15));

        // On click listener
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                Marker newMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Click to info window to setup location!")
                        .snippet(latLng.latitude + "-" + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                newMarker.showInfoWindow();
            }
        });

        // Load all site
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLoadedCallback(this);

        // On click listener
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

    /**
     * Setup all markers display on the map view
     */
    @Override
    public void onMapLoaded() {
        // Get all sites to display marker to map
        cleaningSiteList = new ArrayList<>();
        getSites(new OnSiteCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                if (cleaningSiteList.size() < 1) {
                    Log.d(TAG, "Don't have any site");
                } else {
                    // Get all location latlng
                    for (CleaningSite cleaningSite : cleaningSiteList) {
                        if (cleaningSite.getLat() == null || cleaningSite.getLng() == null) {
                            continue;
                        }

                        // Set custom info Google map adapter
                        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(GetLocationActivity.this);
                        mMap.setInfoWindowAdapter(adapter);

                        // Add marker for all sites
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(cleaningSite.getLat(), cleaningSite.getLng()))
                                .title(cleaningSite.getName())
                                .snippet(cleaningSite.getAddress())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));
                    }
                }
            }
        });
    }

    /**
     * Setup event when click info window
     * -> popup dialog to confirm if want to get this location
     * -> if Success, save location & requestCode to intent and finish activity
     * -> if Failure, display Log debug
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GetLocationActivity.this);
        alertDialogBuilder
                .setTitle("Confirm location")
                .setMessage("Do you want to get this location?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.putExtra("lat", marker.getPosition().latitude);
                        intent.putExtra("lng", marker.getPosition().longitude);
                        intent.putExtra("requestCode", "200");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }

    /**
     * Function to get all sites display to UI listView
     * if Success, add all CleaningSite without current owner' site object to a list
     * by init from CleaningSite, then assign function onSiteCallBack
     * if Failure, display Log debug
     *
     * @param onSiteCallBack callBack to get list of sites
     */
    private void getSites(OnSiteCallBack onSiteCallBack) {
        currentUser = mAuth.getCurrentUser();
        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (cleaningSite.getOwner().equals(currentUser.getUid())) {
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                            Log.d(TAG, "Size list => " + cleaningSiteList.size());


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of cleaningSite from getSites().
     */
    private interface OnSiteCallBack {
        void onCallBack(List<CleaningSite> cleaningSites);
    }


}