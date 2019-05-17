package com.digicular.affiliateBuddy.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.digicular.affiliateBuddy.R;
import com.digicular.affiliateBuddy.data.AppContract;
import com.digicular.affiliateBuddy.data.linksContract;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by AsusPC
 * Website - SusheelKaram.com
 */
// Fetches Product title in Background if Title is empty
public class TitleFetcher extends AsyncTask<String, Void, String> {
    // Parameters
    String entryUriString = "";
    String linkUrl = "";
    String htmlSelector;
    int siteCode;
    Context context;
    TextView titleView;

    public TitleFetcher(Context context){
        this.context = context;
        titleView = ((Activity) context).findViewById(R.id.textView_productTitle);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        titleView.setText("Fetching Product Name...");
    }

    @Override
    protected String doInBackground(String... strings) {
        String title = "";
        linkUrl = strings[0];
        entryUriString = strings[1];
        siteCode = Integer.valueOf(strings[2]);
        Document doc;

        // Checking if it an App Deep link (Ex: https://gearbest.app.link/nsjdjsjd)
        boolean isAppLink;
        String appLinkPattern = "^(https:\\/\\/|http:\\/\\/)?((([bB]anggood)|[gG]earbest)[.])(app[.]link)(\\/).{0,}?$";
        isAppLink = Pattern.matches(appLinkPattern, linkUrl);

        // Choosing the HTML DOM selector based on Website
        switch (siteCode){
            case AppContract.AMAZON_TYPE_CODE:
                htmlSelector = "span#productTitle";
                break;
            case AppContract.FLIPKART_TYPE_CODE:
                htmlSelector = "span._35KyD6";
                break;
            case AppContract.GEARBEST_TYPE_CODE:
                htmlSelector = "h1.goodsIntro_title";
                break;
            case AppContract.BANGGOOD_TYPE_CODE:
                if(isAppLink){
                    htmlSelector = "div.product_title";
                }
                else htmlSelector = "strong.title_strong";
                break;
            default:
                Log.d("DOM SELECTOR", "Invalid site type");
        }
        try {
            String mUserAgents;
            if(isAppLink){
                mUserAgents = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
            }
            else {
                mUserAgents = "Mozilla/65.0.1 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";
            }
            doc = Jsoup
                    .connect(linkUrl)
                    .timeout(10 * 1000)
                    .userAgent(mUserAgents)
                    .referrer("http://www.google.com")
                    .followRedirects(false)
                    .get();
            Log.i("JSOUP", "jsoup_title" + doc.title());
            Log.i("JSOUP", "jsoup" + doc.toString());
            Element productTitle = doc.select(htmlSelector).first();
            if(productTitle != null) {
                title = productTitle.text();
            }
            else {
                title = doc.title();
            }
        }
        catch (IOException IOe){
            IOe.printStackTrace();
            Log.i("JSOUP_ERROR", "jsoup" + IOe);

        }

        return title;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s == null || s.equals("")){
            s = "No Product Name";
        }
        ContentValues updateTitleValues = new ContentValues();
        updateTitleValues.put(linksContract.linksEntry.COLUMN_TITLE, s);
        Uri entryUri = Uri.parse(entryUriString);
        int updatedNo = context.getContentResolver().update(entryUri, updateTitleValues, null, null);
//            Toast.makeText(MainActivity.this, "No. of cells updated for Uri " + entryUri + " is " + Integer.toString(updatedNo),Toast.LENGTH_SHORT).show();
        titleView.setText(s);
    }

}
