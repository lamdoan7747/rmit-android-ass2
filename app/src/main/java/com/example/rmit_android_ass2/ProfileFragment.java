package com.example.rmit_android_ass2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rmit_android_ass2.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        logoutButton = getView().findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        });

    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(),AuthActivity.class));
            getActivity().finish();
        }
    }


}