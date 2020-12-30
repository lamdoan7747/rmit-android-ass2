package com.example.rmit_android_ass2.main.profileView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    // Constant declaration
    private final String TAG = "PROFILE_FRAGMENT";

    // Google Firebase declaration
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Declare FirebaseAuth to get current user
        mAuth = FirebaseAuth.getInstance();

        // Logout button
        Button logoutButton = getView().findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        });

    }

    // UpdateUI return to the AuthActivity for login when User logout -> finish current activity
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        }
    }
}