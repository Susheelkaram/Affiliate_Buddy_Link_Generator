package com.digicular.affiliateBuddy.staticActivities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.digicular.affiliateBuddy.BaseAppCompatActivity;
import com.digicular.affiliateBuddy.R;

//import butterknife.BindView;

/*
 * @author Susheel Karam
 * Website - SusheelKaram.com
 * */
public class AboutApp extends BaseAppCompatActivity {
//    @BindView(R.id.tv_itemName) TextView tv_Name;
//    @BindView(R.id.tv_itemWebsite) TextView tv_Website;
//    @BindView(R.id.tv_itemTwitter) TextView tv_Twitter;
//    @BindView(R.id.tv_itemRateUs) TextView tv_RateUs;
//    @BindView(R.id.tv_itemShare) TextView tv_Share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
    }

    public void itemOnClick(View view){
        int itemId = view.getId();
        Intent browserIntent;
        switch(itemId) {
            case R.id.tv_itemWebsite:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://susheelkaram.com"));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemTwitter:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Susheel_karam"));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemShare:
                break;
        }
    }
}
