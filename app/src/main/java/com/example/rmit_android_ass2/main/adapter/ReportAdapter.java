package com.example.rmit_android_ass2.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.example.rmit_android_ass2.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private final ArrayList<Report> reports;

    public ReportAdapter(ArrayList<Report> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.reportSiteName.setText(report.getName());
        holder.reportSiteFollower.setText(String.valueOf(report.getFollower()));
        holder.reportSiteAmount.setText(String.valueOf(report.getAmount()));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView reportSiteName;
        private final TextView reportSiteFollower;
        private final TextView reportSiteAmount;

        public ViewHolder(@NonNull View view) {
            super(view);
            reportSiteName = view.findViewById(R.id.reportSiteNameListView);
            reportSiteFollower = view.findViewById(R.id.reportFollowerListView);
            reportSiteAmount = view.findViewById(R.id.reportAmountListView);
        }
    }
}
