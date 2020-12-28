package com.example.rmit_android_ass2.main.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;

import java.util.ArrayList;

public class SiteListAdapter extends BaseAdapter {
    final ArrayList<CleaningSite> cleaningSites;

    public SiteListAdapter(ArrayList<CleaningSite> cleaningSites) {
        this.cleaningSites = cleaningSites;
    }

    @Override
    public int getCount() {
        return cleaningSites.size();
    }

    @Override
    public Object getItem(int position) {
        return cleaningSites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewSite;
        if (view == null) {
            viewSite = View.inflate(viewGroup.getContext(), R.layout.list_view_site,null);
        } else viewSite = view;

        CleaningSite cleaningSite = (CleaningSite) getItem(position);
        ((TextView) viewSite.findViewById(R.id.siteNameListView)).setText(String.format("%s",cleaningSite.getName()));
        ((TextView) viewSite.findViewById(R.id.addressListView)).setText(String.format("%s",cleaningSite.getAddress()));

        return viewSite;
    }
}
