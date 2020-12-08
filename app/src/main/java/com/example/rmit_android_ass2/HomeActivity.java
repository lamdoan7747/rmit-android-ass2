package com.example.rmit_android_ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private ActionBar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        toolBar = getSupportActionBar();
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


        toolBar.setTitle("Google Map");
        loadFragment(new MapsFragment());



    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.gMap:
                    toolBar.setTitle("Google Map");
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.lView:
                    toolBar.setTitle("List Site");
                    fragment = new SiteListFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.cCite:
                    toolBar.setTitle("Create Site");
                    fragment = new SiteCreateFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.uProfile:
                    toolBar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    private void loadFragment(Fragment fragment) {
        // Load Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}