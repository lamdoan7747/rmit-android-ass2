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

    private static final String TAG = "GET_LOCATION_ACTIVITY";
    private GoogleMap mMap;

    private ArrayList<CleaningSite> cleaningSiteList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cleaningSiteList = new ArrayList<>();
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit,15));

        // On click listener
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                Marker newMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("New marker")
                        .snippet(latLng.latitude + "/" + latLng.longitude)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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
                        intent.putExtra("requestCode","200");
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

    private void getAllSites(FirestoreCallBack firestoreCallBack) {
        currentUser = mAuth.getCurrentUser();

        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (cleaningSite.getOwner().equals(currentUser.getUid())){
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }
                            firestoreCallBack.onCallBack(cleaningSiteList);
                            Log.d(TAG,"Size list => " + cleaningSiteList.size());


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onMapLoaded() {
        getAllSites(new GetLocationActivity.FirestoreCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                if (cleaningSiteList.size() < 1) {
                    Log.d(TAG, "Don't have any site");
                } else {
                    List<LatLng> locations = new ArrayList<>();

                    // Get all location latlng
                    for (CleaningSite cleaningSite: cleaningSiteList){
                        if (cleaningSite.getLat() == null || cleaningSite.getLng() == null){ continue; }
                        locations.add(new LatLng(cleaningSite.getLat(),cleaningSite.getLng()));

                        // Set custom info Google map adapter
                        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(GetLocationActivity.this);
                        mMap.setInfoWindowAdapter(adapter);

                        // Add marker for all sites
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(cleaningSite.getLat(),cleaningSite.getLng()))
                                .title(cleaningSite.getName())
                                .snippet(cleaningSite.getAddress())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));
                    }

                    // Add all marker
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(locations.get(0));
                    builder.include(locations.get(locations.size() - 1));
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,200);
                    mMap.moveCamera(cu);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 200, null);


                }
            }
        });
    }

    private interface FirestoreCallBack{
        void onCallBack(List<CleaningSite> cleaningSites);
    }


}