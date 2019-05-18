package com.digicular.affiliateBuddy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.digicular.affiliateBuddy.DisplayLinksHistory;
import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.R;
import com.digicular.affiliateBuddy.SettingsActivity;
import com.digicular.affiliateBuddy.data.AppContract;
import com.digicular.affiliateBuddy.staticActivities.AboutApp;
import com.digicular.affiliateBuddy.staticActivities.HowToUse;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by AsusPC
 * Website - SusheelKaram.com
 */
public class ListenerManager {
    Context mContext;
    static Activity mActivity;
    static EditText editText_inputUrl;
    DrawerLayout myDrawerLayout;
    NavigationView myNavigationView;
    SharedPreferences preferences;
    ShorteningPointsManager shorteningPointsManager;

    public ListenerManager(Context context){
        mContext = context;
        mActivity = (Activity) mContext;

        editText_inputUrl = (EditText) mActivity.findViewById(R.id.txtInput_url);
        myDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.myDrawerLayout);
        myNavigationView = (NavigationView) mActivity.findViewById(R.id.myNavigationView);

        preferences = context.getSharedPreferences(AppContract.PREFS_APP_SETTINGS, Context.MODE_PRIVATE);
        shorteningPointsManager = new ShorteningPointsManager(mContext);
    }
//    public TextWatcher inputTextWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            String newInputUrl = editText_inputUrl.getText().toString();
//            MainActivity.setAppMode(newInputUrl);
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) { }
//    };

    public NavigationView.OnNavigationItemSelectedListener navigationListener  = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent homeIntent =  new Intent(mContext, MainActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(homeIntent);
                    break;
                case R.id.nav_myLinks:
                    Intent historyIntent = new Intent(mContext, DisplayLinksHistory.class);
                    mContext.startActivity(historyIntent);
                    break;
                case R.id.nav_howtoUse:
                    Intent howToUseIntent = new Intent(mContext, HowToUse.class);
                    mContext.startActivity(howToUseIntent);
                    break;
                case R.id.nav_settings:
                    Intent setupIntent = new Intent(mContext, SettingsActivity.class);
                    mContext.startActivity(setupIntent);
                    break;
                case R.id.nav_about:
                    Intent bitlyIntent = new Intent(mContext, AboutApp.class);
                    mContext.startActivity(bitlyIntent);
                    break;
            }
            menuItem.setChecked(true);
            myDrawerLayout.closeDrawers();
            return true;
        }
    };

    public RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {

        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            shorteningPointsManager.addRewards();
            Toast.makeText(mContext, "Congratulations! You won 5 Short link points", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Toast.makeText(mContext, "Unable to load the Ad. Check your Internet connection", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoCompleted() {

        }

    };

}
