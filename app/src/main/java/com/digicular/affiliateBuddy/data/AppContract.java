package com.digicular.affiliateBuddy.data;

import java.util.HashMap;
import java.util.Map;

public final class AppContract {


    // String codes for Sites/Programs
    public static final String AMAZON_TYPE_ST = "amazon";
    public static final String FLIPKART_TYPE_ST = "flipkart";
    public static final String GEARBEST_TYPE_ST = "gearbest";
    public static final String BANGGOOD_TYPE_ST = "banggood";

    // Numeric Codes for Sites/Programs
   public static final int AMAZON_TYPE_CODE = 201;
    public static final int FLIPKART_TYPE_CODE = 202;
    public static final int GEARBEST_TYPE_CODE = 203;
    public static final int BANGGOOD_TYPE_CODE = 204;

    // Shared Preferences
    public static final String PREFS_APP_SETTINGS = "affoPreferences";
    public static final String PREF_AUTO_SHORTEN = "autoLinkShortening";
    public static final String PREF_BITLY_TOKEN = "bitlyAccessToken";
    public static final String PREF_BITLY_NAME = "bitlyLoginName";
    public static final String PREF_SHORTLINK_POINTS = "shortenPoints";
    public static final String PREF_IS_FIRST_STARTUP = "isFirstStartup";
    public static final String PREF_IS_FREE_SHORTENER = "isFreeShorterInUse";

    // Short Link Points
    public static final String DEV_BITLY_ACCESS_TOKEN = "c7d893ceb4a5d1f91ffa124522772cc6c7beb4c9";
    public static final int POINTS_MAX = 50;
    public static final int POINTS_INITIAL = 10;
    public static final int POINTS_REWARD = 5;

    protected static HashMap<Integer, String> appCodeMap = new HashMap<Integer, String>()
                                            {{
                                                put(AMAZON_TYPE_CODE,AMAZON_TYPE_ST);
                                                put(FLIPKART_TYPE_CODE, FLIPKART_TYPE_ST);
                                                put(GEARBEST_TYPE_CODE, GEARBEST_TYPE_ST);
                                                put(BANGGOOD_TYPE_CODE, BANGGOOD_TYPE_ST);
                                            }};

    public static int getModeCode(String value){
        for (Map.Entry entry : appCodeMap.entrySet()){
            if (entry.getValue().equals(value)) {
                return (Integer) entry.getKey();
            }
        }
        return 0;
    }
    public static String getModeAsString(int key){
        if(appCodeMap.containsKey(key)){
            return appCodeMap.get(key);
        }
        return null;
    }
}
