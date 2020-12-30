package com.example.rmit_android_ass2.model;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class SiteSuggestion implements SearchSuggestion {
    private String siteName;
    private String cleaningSiteId;
    private boolean isHistory = false;

    public SiteSuggestion() {
    }

    public SiteSuggestion(String suggestion, String cleaningSiteId) {
        this.siteName = suggestion.toLowerCase();
        this.cleaningSiteId = cleaningSiteId;
    }

    public SiteSuggestion(Parcel source) {
        this.siteName = source.readString();
        this.cleaningSiteId = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    @Override
    public String getBody() {
        return siteName;
    }

    public String getCleaningSiteId() {
        return cleaningSiteId;
    }

    public static final Creator<SiteSuggestion> CREATOR = new Creator<SiteSuggestion>() {
        @Override
        public SiteSuggestion createFromParcel(Parcel in) {
            return new SiteSuggestion(in);
        }

        @Override
        public SiteSuggestion[] newArray(int size) {
            return new SiteSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(siteName);
        dest.writeString(cleaningSiteId);
        dest.writeInt(isHistory ? 1 : 0);
    }
}
