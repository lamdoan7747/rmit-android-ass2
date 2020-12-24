package com.example.rmit_android_ass2.main.listView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;

import java.util.List;

/**
 * {@link RecyclerView.Adapter}
 * TODO: Replace the implementation with code for your data type.
 */
public class CleaningSiteRecyclerViewAdapter extends RecyclerView.Adapter<CleaningSiteRecyclerViewAdapter.ViewHolder> {

    private final List<CleaningSite> cleaningSiteList;
    private Context context;

    public CleaningSiteRecyclerViewAdapter(List<CleaningSite> cleaningSiteList, Context context) {
        this.cleaningSiteList = cleaningSiteList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_site_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CleaningSite cleaningSite = cleaningSiteList.get(position);
        holder.siteName.setText(cleaningSite.getName());
        holder.addressName.setText(cleaningSite.getAddress());
    }

    @Override
    public int getItemCount() {
        return cleaningSiteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView siteName;
        public final TextView addressName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            siteName = mView.findViewById(R.id.siteNameListView);
            addressName = mView.findViewById(R.id.addressListView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + siteName.getText() + "'";
        }
    }
}