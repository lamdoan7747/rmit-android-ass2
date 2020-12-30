package com.example.rmit_android_ass2.auth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterFragment extends Fragment {
    // Constant declaration
    private static final String EMAIL_REGEX_CHECK = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
    private final String TAG = "REGISTER_FRAGMENT";

    // Google Firebase declaration
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Android view declaration
    private EditText nameRegister, passwordRegister,
            emailRegister, confirmPasswordRegister;
    private Button registerButton;
    private TextView backToLogin;
    private ProgressBar loadingProgressBar;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_register, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initiate view for the fragment
        renderView(view);

        // Set all events on touchable
        onClickListener();

    }

    private void onClickListener() {
        /*
         *   Register button
         *   When clicked, function get 4 variable
         *   from UI to check valid and start registerAccount function
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailRegister.getText().toString();
                String name = nameRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                String confirmPassword = confirmPasswordRegister.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX_CHECK)) {
                    emailRegister.setError("Invalid Email");
                    return;
                }
                if (name.isEmpty() || name.equals(" ")) {
                    nameRegister.setError("Required");
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    passwordRegister.setError("Invalid Password");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPasswordRegister.setError("Invalid Password");
                    return;
                }

                registerAccount(email, password, name);
            }
        });

        // Back
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
    }

    private void renderView(View view) {
        nameRegister = view.findViewById(R.id.nameRegister);
        passwordRegister = view.findViewById(R.id.passwordRegister);
        confirmPasswordRegister = view.findViewById(R.id.confirmPasswordRegister);
        emailRegister = view.findViewById(R.id.emailRegister);
        registerButton = view.findViewById(R.id.registerButtonRegister);
        loadingProgressBar = view.findViewById(R.id.loadingBarRegister);
        backToLogin = view.findViewById(R.id.backToLoginRegister);
    }

    /**
     * Function to register to the app using FirebaseAuth API
     * if Success, call function addUser to cloud database
     * if Failure, display an error toast
     *
     * @param email    email get from UI editText
     * @param password password get from UI editText
     * @param name     name get from UI editText
     */
    private void registerAccount(String email, String password, String name) {
        // Set loading progress bar when function start
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail: SUCCESS");

                            // Set loading bar stop
                            loadingProgressBar.setVisibility(View.GONE);

                            // Get ID from User
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Add User to database
                            assert currentUser != null;
                            User user = new User(currentUser.getUid(), name, email);
                            addUser(user);


                        } else {
                            // Set loading bar stop
                            loadingProgressBar.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail: FAILURE", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Function to add user object to cloud database
     * if Success, back to Login Fragment
     * if Failure, display an error toast
     *
     * @param user: user object
     */
    private void addUser(User user) {
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "addUserToFirestore: SUCCESS", task.getException());
                            // Update UI
                            backToPrevious();
                        }
                    }
                });
    }

    // Return the previous fragment
    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}