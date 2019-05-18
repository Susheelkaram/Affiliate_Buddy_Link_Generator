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

    public ShorteningPointsManager(Context context){
        mContext = context;
        Activity activity = (Activity) context;
        preferences = activity.getSharedPreferences(AppContract.PREFS_APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public void initialStart(){
        Boolean isFirstStart = preferences.getBoolean(AppContract.PREF_IS_FIRST_STARTUP, true);

        if (isFirstStart){
            Editor editor = preferences.edit();
            editor.putInt(AppContract.PREF_SHORTLINK_POINTS, AppContract.POINTS_INITIAL);
            editor.putBoolean(AppContract.PREF_IS_FIRST_STARTUP, false);
            editor.apply();
        }
    }

    public void addRewards(){
        int currentPoints = getCurrentPoints();
        if (currentPoints < AppContract.POINTS_MAX) {
            addRewardPointsOf(AppContract.POINTS_REWARD);
            Toast.makeText(mContext, "Congratulations! You won 5 Short Link points", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mContext,"Can't add more points, max Reward Points reached.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addRewardPointsOf(int points){
        int currentPoints = getCurrentPoints();
        Editor editor = preferences.edit();

        int totalPoints = currentPoints + points;
        totalPoints = (totalPoints <= AppContract.POINTS_MAX) ? totalPoints : AppContract.POINTS_MAX;

        editor.putInt(AppContract.PREF_SHORTLINK_POINTS,totalPoints);
        editor.apply();
    }

    public int getCurrentPoints(){
        return preferences.getInt(AppContract.PREF_SHORTLINK_POINTS,0);
    }
    public boolean isMaxPointsReached(){
        return getCurrentPoints() >= AppContract.POINTS_MAX;
    }
}
