package com.example.rmit_android_ass2.main.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.main.adapter.ReportAdapter;
import com.example.rmit_android_ass2.model.CleaningResult;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {

    private final String TAG = "REPORT_FRAGMENT";

    private FirebaseFirestore db;

    private RecyclerView reportListView;
    private ArrayList<Report> reports;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_report, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        reports = new ArrayList<>();

        updateReport(new OnReportCallBack() {
            @Override
            public void onCallBack(List<Report> report) {
                reportListView = (RecyclerView) view.findViewById(R.id.listReport);
                ReportAdapter reportAdapter = new ReportAdapter(reports);
                if (reportAdapter == null) {
                    Log.d(TAG,"CANNOT GET ADAPTER");
                }
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                reportAdapter.notifyDataSetChanged();
                reportListView.setAdapter(reportAdapter);
                reportListView.setLayoutManager(layoutManager);
                reportListView.setHasFixedSize(true);
            }
        });

    }

    private void updateReport(OnReportCallBack onReportCallBack){
        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document: task.getResult()){
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                String siteName = cleaningSite.getName();
                                long follower = cleaningSite.getFollower();
                                double totalAmount = cleaningSite.getTotalAmount();
                                Report report = new Report(siteName, follower, totalAmount);

                                reports.add(report);
                            }
                            onReportCallBack.onCallBack(reports);
                            Log.d(TAG,"Size: " + reports.size());
                        }
                    }
                });
    }

    private interface OnReportCallBack {
        void onCallBack(List<Report> report);
    }
}