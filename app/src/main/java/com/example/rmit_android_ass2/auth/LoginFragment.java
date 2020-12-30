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
import com.example.rmit_android_ass2.main.admin.AdminActivity;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment {
    // Constant declaration
    private final String TAG = "LOGIN_FRAGMENT";

    // Google Firebase declaration
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Android View declaration
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

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initiate view for the fragment
        renderView(view);

        // Set all events on touchable
        onClickListener();
    }

    private void onClickListener() {
        /*
         *  Login button
         *  When clicked, function get two variable {email, password}
         *  from UI to check valid and start logIn method
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Input valid email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Input valid password");
                    return;
                }

                logIn(email, password);
            }
        });

        // Click button move to the register fragment
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load new fragment when clicked
                loadFragment(new RegisterFragment());
            }
        });
    }

    /**
     * Function to initiate all the view with the right id
     *
     * @param view view get from UI
     */
    private void renderView(View view) {
        emailEditText = view.findViewById(R.id.emailLogin);
        passwordEditText = view.findViewById(R.id.passwordLogin);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBarLogin);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.moveToRegisterButton);

    }

    /**
     * Function to login to the app using FirebaseAuth API
     * if Success, call function checkAdmin to start new activity
     * if Failure, display an error toast
     *
     * @param email    email get from UI editText
     * @param password password get from UI editText
     */
    private void logIn(String email, String password) {
        // Set loading progress bar when function start
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail: SUCCESS");

                            // Set loading bar stop
                            loadingProgressBar.setVisibility(View.GONE);

                            // Get user id to check if user is admin
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();

                                // Method check if user is Admin
                                checkAdmin(userId);
                            }


                        } else {
                            // Set loading bar stop
                            loadingProgressBar.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail: FAILURE", task.getException());
                            Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Function to check if user is admin
     * if Success, start admin flow activity
     * if Failure, start user flow activity
     *
     * @param userId: get userId to query in Firebase
     */
    private void checkAdmin(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If document check having a boolean field admin is not null
                            if (document.getBoolean("isAdmin") != null) {
                                startActivity(new Intent(getActivity(), AdminActivity.class));
                            } else {
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                            }
                            // finish the Auth activity when finish
                            getActivity().finish();
                        }
                    }
                });
    }

    /**
     * Start a new transaction to add fragment
     *
     * @param fragment: init fragment to load
     */
    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.authContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}