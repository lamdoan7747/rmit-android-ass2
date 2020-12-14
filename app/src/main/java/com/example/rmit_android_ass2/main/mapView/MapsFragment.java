package com.example.rmit_android_ass2.main.mapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private SearchView searchMap;
    private GoogleMap map;

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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        searchMap = (SearchView) getView().findViewById(R.id.searchMap);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnInfoWindowClickListener(this);

        // On Search map
        searchMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchMap.getQuery().toString();
                List<Address> addressList = null;
                if (location!=null||!location.equals("")){
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // On click listener
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                Marker newMarker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("New marker")
                        .snippet(latLng.latitude + "/" + latLng.longitude)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                newMarker.showInfoWindow();
                Toast.makeText(getActivity(), latLng.latitude + "-" + latLng.longitude,Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked", Toast.LENGTH_SHORT).show();
    }
}