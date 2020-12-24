package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.rmit_android_ass2.main.listView.ListViewFragment;
import com.example.rmit_android_ass2.main.mapView.MapsFragment;
import com.example.rmit_android_ass2.main.mapView.direction.TaskLoadedCallback;
import com.example.rmit_android_ass2.main.profileView.ProfileFragment;
import com.example.rmit_android_ass2.main.siteView.SiteViewFragment;
import com.example.rmit_android_ass2.notification.NotificationHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity{
    private BottomNavigationView navigationView;
    private int startingPosition = 0;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        int selectedItemId = navigationView.getSelectedItemId();
//        if (R.id.bottom_navigation != selectedItemId) {
//            loadFragment(new MapsFragment(), 1);
//            toolBar.setTitle("Google Map");
//            navigationView.setSelectedItemId(R.id.bottom_navigation);
//        } else {
//            super.onBackPressed();
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("TEST", "onActivityResult: " + requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set notification channel for User
        NotificationHelper.createNotificationChannel(getApplicationContext());

        if (savedInstanceState == null) {
            loadFragment(new ListViewFragment(), 1);
        }

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            int newPosition = 0;
            switch (item.getItemId()) {
                case R.id.bottomNavigationHome:
                    fragment = new ListViewFragment();
                    newPosition = 1;
                    break;
                case R.id.bottomNavigationMap:
                    fragment = new MapsFragment();
                    newPosition = 2;
                    break;
                case R.id.bottomNavigationMySite:
                    fragment = new SiteViewFragment();
                    newPosition = 3;
                    break;
                case R.id.bottomNavigationProfile:
                    fragment = new ProfileFragment();
                    newPosition = 4;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            return loadFragment(fragment, newPosition);
        }
    };


    private boolean loadFragment(Fragment fragment, int newPosition) {
        if(fragment != null) {
            if(newPosition == 0) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainer, fragment).commit();

            }
            if(startingPosition > newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right )
                        .replace(R.id.frameContainer, fragment).commit();

            }
            if(startingPosition < newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.frameContainer, fragment).commit();

            }
            startingPosition = newPosition;
            return true;
        }

        return false;
    }


}