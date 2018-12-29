package com.digicular.affiliateBuddy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.data.SiteDetector;

public class LinkGenerator {

    protected static final int AMAZON_IN = 100;
    protected static final int AMAZON_COM = 101;
    protected static final int FLIPKART = 200;
    protected static final int GEARBEST = 300;
    protected static final String LOG_TAG = "Link Generator";
    String generatedLink = "";
    LinkGenerator(){

    }

    public String generate(String linkAddress, int generatorMode, String affiliateID){
        // Site specific string keywords
        String selectedAssociateId = affiliateID;
        final String amazon_tagKeyword = "tag=";
        final String flipkart_tagKeyword = "affid=";
        final String gearbest_tagKeyword = "lkid=";

        switch (generatorMode){

            // Amazon.in and Amazon.com Link generator
            case AMAZON_IN:
            case AMAZON_COM:
                if (linkAddress.contains("?")) {
                    linkAddress = linkAddress.split("\\?")[0];
                    generatedLink = linkAddress + "?" + amazon_tagKeyword + selectedAssociateId;
                    //generatedLink = linkAddress + "&" + amazon_tagKeyword + selectedAssociateId;
                }
                else if (linkAddress.charAt(linkAddress.length() - 1) != '/') {
                    generatedLink = linkAddress + "/?" + amazon_tagKeyword + selectedAssociateId;
                }
                else {
                    generatedLink = linkAddress + "?" + amazon_tagKeyword + selectedAssociateId;
                }
                break;

            // Flipkart Link generator
            case FLIPKART:
                if (linkAddress.contains("?")) {
                    linkAddress = linkAddress.split("\\?")[0];
                    generatedLink = linkAddress + "?" + flipkart_tagKeyword + selectedAssociateId;
                    //generatedLink = linkAddress + "&" + amazon_tagKeyword + selectedAssociateId;
                }
                else if (linkAddress.charAt(linkAddress.length() - 1) != '/') {
                    generatedLink = linkAddress + "/?" + flipkart_tagKeyword + selectedAssociateId;
                }
                else {
                    generatedLink = linkAddress + "?" + flipkart_tagKeyword + selectedAssociateId;
                }
                break;

            // GearBest Link generator
            case GEARBEST:
                if (linkAddress.contains("?")) {
                    linkAddress = linkAddress.split("\\?")[0];
                    generatedLink = linkAddress + "?" + gearbest_tagKeyword + selectedAssociateId;
                    //generatedLink = linkAddress + "&" + amazon_tagKeyword + selectedAssociateId;
                }
                else if (linkAddress.charAt(linkAddress.length() - 1) != '/') {
                    generatedLink = linkAddress + "/?" + gearbest_tagKeyword + selectedAssociateId;
                }
                else {
                    generatedLink = linkAddress + "?" + gearbest_tagKeyword + selectedAssociateId;
                }
                break;

            default:
                Log.d(LOG_TAG,  "Please Enter a Valid URL");
                break;
        }
        return generatedLink;
    }
}
