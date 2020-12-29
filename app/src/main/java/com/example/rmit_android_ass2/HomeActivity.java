package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.rmit_android_ass2.main.homeView.HomeViewFragment;
import com.example.rmit_android_ass2.main.mapView.MapsFragment;
import com.example.rmit_android_ass2.main.profileView.ProfileFragment;
import com.example.rmit_android_ass2.main.siteView.SiteViewFragment;
import com.example.rmit_android_ass2.notification.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity{

    private static final String TAG = "HOME_ACTIVITY";
    private int startingPosition = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: " + requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set notification channel for User
        NotificationHelper.createNotificationChannel(getApplicationContext());

        // Load fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeViewFragment(), 1);
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                int newPosition;
                switch (item.getItemId()) {
                    case R.id.bottomNavigationHome:
                        fragment = new HomeViewFragment();
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