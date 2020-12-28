package com.example.rmit_android_ass2.auth;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private ProgressBar loadingProgressBar;
    private Button loginButton, registerButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        renderView(view);
        onClickListener();
    }

    private void onClickListener() {
        // On login click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email)){
                    emailEditText.setError("Input valid email");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    passwordEditText.setError("Input valid password");
                    return;
                }

                logIn(email, password);
            }
        });

        // Navigate to register fragment
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new RegisterFragment());
            }
        });
    }

    private void renderView(View view) {
        emailEditText = view.findViewById(R.id.lEmail);
        passwordEditText = view.findViewById(R.id.lPassword);
        loadingProgressBar = view.findViewById(R.id.lLoading);
        loginButton = view.findViewById(R.id.lLogin);
        registerButton = view.findViewById(R.id.lRegister);

    }

    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,R.anim.slide_out_right);
        transaction.replace(R.id.authContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logIn(String email, String password) {

        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getTag(), "signInWithEmail:success");
                            loadingProgressBar.setVisibility(View.GONE);

                            startActivity(new Intent(getActivity(),HomeActivity.class));
                            getActivity().finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            loadingProgressBar.setVisibility(View.GONE);
                            Log.d(getTag(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}