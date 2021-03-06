package com.example.rmit_android_ass2.main.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.User;

import java.util.ArrayList;

public class FollowerListAdapter extends BaseAdapter {
    private final ArrayList<User> followers;

    public FollowerListAdapter(ArrayList<User> followers) {
        this.followers = followers;
    }

    @Override
    public int getCount() {
        return followers.size();
    }

    @Override
    public Object getItem(int position) {
        return followers.get(position);
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

        User follower = (User) getItem(position);
        ((TextView) viewSite.findViewById(R.id.siteNameListView)).setText(String.format("%s",follower.getName()));
        ((TextView) viewSite.findViewById(R.id.siteAddressListView)).setText(String.format("%s",follower.getEmail()));

        return viewSite;
    }
}
