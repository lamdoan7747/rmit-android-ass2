package com.example.rmit_android_ass2;

import android.content.Intent;
import android.os.Bundle;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.rmit_android_ass2.main.adapter.SearchSiteListAdapter;
import com.example.rmit_android_ass2.model.SiteSuggestion;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SEARCH_ACTIVITY";

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView searchView;

    private RecyclerView searchSiteList;
    private SearchSiteListAdapter searchSiteAdapter;

    private String mLastQuery = "";
    private String cleaningSiteId;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        //searchSiteList = (RecyclerView) findViewById(R.id.search_results_list);

        setupFloatingSearch();
        setupResultsList();
    }

    private void setupResultsList(){
    }

    private void setupFloatingSearch() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    searchView.showProgress();
                    //this will swap the data and
                    //render the collapse/expand animations as necessary
                    getSites(new OnSiteCallBack() {
                        @Override
                        public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                            final int limit = 5;
                            List<SiteSuggestion> suggestionList = new ArrayList<>();
                            for(SiteSuggestion siteSuggestion:siteSuggestions){
                                if(siteSuggestion.getBody().toLowerCase().contains(newQuery.toLowerCase())){
                                    suggestionList.add(siteSuggestion);
                                    if (suggestionList.size() == limit) {
                                        break;
                                    }
                                }
                            }
                            searchView.swapSuggestions(suggestionList);

                        }
                    });
                    //let the users know that the background
                    //process has completed
                    searchView.hideProgress();
                    Log.d(TAG, "onSearchTextChanged()");
                }
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                SiteSuggestion siteSuggestion = (SiteSuggestion) searchSuggestion;
                cleaningSiteId = siteSuggestion.getCleaningSiteId();


                Intent intent = new Intent(SearchActivity.this, SiteDetailActivity.class);
                intent.putExtra("cleaningSiteId",cleaningSiteId);
                startActivity(intent);

                Log.d(TAG, "onSuggestionClicked()");
                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                Log.d(TAG, "onSearchAction()");
            }
        });


        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                searchView.showProgress();
                //show suggestions when search bar gains focus (typically history suggestions)
                //searchView.swapSuggestions(DataHelper.getHistory(SearchActivity.this, 3));
                getSites(new OnSiteCallBack() {
                    @Override
                    public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                        final int limit = 5;
                        List<SiteSuggestion> suggestionList=new ArrayList<>();
                        for(SiteSuggestion siteSuggestion:siteSuggestions){
                            if(siteSuggestion.getBody().toLowerCase().contains(searchView.getQuery().toLowerCase())){
                                suggestionList.add(siteSuggestion);
                                if (suggestionList.size() == limit) {
                                    break;
                                }
                            }
                        }
                        searchView.swapSuggestions(suggestionList);
                    }
                });
                searchView.hideProgress();
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                searchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                searchView.setSearchText(mLastQuery);

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                Log.d(TAG, "onActionMenuItemSelected()");
            }
        });
    }

    private void getSites(OnSiteCallBack onSiteCallBack) {
        List<CleaningSite> cleaningSites = new ArrayList<>();
        List<SiteSuggestion> siteSuggestions = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);

                                siteSuggestions.add(new SiteSuggestion(cleaningSite.getName(), cleaningSite.get_id()));
                            }
                            onSiteCallBack.onCallBack(siteSuggestions);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private interface OnSiteCallBack{
        void onCallBack(List<SiteSuggestion> siteSuggestions);
    }
}