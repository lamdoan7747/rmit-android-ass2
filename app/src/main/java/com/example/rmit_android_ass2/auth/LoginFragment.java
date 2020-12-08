package com.example.rmit_android_ass2.auth;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rmit_android_ass2.HomeActivity;
import com.example.rmit_android_ass2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        final EditText emailEditText = view.findViewById(R.id.lEmail);
        final EditText passwordEditText = view.findViewById(R.id.lPassword);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);

        final Button loginButton = view.findViewById(R.id.lLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                signIn(email, password);
            }
        });

        final Button registerButton = view.findViewById(R.id.lRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new RegisterFragment());
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.authContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getTag(), "signInWithEmail:success");
                            Toast.makeText(getActivity(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(getTag(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Toast.makeText(getActivity(),"Sign in successfully!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),HomeActivity.class));
            getActivity().finish();
        }
    }
}