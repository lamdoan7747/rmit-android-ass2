package com.example.rmit_android_ass2;

import android.content.Intent;
import android.os.Bundle;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.rmit_android_ass2.model.SiteSuggestion;
import com.example.rmit_android_ass2.model.CleaningSite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    // Constant declaration
    private final String TAG = "SEARCH_ACTIVITY";

    // Android view declaration
    private FloatingSearchView searchView;

    // Utils variable declaration
    private String mLastQuery = "";
    private final int limit = 3;
    private String cleaningSiteId;

    // Google Firebase declaration
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*
         *   Represents a Cloud Firestore database and
         *   is the entry point for all Cloud Firestore
         *   operations.
         */
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // When activity create, set focus to the search view
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setSearchFocused(true);

        // Action when use the searchView
        setupFloatingSearch();

        // Display recyclerView for sites (Out scope)
        setupResultsList();
    }

    // Display recyclerView for sites (Out scope)
    private void setupResultsList() {
    }

    private void setupFloatingSearch() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            /**
             * Called when the query has changed. It will
             * be invoked when one or more characters in the
             * query was changed.
             *
             * @param oldQuery the previous query
             * @param newQuery the new query
             */
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mLastQuery = "";
                    searchView.clearSuggestions();
                } else {
                    /*  this shows the top left circular progress
                     *  you can call it where ever you want, but
                     *  it makes sense to do it when loading something in
                     *  the background.
                     */
                    searchView.showProgress();

                    // this will swap the data and
                    // render the collapse/expand animations as necessary
                    getSites(new OnSiteCallBack() {
                        @Override
                        public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                            List<SiteSuggestion> suggestionList = new ArrayList<>();
                            for (SiteSuggestion siteSuggestion : siteSuggestions) {
                                if (siteSuggestion.getBody().toLowerCase().contains(newQuery.toLowerCase())) {
                                    suggestionList.add(siteSuggestion);
                                    if (suggestionList.size() == limit) {
                                        break;
                                    }
                                }
                            }
                            mLastQuery = newQuery;
                            searchView.swapSuggestions(suggestionList);
                        }
                    });

                    // let the users know that the background
                    // process has completed
                    searchView.hideProgress();
                    Log.d(TAG, "onSearchTextChanged(): " + newQuery);
                }
            }
        });


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            /**
             * Called when a suggestion was clicked indicating
             * that the current search has completed.
             *
             * @param searchSuggestion siteSuggestion
             */
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                // Cast to SiteSuggestion model object
                SiteSuggestion siteSuggestion = (SiteSuggestion) searchSuggestion;
                cleaningSiteId = siteSuggestion.getCleaningSiteId();

                // Start new activity when clicked
                Intent intent = new Intent(SearchActivity.this, SiteDetailActivity.class);
                intent.putExtra("cleaningSiteId", cleaningSiteId);
                startActivity(intent);

                Log.d(TAG, "onSuggestionClicked(): " + searchSuggestion.getBody());
                // When return back to the Search Activity, display query on searchText
                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String currentQuery) {
                mLastQuery = currentQuery;
                Log.d(TAG, "onSearchAction()");
            }
        });


        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            /**
             * Called when the search bar has gained focus
             * and listeners are now active.
             */
            @Override
            public void onFocus() {
                searchView.showProgress();
                //show suggestions when search bar gains focus (typically history suggestions)
                //searchView.swapSuggestions(DataHelper.getHistory(SearchActivity.this, 3));
                getSites(new OnSiteCallBack() {
                    @Override
                    public void onCallBack(List<SiteSuggestion> siteSuggestions) {
                        List<SiteSuggestion> suggestionList = new ArrayList<>();
                        for (SiteSuggestion siteSuggestion : siteSuggestions) {
                            if (siteSuggestion.getBody().toLowerCase().contains(searchView.getQuery().toLowerCase())) {
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

            /**
             * Called when the search bar has lost focus
             * and listeners are no more active.
             */
            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                searchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                searchView.setSearchText(mLastQuery);
                Log.d(TAG, "onFocusCleared()");
            }
        });
    }

    /**
     * Function to get all sites display to UI listView except current user's sites
     * if Success, add all SiteSuggestion object to a list
     * by init from CleaningSite, then assign function onSiteCallBack
     * if Failure, display Log debug
     *
     * @param onSiteCallBack callBack to get list of sites
     */
    private void getSites(OnSiteCallBack onSiteCallBack) {
        currentUser = mAuth.getCurrentUser();

        List<SiteSuggestion> siteSuggestions = new ArrayList<>();
        db.collection("cleaningSites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                CleaningSite cleaningSite = document.toObject(CleaningSite.class);
                                if (!cleaningSite.getOwner().equals(currentUser.getUid())) {
                                    siteSuggestions.add(new SiteSuggestion(cleaningSite.getName(), cleaningSite.getId()));
                                }
                            }
                            onSiteCallBack.onCallBack(siteSuggestions);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Interface for implementing a listener to listen
     * to get list of siteSuggestion from getSites().
     */
    private interface OnSiteCallBack {
        void onCallBack(List<SiteSuggestion> siteSuggestions);
    }
}