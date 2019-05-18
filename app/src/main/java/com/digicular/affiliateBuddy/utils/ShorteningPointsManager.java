package com.digicular.affiliateBuddy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.digicular.affiliateBuddy.data.AppContract;

import java.util.prefs.Preferences;

/**
 * Created by AsusPC
 * Website - SusheelKaram.com
 */
public class ShorteningPointsManager {
    SharedPreferences preferences;
    Context mContext;

    ShorteningPointsManager(Context context){
        mContext = context;
        Activity activity = (Activity) context;
        preferences = activity.getSharedPreferences(AppContract.PREFS_APP_SETTINGS, Context.MODE_PRIVATE);
    }

    protected void initialStart(){
        Boolean isFirstStart = preferences.getBoolean(AppContract.PREF_IS_FIRST_STARTUP, true);

        if (isFirstStart){
            Editor editor = preferences.edit();
            editor.putInt(AppContract.PREF_SHORTLINK_POINTS, AppContract.POINTS_INITIAL);
            editor.putBoolean(AppContract.PREF_IS_FIRST_STARTUP, false);
            editor.apply();
        }
    }

    protected void addRewards(){
        int currentPoints = preferences.getInt(AppContract.PREF_SHORTLINK_POINTS, 0);
        if (currentPoints < AppContract.POINTS_MAX) {
            addRewardPointsOf(AppContract.POINTS_REWARD);
        }
        else {
            Toast.makeText(mContext,"Can't add more points, max Reward Points reached.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void addRewardPointsOf(int points){
        int currentPoints = preferences.getInt(AppContract.PREF_SHORTLINK_POINTS,0);
        Editor editor = preferences.edit();
        int totalPoints = currentPoints + points;
        editor.putInt(AppContract.PREF_SHORTLINK_POINTS,totalPoints);
        editor.apply();
    }
}
