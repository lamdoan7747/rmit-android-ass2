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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText fnameRegister, passwordRegister,
            emailRegister, confirmPasswordRegister;
    private Button registerButton;
    private TextView backToLogin;
    private ProgressBar loadingProgressBar;

    public static final String EMAIL_REGEX_CHECK = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";


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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        renderView(view);
        onClickListener();

    }


    private void onClickListener() {
        // On register button clicked
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailRegister.getText().toString();
                String fname = fnameRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                String confirmPassword = confirmPasswordRegister.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX_CHECK)) {
                    emailRegister.setError("Invalid Email");
                    return;
                }
                if (fname.isEmpty() || fname.equals(" ")) {
                    fnameRegister.setError("Required");
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    passwordRegister.setError("Invalid Password");
                    return;
                }
                if (!password.equals(confirmPassword) ) {
                    confirmPasswordRegister.setError("Invalid Password");
                    return;
                }
                registerAccount(email,password, fname);
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
    }


    private void renderView(View view) {
        fnameRegister = view.findViewById(R.id.fnameRegister);
        passwordRegister = view.findViewById(R.id.passwordRegister);
        confirmPasswordRegister = view.findViewById(R.id.confirmPasswordRegister);
        emailRegister = view.findViewById(R.id.emailRegister);
        registerButton = view.findViewById(R.id.registerButtonRegister);
        loadingProgressBar = view.findViewById(R.id.loadingBarRegister);
        backToLogin = view.findViewById(R.id.backToLoginRegister);
    }


    private void registerAccount(String email, String password, String fname) {

        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getTag(), "createUserWithEmail:success");

                            // Get ID from User
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Add User to database
                            assert currentUser != null;
                            User user = new User(currentUser.getUid(), fname, email);
                            addUser(user);
                            loadingProgressBar.setVisibility(View.GONE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(getTag(), "createUserWithEmail:failure", task.getException());
                            loadingProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUser(User user) {
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update UI
                            backToPrevious();
                        }
                    }
                });
    }

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}