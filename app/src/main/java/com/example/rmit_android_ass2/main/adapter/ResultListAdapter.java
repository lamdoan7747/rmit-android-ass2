package com.example.rmit_android_ass2.main.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResultListAdapter extends BaseAdapter {
    private ArrayList<CleaningResult> cleaningResults;

    public ResultListAdapter(ArrayList<CleaningResult> cleaningResults) {
        this.cleaningResults = cleaningResults;
    }

    @Override
    public int getCount() {
        return cleaningResults.size();
    }

    @Override
    public Object getItem(int position) {
        return cleaningResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewSite;
        if (view == null) {
            viewSite = View.inflate(viewGroup.getContext(), R.layout.list_view_result,null);
        } else viewSite = view;

        CleaningResult cleaningResult = (CleaningResult) getItem(position);

        if (cleaningResult.getDateCleaning() != null){
            Date dateFormat = cleaningResult.getDateCleaning().toDate();
            @SuppressLint("SimpleDateFormat")
            String simpleDateFormat = new SimpleDateFormat("EEE, dd MMM, yyyy").format(dateFormat);

            ((TextView) viewSite.findViewById(R.id.resultDateListView)).setText(simpleDateFormat);
        }

        ((TextView) viewSite.findViewById(R.id.resultAmountListView)).setText(String.format("%s",cleaningResult.getAmount()));

        return viewSite;
    }
}
