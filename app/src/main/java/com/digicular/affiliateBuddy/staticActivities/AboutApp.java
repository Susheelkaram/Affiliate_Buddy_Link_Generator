package com.digicular.affiliateBuddy.staticActivities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
    CardView aboutAppCard;
    CardView aboutDevCard;
    CardView aboutFeedbackCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        aboutAppCard = (CardView) findViewById(R.id.aboutAppCard);
        aboutDevCard = (CardView) findViewById(R.id.aboutDevCard);
        aboutFeedbackCard = (CardView) findViewById(R.id.aboutFeedbackCard);

    }

    public void itemOnClick(View view){
        int itemId = view.getId();
        Intent browserIntent;
        String marketUrl = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
        switch(itemId) {
            case R.id.tv_itemWebsite:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://susheelkaram.com"));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemTwitter:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Susheel_karam"));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemLinkedin:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/susheelkaram/"));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemRateUs:
                String packageName = getApplicationContext().getPackageName();
                browserIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse("market://details?id=" + packageName));
                startActivity(browserIntent);
                break;
            case R.id.tv_itemShare:
                Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
                shareAppIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.txt_ShareApp) + " - " + marketUrl);
                shareAppIntent.setType("text/plain");
                startActivity(shareAppIntent);
                break;
        }
    }

    // Animation
    public void animate(){
        int appCardStartVal = aboutAppCard.getLeft() - 20;
        int appCardEndVal = aboutAppCard.getLeft();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(aboutAppCard, "x", appCardStartVal, appCardEndVal);

        int devCardStartVal = aboutDevCard.getLeft() - 20;
        int devCardEndVal = aboutDevCard.getLeft();
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(aboutDevCard, "x", devCardStartVal, devCardEndVal);

        int feedbackCardStartVal = aboutFeedbackCard.getLeft() - 20;
        int feedbackCardEndVal = aboutFeedbackCard.getLeft();
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(aboutFeedbackCard, "x", feedbackCardStartVal, feedbackCardEndVal);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(anim1, anim2, anim3);
        set.start();
    }

}
