package com.digicular.affiliateBuddy.staticActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.digicular.affiliateBuddy.BaseAppCompatActivity;
import com.digicular.affiliateBuddy.R;

public class HowToUse extends BaseAppCompatActivity {
    WebView tutorialWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        tutorialWebView =  (WebView) findViewById(R.id.tutorialWebView);

        tutorialWebView.loadUrl("file:///android_asset/tutorial.html");
    }
}
