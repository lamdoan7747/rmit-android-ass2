package com.example.rmit_android_ass2.main.mapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.rmit_android_ass2.HomeActivity;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.example.rmit_android_ass2.main.mapView.direction.FetchDirectionURL;
import com.example.rmit_android_ass2.main.mapView.direction.TaskLoadedCallback;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback{

    private static final String TAG = "MAP_FRAGMENT";
    private FloatingSearchView searchMap;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient client;

    private Polyline currentPolyline;

    private ArrayList<CleaningSite> cleaningSiteList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private List<String> mSuggestions =new ArrayList<>();

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

        cleaningSiteList = new ArrayList<>();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        searchMap = requireView().findViewById(R.id.floating_search_view);
        searchMap.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchMap.clearSuggestions();
                } else {
//                    searchMap.showProgress();
//                    searchMap.swapSuggestions(getSuggestion(newQuery));
//                    searchMap.hideProgress();
                }
            }
        });
        searchMap.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
//                searchMap.showProgress();
//                searchMap.swapSuggestions(getSuggestion(searchMap.getQuery()));
//                searchMap.hideProgress();
            }

            @Override
            public void onFocusCleared() {

            }
        });


    }


    private void getCurrentLocation() {
        // Initialize task location
        @SuppressLint("MissingPermission")
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // When success
                if (location != null) {
                    // Initialize latlng
                    LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());

                    // Create marker option
                    // MarkerOptions options = new MarkerOptions().position(latLng).title("I'm here");
                    CircleOptions circles = new CircleOptions()
                            .center(currentLocation)
                            .radius(500)
                            .fillColor(Color.argb(70,140,180,160))
                            .strokeColor(Color.GREEN)
                            .strokeWidth(2f);

                    // Zoom map
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));

                    // Add marker
                    //mMap.addMarker(options);
                    mMap.addCircle(circles);

                    // Setup direction for Google Map
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            @SuppressLint("MissingPermission")
                            LatLng newLocation = marker.getPosition();
                            Button direction = requireView().findViewById(R.id.directionMap);
                            direction.setVisibility(View.VISIBLE);
                            direction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = getUrl(currentLocation, newLocation, "driving");
                                    new FetchDirectionURL(getActivity(), new TaskLoadedCallback() {
                                        @Override
                                        public void onTaskDone(Object... values) {
                                            if (currentPolyline != null)
                                                currentPolyline.remove();
                                            mMap.addPolyline((PolylineOptions) values[0]).setColor(Color.rgb(87,127,103));
                                        }
                                    }).execute(url, "driving");
                                }
                            });
                            return false;
                        }
                    });

                    
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When permission granted
                // Call get location method
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setting UI for google map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Load all site
        mMap.setOnMapLoadedCallback(this);
        mMap.setOnInfoWindowClickListener(this);

        // Check permission
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // When permission granted
            // Call method get location
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            // When permission denied
            // Request permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
        for (CleaningSite cleaningSite: cleaningSiteList){
            if (cleaningSite.getName().equals(marker.getTitle())){
                Log.d(TAG, "Get cleaning site: " + cleaningSite.get_id());
                intent.putExtra("cleaningSite", cleaningSite);
            }
        }
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }


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
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())){
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                            Log.d(TAG, "Site list => " + cleaningSiteList.size());


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onMapLoaded() {
        getSites(new OnSiteCallBack() {
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
                        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
                        mMap.setInfoWindowAdapter(adapter);

                        // Add marker for all sites
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(cleaningSite.getLat(),cleaningSite.getLng()))
                                .title(cleaningSite.getName())
                                .snippet(cleaningSite.getAddress())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));
                    }
                }
            }
        });
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = String.format("https://maps.googleapis.com/maps/api/directions/%s?%s&key=%s", output, parameters, getString(R.string.google_maps_key));
        return url;
    }



    private interface OnSiteCallBack{
        void onCallBack(List<CleaningSite> cleaningSites);
    }
}