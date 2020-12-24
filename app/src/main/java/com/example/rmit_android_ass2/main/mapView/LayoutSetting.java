package com.example.rmit_android_ass2.main.mapView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LayoutSetting {
    private static final String TAG ="LayoutSetting";

    public static void transparentStatusBar(Activity activity, boolean isTransparent, boolean fullscreen) {
        if (isTransparent){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                // FOR TRANSPARENT NAVIGATION BAR
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                Log.d(TAG,"Setting Color Transparent "+Color.TRANSPARENT);
            }
        }

        if (fullscreen){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }
}
