package com.example.rmit_android_ass2.main.mapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.example.rmit_android_ass2.main.listView.ListViewFragment;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback {

    private SearchView searchMap;
    private GoogleMap mMap;

    private ArrayList<CleaningSite> cleaningSiteList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        searchMap = (SearchView) getView().findViewById(R.id.searchMap);
        mapFragment.getMapAsync(this);

        cleaningSiteList = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng rmit = new LatLng(10.729655, 106.693023);
        mMap.addMarker(new MarkerOptions().position(rmit).title("Marker in RMIT"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit,15));

        // On Search map
        searchMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                String location = searchMap.getQuery().toString();
//                List<Address> addressList = null;
//                if (location!=null||!location.equals("")){
//                    Geocoder geocoder = new Geocoder(getActivity());
//                    try {
//                        addressList = geocoder.getFromLocationName(location,1);
//                    } catch (IOException e){
//                        e.printStackTrace();
//                    }
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // On click listener
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Load all site
        mMap.setOnMapLoadedCallback(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
        for (CleaningSite cleaningSite: cleaningSiteList){
            if (cleaningSite.getName().equals(marker.getTitle())){
                Log.d("GET_NAME", "Error getting documents: " + cleaningSite.getName());
                Log.d("GET_NAME", "Error getting documents: " + marker.getTitle());
                Log.d("GET_ID", "Error getting documents: " + cleaningSite.get_id());
                intent.putExtra("siteId", cleaningSite.get_id());
            }
        }
        startActivity(intent);
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
                                Log.d("DOCUMENT_ID", document.getId() + " => " + document.getData());
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())){
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }

                            firestoreCallBack.onCallBack(cleaningSiteList);

                        } else {
                            Log.d(getTag(), "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onMapLoaded() {
        getAllSites(new FirestoreCallBack() {
            @Override
            public void onCallBack(List<CleaningSite> cleaningSites) {
                if (cleaningSiteList.size() < 1) {
                    Log.d("LIST_SIZE", "Don't have any site");
                } else {
                    List<LatLng> locations = new ArrayList<>();

                    // Get all location latlng
                    for (CleaningSite cleaningSite: cleaningSiteList){
                        if (cleaningSite.getLat() == null || cleaningSite.getLng() == null){ continue; }
                        locations.add(new LatLng(cleaningSite.getLat(),cleaningSite.getLng()));
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