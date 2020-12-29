package com.example.rmit_android_ass2.main.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.rmit_android_ass2.main.homeView.HomeViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.example.rmit_android_ass2.R;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "ADMIN_ACTIVITY";
    private int startingPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // Load fragment
        if (savedInstanceState == null) {
            loadFragment(new ReportFragment(), 1);
        }

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_admin);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        int newPosition;
        switch (item.getItemId()){
            case R.id.bottomNavigationHomeAdmin:
                fragment = new ReportFragment();
                newPosition = 1;
                break;
            case R.id.bottomNavigationSettingAdmin:
                fragment = new UserFragment();
                newPosition = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return loadFragment(fragment, newPosition);
    };

    private boolean loadFragment(Fragment fragment, int newPosition) {
        if (fragment != null) {
            if (newPosition == 0){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminContainer, fragment)
                        .commit();
            }
            if(startingPosition > newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right )
                        .replace(R.id.adminContainer, fragment).commit();

            }
            if(startingPosition < newPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.adminContainer, fragment).commit();

            }
            startingPosition = newPosition;
            return true;
        }
        return false;
    }
}