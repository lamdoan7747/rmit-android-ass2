package com.example.rmit_android_ass2.main.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserFragment extends Fragment {
    private final String TAG = "USER_FRAGMENT";

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_user, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        Button logoutButton = getView().findViewById(R.id.logoutAdmin);
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
            startActivity(new Intent(getActivity(), AuthActivity.class));
            getActivity().finish();
        }
    }
}