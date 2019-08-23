package com.digicular.affiliateBuddy.utils;

import android.content.Context;

import com.digicular.affiliateBuddy.data.AppContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

/**
 * Created by AsusPC
 * Website - SusheelKaram.com
 */
public class AdManager {
    Context mContext;

    protected RewardedVideoAd linkRewardsAd;
    private InterstitialAd interstitialAd;

    protected ListenerManager listenerManager;

    public AdManager(Context context) {
        mContext = context;
        listenerManager = new ListenerManager(context);
    }

    public void initializeAdMob() {
        MobileAds.initialize(mContext, AppContract.ADMOB_APP_ID);
    }

    private void loadBannerAd() {

    }

    public void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(mContext);
        interstitialAd.setAdUnitId(AppContract.ADMOB_AD_INTERSTITIAL_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void showInterstitialAd() {
        if (interstitialAd == null) {
            loadInterstitialAd();
            return;
        }

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            return;
        }
        loadInterstitialAd();
        interstitialAd.show();
    }

    public void loadRewardAd() {
        linkRewardsAd = MobileAds.getRewardedVideoAdInstance(mContext);
        linkRewardsAd.setRewardedVideoAdListener(listenerManager.rewardedVideoAdListener);
        linkRewardsAd.loadAd(AppContract.ADMOB_AD_REWARD_ID,
                new AdRequest.Builder().build());
    }


    public void showRewardAd() {
        if (linkRewardsAd.isLoaded()) {
            linkRewardsAd.show();
            loadRewardAd();
        } else {
            loadRewardAd();
            linkRewardsAd.show();
        }
    }
}
