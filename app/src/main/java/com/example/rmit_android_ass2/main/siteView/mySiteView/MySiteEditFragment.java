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

    private static String TAG = "MY_SITE_EDIT_FRAGMENT";
    private static int REQUEST_CODE = 200;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText editName, editAddress, editLatitude,
            editLongitude, editDate, editStartTime, editEndTime;
    private Button editSiteButton, editSiteLocationButton;
    private ImageButton editSiteBackButton;

    private String cleaningSiteId;


    public MySiteEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            cleaningSiteId = getArguments().getString("cleaningSiteId");
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_edit, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        renderView(requireView());
        onClickListener();

        // Display current site detail
        displayCurrentSite(cleaningSiteId);
    }

    private void onClickListener() {
        editSiteBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPrevious();
            }
        });

        editSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Confirm Update")
                        .setMessage("Do you want to update this site?")
                        .setMessage("All information included followers would be edited!")
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

        editSiteLocationButton.setOnClickListener(new View.OnClickListener() {
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

    private void renderView(View view) {
        editName =view.findViewById(R.id.siteNameSiteEdit);
        editAddress = view.findViewById(R.id.siteAddressSiteEdit);
        editLatitude = view.findViewById(R.id.siteLatitudeSiteEdit);
        editLongitude = view.findViewById(R.id.siteLongitudeSiteEdit);
        editDate = view.findViewById(R.id.siteDateSiteEdit);
        editStartTime = view.findViewById(R.id.siteStartTimeSiteEdit);
        editEndTime = view.findViewById(R.id.siteEndTimeSiteEdit);

        editSiteButton =view.findViewById(R.id.editSiteSiteEdit);
        editSiteBackButton = view.findViewById(R.id.backButtonToolbarSiteEdit);
        editSiteLocationButton = view.findViewById(R.id.siteLocationSiteEdit);
    }


    private void displayCurrentSite(String cleaningSiteId) {
        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        docRef.get()
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

                                if (cleaningSite.getDate() != null) {
                                    Date dateFormat = cleaningSite.getDate().toDate();
                                    @SuppressLint("SimpleDateFormat")
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

    private void updateSite(String cleaningSiteId) {
        String siteName = editName.getText().toString();
        String siteAddress = editAddress.getText().toString();
        Double latitude = Double.valueOf(editLatitude.getText().toString());
        Double longitude = Double.valueOf(editLongitude.getText().toString());

        DocumentReference docRef = db.collection("cleaningSites").document(cleaningSiteId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name",siteName);
        updates.put("address",siteAddress);
        updates.put("lat",latitude);
        updates.put("lng",longitude);
        updates.put("timestamp", FieldValue.serverTimestamp());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UPDATE SITE", "DocumentSnapshot successfully updated!");
                        requireActivity().finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UPDATE SITE", "Error updating document", e);
                    }
                });

    }

    private void backToPrevious() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}