package com.digicular.affiliateBuddy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digicular.affiliateBuddy.DisplayLinksHistory;
import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.R;
import com.digicular.affiliateBuddy.SettingsActivity;
import com.digicular.affiliateBuddy.data.AppContract;
import com.digicular.affiliateBuddy.staticActivities.AboutApp;
import com.digicular.affiliateBuddy.staticActivities.HowToUse;

/**
 * Created by AsusPC
 * Website - SusheelKaram.com
 */
public class InitialSetup {
    static Context mContext;
    static Activity mActivity;
    ListenerManager listenerManager;

    DrawerLayout myDrawerLayout;
    NavigationView myNavigationView;
    public InitialSetup(Context context){
        mContext = context;
        mActivity = (Activity) context;
        listenerManager = new ListenerManager(context);
    }

    public void run(){
        setupNavDrawer(mContext);
        ShorteningPointsManager shorteningPointsManager = new ShorteningPointsManager(mContext);
        shorteningPointsManager.initialStart();
        displayLinkPointsInDrawer();
    }

    protected void displayLinkPointsInDrawer(){
        SharedPreferences preferences = mContext.getSharedPreferences(AppContract.PREFS_APP_SETTINGS, Context.MODE_PRIVATE);

        String currentPoints = Integer.toString(preferences.getInt(AppContract.PREF_SHORTLINK_POINTS, 0));

        myNavigationView = (NavigationView) mActivity.findViewById(R.id.myNavigationView);
        View navHeader = myNavigationView.getHeaderView(0);

        TextView pointsTextView = navHeader.findViewById(R.id.textView_ShortPoints);
        Button btnGetMore = navHeader.findViewById(R.id.btn_getMore);

        pointsTextView.setText("Short links Left: " + currentPoints);
    }

    // Navigation Drawer
    protected void setupNavDrawer(Context context){

        myDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.myDrawerLayout);
        myNavigationView = (NavigationView) mActivity.findViewById(R.id.myNavigationView);

        myNavigationView.setNavigationItemSelectedListener(listenerManager.navigationListener);
    }


}
