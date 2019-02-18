package com.digicular.affiliateBuddy.data;

import java.util.HashMap;
import java.util.Map;

public final class AppContract {


    // String codes for Sites/Programs
//    public static final String AMAZONCOM_TYPE_ST = "amazon.com";
    public static final String AMAZON_TYPE_ST = "amazon";
    public static final String FLIPKART_TYPE_ST = "flipkart";
    public static final String GEARBEST_TYPE_ST = "gearbest";

    // Numeric Codes for Sites/Programs
//    public static final int AMAZONCOM_TYPE_CODE = 200;
    public static final int AMAZON_TYPE_CODE = 201;
    public static final int FLIPKART_TYPE_CODE = 202;
    public static final int GEARBEST_TYPE_CODE = 203;

    protected static HashMap<Integer, String> appCodeMap = new HashMap<Integer, String>()
                                            {{
                                                put(AMAZON_TYPE_CODE,AMAZON_TYPE_ST);
                                                put(FLIPKART_TYPE_CODE, FLIPKART_TYPE_ST);
                                                put(GEARBEST_TYPE_CODE, GEARBEST_TYPE_ST);
                                            }};
    public static int getTypeCode(String value){
        for (Map.Entry entry : appCodeMap.entrySet()){
            if (entry.getValue().equals(value)) {
                return (Integer) entry.getKey();
            }
        }
        return 0;
    }
    public static String getTypeString(int key){
        if(appCodeMap.containsKey(key)){
            return appCodeMap.get(key);
        }
        return null;
    }
}
