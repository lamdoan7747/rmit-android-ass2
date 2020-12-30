package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySiteInsertDataFragment extends Fragment {

    // Constant declaration
    private final String TAG = "MY_SITE_EDIT_FRAGMENT";
    private final String SITE_ID = "cleaningSiteId";

    // Google Firebase declaration
    private FirebaseFirestore db;

    // Android view declaration
    private EditText inputAmount, inputDate;
    private Button saveDataCollectionButton;
    private ImageButton backButton;

    // Utils variable declaration
    private String cleaningSiteId;


    public MySiteInsertDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get message cleaningSiteId from previous fragment
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

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();

        // Initiate view for the activity
        renderView(requireView());

        // Set all events on touchable
        onClickListener();
    }

    private void onClickListener() {
        // Back button to return the previous fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });

        // Save data button to trigger saveDataCollection() function
        saveDataCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataCollection(cleaningSiteId);
            }
        });
    }

    /**
     * Function to initiate all the view with the right id
     *
     * @param view view get from UI
     */
    private void renderView(View view) {
        inputAmount = view.findViewById(R.id.inputAmountCollection);
        inputDate = view.findViewById(R.id.inputDateCollection);
        saveDataCollectionButton = view.findViewById(R.id.saveDataCollection);
        backButton = view.findViewById(R.id.backButtonToolbarMySiteInsertData);
    }

    /**
     * Function to save collection data to Firebase
     * if Success, trigger updateAmount() funciton, then return to the previous fragment
     * if Failure, display Log debug
     *
     * @param cleaningSiteId site id to get document
     */
    private void saveDataCollection(String cleaningSiteId) {
        // Set value for data collection with new timestamp and amount of garbage
        double amount = Double.parseDouble(inputAmount.getText().toString());
        CleaningResult results = new CleaningResult(amount);

        // Add new data collection
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .collection("results")
                .add(results)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Add success: " + documentReference.getId());
                        updateAmount(cleaningSiteId, amount);
                        backToPrevious();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure: " + e.toString());
                    }
                });
    }

    /**
     * Function to update totalAmount in "cleaningSites" collection
     *
     * @param cleaningSiteId cleaning siteId to get document
     * @param amount         amount of collection getting from UI
     */
    private void updateAmount(String cleaningSiteId, double amount) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
                DocumentSnapshot document = transaction.get(docRef);
                double addAmount = document.getDouble("totalAmount") + amount;
                transaction.update(docRef, "totalAmount", addAmount);
                return null;
            }
        });
    }

    // Function to return previous fragment
    private void backToPrevious() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}