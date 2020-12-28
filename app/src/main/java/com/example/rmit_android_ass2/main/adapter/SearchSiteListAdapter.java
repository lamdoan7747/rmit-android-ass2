package com.example.rmit_android_ass2.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.util.Util;
import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;

import java.util.ArrayList;
import java.util.List;

public class SearchSiteListAdapter extends RecyclerView.Adapter<SearchSiteListAdapter.ViewHolder> {
    private List<CleaningSite> cleaningSites = new ArrayList<>();

    private int mLastAnimatedItemPosition = -1;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClick(CleaningSite cleaningSite);
    }

    @NonNull
    @Override
    public SearchSiteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_site, parent, false);
        return new ViewHolder(view);
    }

    public void swapData(List<CleaningSite> mNewDataSet) {
        cleaningSites = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CleaningSite cleaningSite = cleaningSites.get(position);
        holder.siteName.setText(cleaningSite.getName());
        holder.siteAddress.setText(cleaningSite.getAddress());

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(cleaningSites.get(position));
                }
            });
        }
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        return cleaningSites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView siteName;
        public final TextView siteAddress;

        public ViewHolder(@NonNull View view) {
            super(view);
            siteName = (TextView) view.findViewById(R.id.siteNameListView);
            siteAddress = (TextView) view.findViewById(R.id.siteAddressListView);
        }
    }
}
