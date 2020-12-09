package com.example.rmit_android_ass2.auth;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editFname, editPassword,
            editEmail, editConfirmPassword;
    private Button register;
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        init(view);
        onClickListener();

    }

    private void onClickListener() {
        // On register button clicked
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String fname = editFname.getText().toString();
                String password = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX_CHECK)) {
                    editEmail.setError("Invalid Email");
                    return;
                }
                if (fname.isEmpty() || fname.equals(" ")) {
                    editFname.setError("Required");
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    editPassword.setError("Invalid Password");
                    return;
                }
                if (!password.equals(confirmPassword) ) {
                    editConfirmPassword.setError("Invalid Password");
                    return;
                }
                registerAccount(email,password, fname);
            }
        });
    }

    private void init(View view) {
        editFname = view.findViewById(R.id.rFname);
        editPassword = view.findViewById(R.id.rPassword);
        editConfirmPassword = view.findViewById(R.id.rConfirmPassword);
        editEmail = view.findViewById(R.id.rEmail);
        register = view.findViewById(R.id.rRegister);
        loadingProgressBar = view.findViewById(R.id.rLoading);
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        ft.replace(R.id.authContainer, fragment);
        ft.addToBackStack(null);
        ft.commit();
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
                            loadFragment(new LoginFragment());
                        }
                    }
                });
    }
}