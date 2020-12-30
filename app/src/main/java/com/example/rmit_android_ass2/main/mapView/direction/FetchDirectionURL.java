package com.example.rmit_android_ass2.main.mapView.direction;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("deprecation")
@SuppressLint("StaticFieldLeak")
public class FetchDirectionURL extends AsyncTask<String, Void, String> {
    // Constant declaration
    private final String TAG = "FETCH_DIRECTION_URL";

    Context mContext;
    String directionMode = "driving";
    PointsParser parserTask;
    TaskLoadedCallback taskLoadedCallback;

    public FetchDirectionURL(Context mContext, TaskLoadedCallback taskLoadedCallback) {
        this.mContext = mContext;
        this.taskLoadedCallback = taskLoadedCallback;
    }

    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String data = "";
        directionMode = strings[1];
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);
            Log.d(TAG, "Background task data " + data.toString());
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        parserTask = new PointsParser(mContext, directionMode, taskLoadedCallback);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d(TAG, "Downloaded URL: " + data);
            br.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
