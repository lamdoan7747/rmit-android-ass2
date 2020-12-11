package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.rmit_android_ass2.main.listView.ListViewFragment;
import com.example.rmit_android_ass2.main.mapView.MapsFragment;
import com.example.rmit_android_ass2.main.profileView.ProfileFragment;
import com.example.rmit_android_ass2.main.siteView.SiteViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private ActionBar actionBar;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            loadFragment(new MapsFragment(), 1);
        }

        Toolbar toolBar = findViewById(R.id.toolbarMaterial);
        setSupportActionBar(toolBar);
        actionBar = getSupportActionBar();

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            int newPosition = 0;
            switch (item.getItemId()) {
                case R.id.gMap:
                    actionBar.setTitle("Google Map");
                    fragment = new MapsFragment();
                    newPosition = 1;
                    break;
                case R.id.lView:
                    actionBar.setTitle("List Site");
                    fragment = new ListViewFragment();
                    newPosition = 2;
                    break;
                case R.id.cCite:
                    actionBar.setTitle("Create Site");
                    fragment = new SiteViewFragment();
                    newPosition = 3;
                    break;
                case R.id.uProfile:
                    actionBar.setTitle("Profile");
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