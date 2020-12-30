package com.example.rmit_android_ass2.main.mapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.SiteDetailActivity;
import com.example.rmit_android_ass2.main.adapter.CustomInfoWindowAdapter;
import com.example.rmit_android_ass2.main.mapView.direction.FetchDirectionURL;
import com.example.rmit_android_ass2.main.mapView.direction.TaskLoadedCallback;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.SiteSuggestion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLoadedCallback {

    // Constant declaration
    private static final String TAG = "MAP_FRAGMENT";

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Google Map declaration
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private Polyline currentPolyline;

    // Android view declaration
    private FloatingSearchView searchMap;

    // Array list declaration
    private ArrayList<CleaningSite> cleaningSiteList;

    // Utils variable declaration
    private String mLastQuery = "";
    private final int limit = 3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Initialize fused location, the main entry point for location services integration
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Action when use the searchView
        searchMap = requireView().findViewById(R.id.floating_search_view);
        setupFloatingSearch();
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

        // Setting UI for google map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Load all site
        mMap.setOnMapLoadedCallback(this);
        mMap.setOnInfoWindowClickListener(this);

        // Set custom info Google map adapter
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
        mMap.setInfoWindowAdapter(adapter);

        // Check permission
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                Log.d(TAG, "Site list => " + cleaningSiteList.size());
                if (cleaningSiteList.size() < 1) {
                    Log.d(TAG, "Don't have any site");
                } else {
                    // Get all location latlng
                    for (CleaningSite cleaningSite : cleaningSiteList) {
                        if (cleaningSite.getLat() == null || cleaningSite.getLng() == null) {
                            continue;
                        }

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
     * -> save cleaningSite object to transfer to new activity
     * -> start new activity
     * -> setup transition slide to right
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), SiteDetailActivity.class);
        for (CleaningSite cleaningSite : cleaningSiteList) {
            if (cleaningSite.getName().equals(marker.getTitle())) {
                Log.d(TAG, "Get cleaning site: " + cleaningSite.getId());
                intent.putExtra("cleaningSite", cleaningSite);
            }
        }
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Setup search view event
     */
    private void setupFloatingSearch() {
        searchMap.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            /**
             * Called when the query has changed. It will
             * be invoked when one or more characters in the
             * query was changed.
             *
             * @param oldQuery the previous query
             * @param newQuery the new query
             */
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mLastQuery = "";
                    searchMap.clearSuggestions();
                } else {
                    /*  this shows the top left circular progress
                     *  you can call it where ever you want, but
                     *  it makes sense to do it when loading something in
                     *  the background.
                     */
                    searchMap.showProgress();
                    getSuggestionSites(new OnSiteSuggestionCallBack() {
                        @Override
                        public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                            List<SiteSuggestion> suggestionList = new ArrayList<>();
                            for (SiteSuggestion siteSuggestion : siteSuggestions) {
                                if (siteSuggestion.getBody().toLowerCase().contains(newQuery.toLowerCase())) {
                                    suggestionList.add(siteSuggestion);
                                    if (suggestionList.size() == limit) {
                                        break;
                                    }
                                }
                            }
                            mLastQuery = newQuery;
                            searchMap.swapSuggestions(suggestionList);
                        }
                    });
                    //let the users know that the background
                    //process has completed
                    searchMap.hideProgress();
                    Log.d(TAG, "onSearchTextChanged(): " + newQuery);
                }
            }
        });

        searchMap.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            /**
             * Called when a suggestion was clicked indicating
             * that the current search has completed.
             * set move to the exact location when clicked
             *
             * @param searchSuggestion siteSuggestion
             */
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                getSites(new OnSiteCallBack() {
                    @Override
                    public void onCallBack(List<CleaningSite> cleaningSites) {
                        for (CleaningSite cleaningSite : cleaningSites) {
                            SiteSuggestion siteSuggestion = (SiteSuggestion) searchSuggestion;
                            String cleaningSiteId = siteSuggestion.getCleaningSiteId();
                            if (cleaningSite.getId().equals(cleaningSiteId)) {
                                LatLng searchLocation = new LatLng(cleaningSite.getLat(), cleaningSite.getLng());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLocation, 20));
                                mLastQuery = searchSuggestion.getBody();
                                searchMap.clearSearchFocus();
                            }
                        }
                    }
                });
            }

            @Override
            public void onSearchAction(String currentQuery) {
                mLastQuery = currentQuery;
                Log.d(TAG, "onSearchAction()");
            }
        });

        searchMap.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            /**
             * Called when the search bar has gained focus
             * and listeners are now active.
             */
            @Override
            public void onFocus() {
                searchMap.showProgress();
                //show suggestions when search bar gains focus (typically history suggestions)
                //searchView.swapSuggestions(DataHelper.getHistory(SearchActivity.this, 3));
                getSuggestionSites(new OnSiteSuggestionCallBack() {
                    @Override
                    public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                        List<SiteSuggestion> suggestionList = new ArrayList<>();
                        for (SiteSuggestion siteSuggestion : siteSuggestions) {
                            if (siteSuggestion.getBody().toLowerCase().contains(searchMap.getQuery().toLowerCase())) {
                                suggestionList.add(siteSuggestion);
                                if (suggestionList.size() == limit) {
                                    break;
                                }
                            }
                        }
                        searchMap.swapSuggestions(suggestionList);
                    }
                });
                searchMap.hideProgress();
                Log.d(TAG, "onFocus()");
            }

            /**
             * Called when the search bar has lost focus
             * and listeners are no more active.
             */
            @Override
            public void onFocusCleared() {
                //set the title of the bar so that when focus is returned a new query begins
                searchMap.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                searchMap.setSearchText(mLastQuery);
                Log.d(TAG, "onFocusCleared()");
            }
        });
    }

    /**
     * Setup current location and route for user when click on a marker
     */
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
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Create marker option
                    // MarkerOptions options = new MarkerOptions().position(latLng).title("I'm here");
                    CircleOptions circles = new CircleOptions()
                            .center(currentLocation)
                            .radius(500)
                            .fillColor(Color.argb(70, 140, 180, 160))
                            .strokeColor(Color.GREEN)
                            .strokeWidth(2f);

                    // Zoom map
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    // Add circle
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
                                            if (currentPolyline != null) {
                                                currentPolyline.remove();
                                            }
                                            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
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

    // Setup Permission to get current location
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
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())) {
                                    cleaningSiteList.add(cleaningSite);
                                }
                            }
                            onSiteCallBack.onCallBack(cleaningSiteList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Function to get all suggestions display to UI searchView
     * if Success, add all siteSuggestion without current owner' suggestion object to a list
     * by init from siteSuggestion, then assign function onSiteSuggestionCallBack
     * if Failure, display Log debug
     *
     * @param onSiteSuggestionCallBack callBack to get list of site suggestion
     */
    private void getSuggestionSites(OnSiteSuggestionCallBack onSiteSuggestionCallBack) {
        List<SiteSuggestion> siteSuggestions = new ArrayList<>();
        currentUser = mAuth.getCurrentUser();

        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())) {
                                    siteSuggestions.add(new SiteSuggestion(cleaningSite.getName(), cleaningSite.getId()));
                                }
                            }
                            onSiteSuggestionCallBack.onCallBack(siteSuggestions);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // Function to get url to return the polyline direction
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

    /**
     * Interface for implementing a listener to listen
     * to get list of cleaningSite from getSites().
     */
    private interface OnSiteCallBack {
        void onCallBack(List<CleaningSite> cleaningSites);
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of siteSuggestion from getSites().
     */
    private interface OnSiteSuggestionCallBack {
        void onCallBack(List<SiteSuggestion> siteSuggestions);
    }
}