package com.digicular.affiliateBuddy;

import com.digicular.affiliateBuddy.data.AppContract;

import android.util.Log;

public class LinkGenerator {

    protected static final int AMAZON = AppContract.AMAZON_TYPE_CODE;
//    protected static final int AMAZON_COM = AppContract.AMAZONCOM_TYPE_CODE;
    protected static final int FLIPKART = AppContract.FLIPKART_TYPE_CODE;
    protected static final int GEARBEST = AppContract.GEARBEST_TYPE_CODE;
    protected static final int BANGGOOD = AppContract.BANGGOOD_TYPE_CODE;

    protected static final String LOG_TAG = "Link Generator";

    String generatedLink = "";
    LinkGenerator(){ }

    public String generate(String linkAddress, int generatorMode, String affiliateID){
        // Site specific string keywords
        String selectedAssociateId = affiliateID;
        final String amazon_tagKeyword = "tag=";
        final String flipkart_tagKeyword = "affid=";
        final String gearbest_tagKeyword = "lkid=";
        final String banggood_tagKeyword = "p=";

        switch (generatorMode){

            // Amazon.in and Amazon.com Link generator

            case AMAZON:
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
            case BANGGOOD:
                if (linkAddress.contains("?")) {
                    linkAddress = linkAddress.split("\\?")[0];
                    generatedLink = linkAddress + "?" + banggood_tagKeyword + selectedAssociateId;
                    //generatedLink = linkAddress + "&" + amazon_tagKeyword + selectedAssociateId;
                }
                else if (linkAddress.charAt(linkAddress.length() - 1) != '/') {
                    generatedLink = linkAddress + "/?" + banggood_tagKeyword + selectedAssociateId;
                }
                else {
                    generatedLink = linkAddress + "?" + banggood_tagKeyword + selectedAssociateId;
                }
                break;

            default:
                Log.d(LOG_TAG,  "Please Enter a Valid URL");
                break;
        }
        return generatedLink;
    }
}
