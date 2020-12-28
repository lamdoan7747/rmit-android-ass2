package com.example.rmit_android_ass2.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        @SuppressLint("InflateParams")
        View view = context.getLayoutInflater().inflate(R.layout.info_window_google_map_custom, null);

        TextView infoTitle = (TextView) view.findViewById(R.id.infoTitle);
        TextView infoAddress = (TextView) view.findViewById(R.id.infoAddress);

        infoTitle.setText(marker.getTitle());
        infoAddress.setText(marker.getSnippet());

        return view;
    }
}
