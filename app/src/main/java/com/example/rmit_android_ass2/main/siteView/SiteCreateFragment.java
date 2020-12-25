package com.example.rmit_android_ass2.main.siteView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SiteCreateFragment extends Fragment {
    // Request code for startActivityForResult() to get location
    private static int REQUEST_CODE = 200;

    // Firestore declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // View declaration
    private EditText editName, editAddress, editLatitude,
            editLongitude, editDate, editStartTime, editEndTime;

    private Button createSiteButton, addLocationButton;
    private ImageButton backButtonToolbar;


    public SiteCreateFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_site_create, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null){
                Double latitude = (Double) data.getExtras().get("lat");
                Double longitude = (Double) data.getExtras().get("lng");
                editLatitude.setText(String.format("%s", latitude));
                editLongitude.setText(String.format("%s",longitude));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        renderView(getView());
        clickListener();
    }

    @SuppressLint("SimpleDateFormat")
    private void clickListener(){
        createSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get all value in the form
                String siteName = editName.getText().toString();
                String siteAddress = editAddress.getText().toString();
                Double latitude = Double.valueOf(editLatitude.getText().toString());
                Double longitude = Double.valueOf(editLongitude.getText().toString());
                String siteDate = editDate.getText().toString();
                String startTime = editStartTime.getText().toString();
                String endTime = editEndTime.getText().toString();

                try {
                    Date siteDateFormat = new SimpleDateFormat("dd/MM/yyyy").parse(siteDate);

                    // Declare all object function of cleaning site
                    CleaningSite cleaningSite = new CleaningSite();
                    cleaningSite.setName(siteName);
                    cleaningSite.setAddress(siteAddress);
                    cleaningSite.setDate(siteDateFormat);
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

        backButtonToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPrevious();
            }
        });

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GetLocationActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

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
                        showToast(timePicker.toString());
                        editStartTime.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }
        });

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
                },hour,minute,android.text.format.DateFormat.is24HourFormat(getContext()));
                timePickerDialog.show();
            }
        });
    }

    private void renderView(View view){
        editName = (EditText) view.findViewById(R.id.siteNameSiteCreate);
        editAddress = (EditText) view.findViewById(R.id.siteAddressSiteCreate);
        editLatitude = (EditText) view.findViewById(R.id.siteLatitudeSiteCreate);
        editLongitude = (EditText) view.findViewById(R.id.siteLongitudeSiteCreate);
        editDate = (EditText) view.findViewById(R.id.siteDateSiteCreate);
        editStartTime = (EditText) view.findViewById(R.id.siteStartTimeSiteCreate);
        editEndTime = (EditText) view.findViewById(R.id.siteEndTimeSiteCreate);

        createSiteButton = (Button) view.findViewById(R.id.createSiteSiteCreate);
        backButtonToolbar = (ImageButton) view.findViewById(R.id.backButtonToolbarSiteCreate);
        addLocationButton = (Button) view.findViewById(R.id.addLocationSiteCreate);

    }

    private void backToPrevious() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void createSite(CleaningSite cleaningSite) {
        currentUser = mAuth.getCurrentUser();
        cleaningSite.setOwner(currentUser.getUid());

        db.collection("cleaningSites")
                .add(cleaningSite)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(),"Add success: " + documentReference.getId(), Toast.LENGTH_LONG).show();
                        backToPrevious();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Failure: " + e.toString(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}