package com.example.rmit_android_ass2.main.siteView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class SiteCreateFragment extends Fragment {
    // Constant declaration
    private final String TAG = "SITE_CREATE_FRAGMENT";
    private int REQUEST_CODE = 200;

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Android view declaration
    private EditText editName, editAddress, editLatitude,
            editLongitude, editDate, editStartTime, editEndTime;
    private Button createSiteButton, addLocationButton;
    private ImageButton backButton;


    public SiteCreateFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_site_create, container, false);
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
        mAuth = FirebaseAuth.getInstance();

        // Initiate view for the activity
        renderView(requireView());

        // Set all events on touchable
        onClickListener();
    }

    @SuppressLint("SimpleDateFormat")
    private void onClickListener() {
        // Back button to return the previous fragment
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPrevious();
            }
        });

        /*
         *   Create site button will get all detail from UI update
         *   Check if all form is updated
         *   Initiate CleaningSite object to set all detail
         *   Then trigger createSite() function
         * */
        createSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get all value in the form
                String siteName = editName.getText().toString();
                String siteAddress = editAddress.getText().toString();
                String latitudeString = editLatitude.getText().toString();
                String longtitudeString = editLongitude.getText().toString();
                String siteDate = editDate.getText().toString();
                String startTime = editStartTime.getText().toString();
                String endTime = editEndTime.getText().toString();

                if (TextUtils.isEmpty(siteName)) {
                    editName.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(latitudeString)) {
                    editLatitude.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(longtitudeString)) {
                    editLongitude.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(siteAddress)) {
                    editAddress.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(startTime)) {
                    editStartTime.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(endTime)) {
                    editEndTime.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(siteDate)) {
                    editDate.setError("Required!");
                    return;
                }

                Double latitude = Double.valueOf(latitudeString);
                Double longitude = Double.valueOf(longtitudeString);
                try {
                    Date siteDateFormat = new SimpleDateFormat("dd/MM/yyyy").parse(siteDate);
                    Timestamp timestampDateFormat = new Timestamp(siteDateFormat);

                    // Declare all object function of cleaning site
                    CleaningSite cleaningSite = new CleaningSite();
                    cleaningSite.setName(siteName);
                    cleaningSite.setAddress(siteAddress);
                    cleaningSite.setDate(timestampDateFormat);
                    cleaningSite.setStartTime(startTime);
                    cleaningSite.setEndTime(endTime);
                    cleaningSite.setLat(latitude);
                    cleaningSite.setLng(longitude);

                    // Create site function
                    createSite(cleaningSite);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // Edit site location will start new map activity to get location with REQUEST_CODE
        addLocationButton.setOnClickListener(new View.OnClickListener() {
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
                        editDate.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
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
                        if (minute == 0) {
                            editStartTime.setText(String.format("%d:00", hourOfDay));
                        } else if (minute < 10) {
                            editStartTime.setText(String.format("%d:0%d", hourOfDay, minute));
                        } else {
                            editStartTime.setText(String.format("%d:%d", hourOfDay, minute));
                        }
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
                        if (minute == 0) {
                            editEndTime.setText(String.format("%d:00", hourOfDay));
                        } else if (minute < 10) {
                            editEndTime.setText(String.format("%d:0%d", hourOfDay, minute));
                        } else {
                            editEndTime.setText(String.format("%d:%d", hourOfDay, minute));
                        }
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
        editName = view.findViewById(R.id.siteNameSiteCreate);
        editAddress = view.findViewById(R.id.siteAddressSiteCreate);
        editLatitude = view.findViewById(R.id.siteLatitudeSiteCreate);
        editLongitude = view.findViewById(R.id.siteLongitudeSiteCreate);
        editDate = view.findViewById(R.id.siteDateSiteCreate);
        editStartTime = view.findViewById(R.id.siteStartTimeSiteCreate);
        editEndTime = view.findViewById(R.id.siteEndTimeSiteCreate);

        createSiteButton = view.findViewById(R.id.createSiteSiteCreate);
        addLocationButton = view.findViewById(R.id.addLocationSiteCreate);
        backButton = view.findViewById(R.id.backButtonToolbarSiteCreate);

    }

    /**
     * Function to create new site to Firebase
     * if Success, return to previous fragment
     * if Failure, display Log debug
     */
    private void createSite(CleaningSite cleaningSite) {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            cleaningSite.setOwner(currentUser.getUid());

        db.collection("cleaningSites")
                .add(cleaningSite)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot successfully added!");
                        backToPrevious();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void backToPrevious() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}