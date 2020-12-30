package com.example.rmit_android_ass2.main.siteView.mySiteView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.siteView.GetLocationActivity;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class MySiteEditFragment extends Fragment {
    // Constant declaration
    private final String TAG = "MY_SITE_EDIT_FRAGMENT";
    private final String SITE_ID = "cleaningSiteId";
    private final int REQUEST_CODE = 200;

    // Google Firebase declaration
    private FirebaseFirestore db;

    // Android view declaration
    private EditText editName, editAddress, editLatitude,
            editLongitude, editDate, editStartTime, editEndTime;
    private Button editSiteButton, editSiteLocationButton;
    private ImageButton backButton;

    // Utils variable declaration
    private String cleaningSiteId;


    public MySiteEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get message cleaningSiteId from previous fragment
        if (getArguments() != null) {
            cleaningSiteId = getArguments().getString(SITE_ID);
        }
    }

    /*
     *   Setup onActivityResult to return intent date
     *   -> Get latitude, longitude
     *   -> Cast to Double
     *   -> Display lat, lng to UI
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Double latitude = (Double) data.getExtras().get("lat");
                Double longitude = (Double) data.getExtras().get("lng");
                editLatitude.setText(String.format("%s", latitude));
                editLongitude.setText(String.format("%s", longitude));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_edit, container, false);
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

        // Display current site detail to UI
        displayCurrentSite(cleaningSiteId);
    }

    private void onClickListener() {
        // Back button to return the previous fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        /*
         *   Edit button will display a dialog with a message
         *   to confirm if you want to update the site
         *   -> OK, trigger updateSite()
         *   -> Cancel, cancel dialog
         */
        editSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Confirm Update")
                        .setMessage("Do you want to update this site?")
                        .setMessage("All information would be edited!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateSite(cleaningSiteId);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });

        // Edit site location will start new map activity to get location with REQUEST_CODE
        editSiteLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GetLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Edit date will popup a DatePicker dialog to set Date
        // When finish, set the date to the UI
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.N)
            // Access Date Dialog when click the EditText
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity());
                datePickerDialog.show();
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editDate.setText(String.format("%d/%d/%d", dayOfMonth, month, year));
                    }
                });
            }
        });

        // Edit start time will popup a TimePicker dialog to set time
        // When finish, set the time to the UI
        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        editStartTime.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }
        });

        // Edit end time will popup a TimePicker dialog to set time
        // When finish, set the time to the UI
        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        editEndTime.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }
        });
    }

    /**
     * Function to initiate all the view with the right id
     *
     * @param view view get from UI
     */
    private void renderView(View view) {
        editName = view.findViewById(R.id.siteNameSiteEdit);
        editAddress = view.findViewById(R.id.siteAddressSiteEdit);
        editLatitude = view.findViewById(R.id.siteLatitudeSiteEdit);
        editLongitude = view.findViewById(R.id.siteLongitudeSiteEdit);
        editDate = view.findViewById(R.id.siteDateSiteEdit);
        editStartTime = view.findViewById(R.id.siteStartTimeSiteEdit);
        editEndTime = view.findViewById(R.id.siteEndTimeSiteEdit);

        editSiteButton = view.findViewById(R.id.editSiteSiteEdit);
        backButton = view.findViewById(R.id.backButtonToolbarSiteEdit);
        editSiteLocationButton = view.findViewById(R.id.siteLocationSiteEdit);
    }

    /**
     * Function to get current site detail to display to UI
     * if Success, set textView for siteName, siteAddress,
     * siteLat, siteLng, siteDate, siteStartTime, siteEndTime
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
    @SuppressLint("SimpleDateFormat")
    private void displayCurrentSite(String cleaningSiteId) {
        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                Log.d(TAG, "Site name: " + cleaningSite.getName());

                                editName.setText(cleaningSite.getName());
                                editAddress.setText(cleaningSite.getAddress());
                                editLatitude.setText(String.valueOf(cleaningSite.getLat()));
                                editLongitude.setText(String.valueOf(cleaningSite.getLng()));

                                // Format Date to display
                                if (cleaningSite.getDate() != null) {
                                    Date dateFormat = cleaningSite.getDate().toDate();
                                    String siteDateFormat = new SimpleDateFormat("dd/MM/yyyy").format(dateFormat);
                                    editDate.setText(siteDateFormat);
                                }

                                editStartTime.setText(cleaningSite.getStartTime());
                                editEndTime.setText(cleaningSite.getEndTime());

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Get failed with ", task.getException());
                        }
                    }
                });
    }

    /**
     * Function to update new site details to Firebase
     * if Success, finish the activity
     * if Failure, display Log debug
     *
     * @param cleaningSiteId cleaning siteId to get document
     */
    private void updateSite(String cleaningSiteId) {
        String siteName = editName.getText().toString();
        String siteAddress = editAddress.getText().toString();
        Double latitude = Double.valueOf(editLatitude.getText().toString());
        Double longitude = Double.valueOf(editLongitude.getText().toString());

        // Set all detail to a HashMap to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", siteName);
        updates.put("address", siteAddress);
        updates.put("lat", latitude);
        updates.put("lng", longitude);
        updates.put("timestamp", FieldValue.serverTimestamp());

        db.collection("cleaningSites")
                .document(cleaningSiteId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        requireActivity().finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }
}