package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningResult;
import com.example.rmit_android_ass2.model.User;
import com.example.rmit_android_ass2.notification.NotificationHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySiteInsertDataFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SITE_ID = "cleaningSiteId";

    private String cleaningSiteId;

    private FirebaseFirestore db;

    private EditText inputAmount, inputDate;
    private Button saveDataCollectionButton;
    private ImageButton backButton;

    public MySiteInsertDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cleaningSiteId = getArguments().getString(SITE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_site_insert_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        renderView(requireView());
        onClickListener();
    }

    private void onClickListener() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });

        saveDataCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataCollection(cleaningSiteId);
            }
        });
    }

    private void renderView(View view){
        inputAmount = view.findViewById(R.id.inputAmountCollection);
        inputDate = view.findViewById(R.id.inputDateCollection);
        saveDataCollectionButton = view.findViewById(R.id.saveDataCollection);
        backButton = view.findViewById(R.id.backButtonToolbarMySiteInsertData);
    }

    private void saveDataCollection(String cleaningSiteId) {
        // Set value for data collection with new timestamp and amount of garbage
        Double data = Double.parseDouble(inputAmount.getText().toString());

        CleaningResult results = new CleaningResult(data);

        // Add new data collection
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("results")
                .add(results)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(),"Add success: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        backToPrevious();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Failure: " + e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void backToPrevious() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}