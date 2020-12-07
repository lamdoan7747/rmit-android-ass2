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
            Toast.makeText(HomeActivity.this, "Here", Toast.LENGTH_SHORT).show();
            switch (item.getItemId()) {
                case R.id.gMap:
                    toolBar.setTitle("Google Map");
                    Toast.makeText(HomeActivity.this, "Google Map", Toast.LENGTH_SHORT).show();
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.lView:
                    toolBar.setTitle("List Site");
                    Toast.makeText(HomeActivity.this, "List site", Toast.LENGTH_SHORT).show();
                    fragment = new SiteListFragment();
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