package com.example.rmit_android_ass2.main.siteView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningResult;
import com.example.rmit_android_ass2.model.CleaningSite;

import java.util.ArrayList;

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
            viewSite = View.inflate(viewGroup.getContext(), R.layout.fragment_site_list_view,null);
        } else viewSite = view;

        CleaningResult cleaningResult = (CleaningResult) getItem(position);
        ((TextView) viewSite.findViewById(R.id.resultDateListView)).setText(String.format("%s",cleaningResult.getDateCleaning()));
        ((TextView) viewSite.findViewById(R.id.resultAmountListView)).setText(String.format("%s",cleaningResult.getAmount()));

        return viewSite;
    }
}
