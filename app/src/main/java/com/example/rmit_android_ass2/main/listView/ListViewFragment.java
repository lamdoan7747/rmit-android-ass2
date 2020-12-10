package com.example.rmit_android_ass2.main.listView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rmit_android_ass2.R;
import com.example.rmit_android_ass2.model.CleaningSite;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ListViewFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView recyclerView;
    private CleaningSiteRecyclerViewAdapter adapter;
    private final List<CleaningSite> cleaningSiteList = new ArrayList<>();
    private int page = 1, limit = 10;
    private ProgressBar loadingBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list_list, container, false);

        //loadingBar = view.findViewById(R.id.contentProgressLoadBar);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            for (int i = 1; i <= 15; i++) {
                cleaningSiteList.add(new CleaningSite("Student "+i , "Something"));
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

            if (mColumnCount <=1) {
                recyclerView.setLayoutManager(linearLayoutManager);
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

            CleaningSiteRecyclerViewAdapter adapter = new CleaningSiteRecyclerViewAdapter(cleaningSiteList,context);

            recyclerView.setAdapter(adapter);

            // Get data
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //loadingBar.setVisibility(View.VISIBLE);

                            Toast.makeText(view.getContext(), "Loading More ...",
                                    Toast.LENGTH_SHORT).show();

                            List<CleaningSite> list = new ArrayList<>();
                            for (int i = 0; i <= 5; i++) {
                                list.add(new CleaningSite("Mới "+ i, "1988"));
                            }
                            cleaningSiteList.addAll(list);
                            adapter.notifyDataSetChanged();
                            //loadingBar.setVisibility(View.GONE);
                        }
                    },1000);
                }
            });

        }
        return view;
    }
}