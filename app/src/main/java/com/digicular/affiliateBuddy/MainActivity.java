package com.digicular.affiliateBuddy;

import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitly.Bitly;
import com.bitly.Error;
import com.bitly.Response;
import com.digicular.affiliateBuddy.data.AppContract;

import com.digicular.affiliateBuddy.staticActivities.AboutApp;
import com.digicular.affiliateBuddy.staticActivities.HowToUse;
import com.digicular.affiliateBuddy.utils.AdManager;
import com.digicular.affiliateBuddy.utils.InitialSetup;
import com.digicular.affiliateBuddy.utils.ListenerManager;
import com.digicular.affiliateBuddy.utils.SiteDetector;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;
import com.digicular.affiliateBuddy.utils.TitleFetcher;
import com.digicular.affiliateBuddy.utils.Helpers;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/*
 * @author Susheel Karam
 * Website - SusheelKaram.com
 * */

public class MainActivity extends BaseAppCompatActivity {

    // Site Detector Constants
    protected static final int AMAZON_IN = 100;
    protected static final int AMAZON_COM = 101;
    protected static final int FLIPKART = 200;
    protected static final int GEARBEST = 300;

    protected static int GeneratorMode = -1;

    private Context mContext = MainActivity.this;
    private String txt_inputUrl;
    private String selectedAssociateId;
    private String txt_outputUrl;
    private String tagKeyword = "tag=";
    private String selectedAffiliateId = "";
    private boolean isAutoShortenEnabled;

    private TextView textView_AppMode;
    private TextInputLayout textInputLayout;
    private EditText inputUrl;
    private Spinner idSelector;
    private CheckBox linkShortenCheckbox;
    private MaterialButton buttonGenerate;
    private EditText generatedUrl;
    private static TextView textView_productTitle;
    private Button buttonShare;
    private Button buttonCopy;
    private Button btnAddMorePoints;

    // Ads
    private AdView bannerAd;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd linkRewardsAd;

    private Toolbar myToolbar;
    private ImageView headerImageView;
    private DrawerLayout myDrawerLayout;
    private NavigationView myNavigationView;
    private ClipboardManager clipboardManager;

    // Link Shortening
    private static String ACCESS_TOKEN = null;
    private static LinkShortener linkShortener;
    private static ListenerManager listenerManager;
    private static AdManager adManager;

    // Shared Preferences
    SharedPreferences appPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layout views Initialisation
        headerImageView = (ImageView) findViewById(R.id.imageView_Header);
        inputUrl = (EditText) findViewById(R.id.txtInput_url);
        idSelector = (Spinner) findViewById(R.id.affId_selector);
        linkShortenCheckbox = (CheckBox) findViewById(R.id.checkBox_Shorten);
        buttonGenerate = (MaterialButton) findViewById(R.id.button_generate);
        generatedUrl = (EditText) findViewById(R.id.txtInput_generatedUrl);
        textView_productTitle = (TextView) findViewById(R.id.textView_productTitle);
        buttonShare = (Button) findViewById(R.id.button_share);
        buttonCopy = (Button) findViewById(R.id.button_copyToClipboard);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        myNavigationView = (NavigationView) findViewById(R.id.myNavigationView);
        btnAddMorePoints = (Button) myNavigationView.getHeaderView(0)
                .findViewById(R.id.btn_getMore);
        bannerAd = (AdView) findViewById(R.id.ad_bannerAd);


        listenerManager = new ListenerManager(this);
        adManager = new AdManager(this);

        // Clipboard copy
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // Setting custom Toolbar or Action bar as default Actionbar
        setupActionBar();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        headerImageView.setVisibility(View.VISIBLE);


        // Shared Preferences
        appPreferences = getApplicationContext().getSharedPreferences(AppContract.PREFS_APP_SETTINGS, MODE_PRIVATE);
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case AppContract.PREF_AUTO_SHORTEN:
                        boolean autoShortenPreference = appPreferences.getBoolean(AppContract.PREF_AUTO_SHORTEN, false);
                        linkShortenCheckbox.setChecked(autoShortenPreference);
                        break;
                }
            }
        };
        appPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        // AdMob Initialization
        adManager.initializeAdMob();

        // Link shortening (Bitly)
        ACCESS_TOKEN = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);

        if (ACCESS_TOKEN != null) {
            // User Bit.ly account is being used
            Bitly.initialize(this, ACCESS_TOKEN);
        } else {
            // Developer Bit.ly account is being used (with Points limit)
            Bitly.initialize(this, AppContract.DEV_BITLY_ACCESS_TOKEN);
            appPreferences.edit()
                    .putBoolean(AppContract.PREF_IS_FREE_SHORTENER, true)
                    .apply();
        }

        linkShortener = new LinkShortener();

        // Auto shorten switch
        isAutoShortenEnabled = appPreferences.getBoolean(AppContract.PREF_AUTO_SHORTEN, false);
        linkShortenCheckbox.setChecked(isAutoShortenEnabled);

        // Setting up Navigation Drawer
//        Helpers.setupNavDrawer(this);
        InitialSetup initialSetup = new InitialSetup(this);
        initialSetup.run();

        // Getting Affiliate Id selection
        idSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAffiliateId = idSelector.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Detecting change in Input URL and  Selecting Mode based on it
        inputUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newInputUrl = inputUrl.getText().toString();
                setAppMode(newInputUrl);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Getting data (URL) from Share action from Shopping apps & Browser
        Intent inShareIntent = getIntent();
        String inShareAction = inShareIntent.getAction();
        String inSharetype = inShareIntent.getType();
        if (Intent.ACTION_SEND.equals(inShareAction) && inSharetype != null) {
            if (inSharetype.equals("text/plain")) {
                handleSendText(inShareIntent);
            }
        }

        // Share Out option - Sharing Generated link
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = generatedUrl.getText().toString();
                Helpers.shareNow(MainActivity.this, shareText);
            }
        });

        buttonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copyText = txt_outputUrl;
                Helpers.copyToClipboard(mContext, copyText);
            }
        });


        // Setting Affiliate ID from user dropdown selection
        idSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssociateId = idSelector.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Loading ads
        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(bannerAdRequest);

        adManager.loadInterstitialAd();

        // Reward Video Ad
        adManager.loadRewardAd();
        btnAddMorePoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adManager.showRewardAd();
            }
        });
    }

    /* onCreate() ENDS HERE*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                myDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void generateClick(View view) {
        generatedUrl.setText("");
        int generateResponse = generateLink();
        if (generateResponse == 0) {
            if (!linkShortenCheckbox.isChecked()) {
                Helpers.addToDb(this);
            }
        }
        adManager.showInterstitialAd();
    }

    public int generateLink() {
        txt_inputUrl = inputUrl.getText().toString();
        idSelector = (Spinner) findViewById(R.id.affId_selector);

        if (GeneratorMode != -1) {
            Cursor spinnerCursor = (Cursor) idSelector.getSelectedItem();
            if (spinnerCursor != null) {
                selectedAffiliateId = spinnerCursor.getString(spinnerCursor.getColumnIndex(linksEntry.AFFID_COLUMN_IDTAG)).toString();
                LinkGenerator linkGenerator = new LinkGenerator();
                txt_outputUrl = linkGenerator.generate(txt_inputUrl, GeneratorMode, selectedAffiliateId);
                generatedUrl.setText(txt_outputUrl);

                // Shortening the Link
                if (linkShortenCheckbox.isChecked()) {
                    linkShortener.shorten(txt_outputUrl);
                }

                return 0;
            } else if (spinnerCursor == null) {
                Toast.makeText(this, R.string.txt_EmptyAffId, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.txt_InvalidUrl, Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    protected boolean isValidUrl(String urlText) {
        if (urlText.isEmpty()) {
            return false;
        }
        return true;
    }

    protected void handleSendText(Intent intent) {
        // Clearing Old information in the Views
        textView_productTitle.setText("");
        inputUrl.setText("");
        generatedUrl.setText("");

        // Getting Shared text and splitting into URl and extra description (i.e., Product title)
        String[] sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).split("(https:\\/\\/|http:\\/\\/)");
        String sharedDescription = sharedText[0];
        String sharedUrl = "https://" + sharedText[1];


        if (sharedText != null) {
            setAppMode(sharedUrl);
            textView_productTitle.setText("");
            inputUrl.setText("");
            textView_productTitle.setText(sharedDescription);
            inputUrl.setText(sharedUrl);
        }
        generateClick(null);
    }


    protected void setupActionBar() {
        // Setting custom Toolbar or Action bar as default Actionbar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }


    // Method to detect the Site and Set the AppMode
    // to generate Links appropriately
    public void setAppMode(String url) {
        if (url != null) {
            textView_AppMode = (TextView) findViewById(R.id.TextView_AppMode);
            SiteDetector siteDetector = new SiteDetector();
            GeneratorMode = siteDetector.detectSite(url);

            // Indicating Site name in a TextView
            if (GeneratorMode != -1) {
                textView_AppMode.setText(AppContract.getModeAsString(GeneratorMode));
            } else textView_AppMode.setText("Unknown");

            // Set Spinner data (AFF ids) to Spinner based on Mode
            idSelector = (Spinner) findViewById(R.id.affId_selector);
            String selection = linksEntry.AFFID_COLUMN_PROGRAM_NAME + "=?";
            String[] selectionArgs = new String[]{String.valueOf(AppContract.getModeAsString(GeneratorMode))};
            Cursor affIds = getContentResolver().query(linksEntry.AFFID_CONTENT_URI, null, selection, selectionArgs, null);
            int[] adapterRowViews = new int[]{android.R.id.text1};
            SimpleCursorAdapter CA_affIds = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, affIds, new String[]{linksEntry.AFFID_COLUMN_IDTAG}, adapterRowViews, 0);
            CA_affIds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idSelector.setAdapter(CA_affIds);
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid URL", Toast.LENGTH_SHORT).show();
        }
    }


    // Link Shortener using Bitly
    public class LinkShortener {
        String shortLink = null;
        String accessToken = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);

        public String shorten(String longLink) {
            boolean isUsingFreeLinkPoints = appPreferences.getBoolean(AppContract.PREF_IS_FREE_SHORTENER, true);
            int currentFreeLinkPoints = appPreferences.getInt(AppContract.PREF_SHORTLINK_POINTS, 0);

            Bitly.Callback bitlyCallback = new Bitly.Callback() {
                @Override
                public void onResponse(Response response) {
                    shortLink = response.getBitlink();
                    Log.d("RESPONSE", "onResponse: " + shortLink);
                    if (shortLink != null) {
                        generatedUrl.setText(shortLink);
                    }
                    Helpers.addToDb(mContext);
                }

                @Override
                public void onError(Error error) {
                    Log.d("BITLY_ERROR", "Bitlink_error: " + error.getErrorMessage());
                    Helpers.addToDb(mContext);
                }
            };

            if (isUsingFreeLinkPoints && currentFreeLinkPoints > 0) {
                Bitly.shorten(longLink, bitlyCallback);
                appPreferences.edit()
                        .putInt(AppContract.PREF_SHORTLINK_POINTS, --currentFreeLinkPoints)
                        .apply();
            } else if (isUsingFreeLinkPoints && currentFreeLinkPoints <= 0) {
                Toast.makeText(getApplicationContext(),
                        "No Free Points. Add more points by pressing \"+\" button in the Menu to Shorten links or Link your Bitly account", Toast.LENGTH_SHORT).show();
            } else if (accessToken != null) {
                Bitly.shorten(longLink, bitlyCallback);
            } else {
                Toast.makeText(getApplicationContext(), "Please link your Bit.ly account or Turn-off link shortening", Toast.LENGTH_SHORT).show();
            }
            return shortLink;
        }
    }


    // XXXXXXXXXXXXXXXXXXXXXXXXX Trash Code under this which should be deleted
    // But couldn't because of fear of crashing :( XXXXXXXXXXXXXXXXXXXXXXXXX


    //    // Share Action
//    public void shareNowMain(Context context, String shareText){
//        if(!shareText.isEmpty()){
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
//            shareIntent.setType("text/plain");
//            context.startActivity(Intent.createChooser(shareIntent, "Share your Generated link using: "));
//        }
//        else {
//            Toast.makeText(this, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
//        }
//    }


//  // This method copies the Generated URL to clipboard
//    public void copyToClipboard(View view){
//        if (txt_outputUrl != null) {
//            ClipData clipDataUrl = ClipData.newPlainText(getResources().getString(R.string.txt_LabelClipboardCopy), txt_outputUrl);
//            clipboardManager.setPrimaryClip(clipDataUrl);
//            Toast.makeText(this, R.string.txt_SuccessClipboardCopy, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Toast.makeText(this, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
//    }

    //    // Fetches Product title in Background if Title is empty
//    public class TitleFetcherTask extends AsyncTask<String, Void, String> {
//        // Parameters
//        String entryUriString = "";
//        String linkUrl = "";
//        String htmlSelector;
//        int siteCode;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            textView_productTitle.setText("Fetching Product Name...");
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String title = "";
//            linkUrl = strings[0];
//            entryUriString = strings[1];
//            siteCode = Integer.valueOf(strings[2]);
//            Document doc;
//
//            // Checking if it an App Deep link (Ex: https://gearbest.app.link/nsjdjsjd)
//            boolean isAppLink;
//            String appLinkPattern = "^(https:\\/\\/|http:\\/\\/)?((([bB]anggood)|[gG]earbest)[.])(app[.]link)(\\/).{0,}?$";
//            isAppLink = Pattern.matches(appLinkPattern, linkUrl);
//
//            // Choosing the HTML DOM selector based on Website
//            switch (siteCode){
//                case AppContract.AMAZON_TYPE_CODE:
//                    htmlSelector = "span#productTitle";
//                    break;
//                case AppContract.FLIPKART_TYPE_CODE:
//                    htmlSelector = "span._35KyD6";
//                    break;
//                case AppContract.GEARBEST_TYPE_CODE:
//                    htmlSelector = "h1.goodsIntro_title";
//                    break;
//                case AppContract.BANGGOOD_TYPE_CODE:
//                    if(isAppLink){
//                        htmlSelector = "div.product_title";
//                    }
//                    else htmlSelector = "strong.title_strong";
//                    break;
//                default:
//                    Log.d("DOM SELECTOR", "Invalid site type");
//            }
//            try {
//                String mUserAgents;
//                if(isAppLink){
//                    mUserAgents = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
//                }
//                else {
//                    mUserAgents = "Mozilla/65.0.1 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";
//                }
//                doc = Jsoup
//                        .connect(linkUrl)
//                        .timeout(10 * 1000)
//                        .userAgent(mUserAgents)
//                        .referrer("http://www.google.com")
//                        .followRedirects(false)
//                        .get();
//                Log.i("JSOUP", "jsoup_title" + doc.title());
//                Log.i("JSOUP", "jsoup" + doc.toString());
//                Element productTitle = doc.select(htmlSelector).first();
//                if(productTitle != null) {
//                    title = productTitle.text();
//                }
//                else {
//                    title = doc.title();
//                }
//            }
//            catch (IOException IOe){
//                IOe.printStackTrace();
//                Log.i("JSOUP_ERROR", "jsoup" + IOe);
//
//            }
//
//            return title;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if(s == null || s.equals("")){
//                s = "No Product Name";
//            }
//            ContentValues updateTitleValues = new ContentValues();
//            updateTitleValues.put(linksEntry.COLUMN_TITLE, s);
//            Uri entryUri = Uri.parse(entryUriString);
//            int updatedNo = getContentResolver().update(entryUri, updateTitleValues, null, null);
////            Toast.makeText(MainActivity.this, "No. of cells updated for Uri " + entryUri + " is " + Integer.toString(updatedNo),Toast.LENGTH_SHORT).show();
//            textView_productTitle.setText(s);
//        }
//
//    }


    //    public void addToDb(){
//        Date date = Calendar.getInstance().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy (hh:mm a)");
//        String timeNow = dateFormat.format(date);
//        String programName = AppContract.getModeAsString(GeneratorMode);
//        String title = textView_productTitle.getText().toString();
//        String url = generatedUrl.getText().toString();
//
//        ContentValues mValues = new ContentValues();
//        mValues.put(linksEntry.COLUMN_DATETIME, timeNow);
//        mValues.put(linksEntry.COLUMN_PROGRAM, programName);
//        mValues.put(linksEntry.COLUMN_URL, url);
//
//        if(title.isEmpty()){
//            title = "No Title";
//        }
//        // Handling Empty title i.e., Avoiding null value in Title
//        mValues.put(linksEntry.COLUMN_TITLE, title);
//
//
//        // long newLinkEntryId =  mDb.insert(linksEntry.TABLE_NAME, null, mValues);
//        Uri newLinkUri = getContentResolver().insert(linksEntry.CONTENT_URI, mValues);
//
//        if(newLinkUri == null){
//            Toast.makeText(this, "Failed! Unable to Add link to database", Toast.LENGTH_SHORT).show();
//        }
//        else {
////            Toast.makeText(this, "Success! Link Added to Database", Toast.LENGTH_SHORT).show();
//        }
//
//        // If Product Title is Empty, fetch it and update it on Database
//        if(title.isEmpty() || title.equals("No Title")){
//            TitleFetcher getTitle = new TitleFetcher(this);
//            getTitle.execute(url, newLinkUri.toString(), Integer.toString(GeneratorMode));
//        }
//
//    }


    // Navigation Drawer
//    protected void setupNavDrawer(){
//        // Setting up Navigation Drawer
//        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
//        myNavigationView = (NavigationView) findViewById(R.id.myNavigationView);
//        myNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.nav_home:
//                        Intent homeIntent =  new Intent(getApplicationContext(), MainActivity.class);
//                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(homeIntent);
//                        break;
//                    case R.id.nav_myLinks:
//                        Intent historyIntent = new Intent(getApplicationContext(), DisplayLinksHistory.class);
//                        startActivity(historyIntent);
//                        break;
//                    case R.id.nav_howtoUse:
//                        Intent howToUseIntent = new Intent(getApplicationContext(), HowToUse.class);
//                        startActivity(howToUseIntent);
//                        break;
//                    case R.id.nav_settings:
//                        Intent setupIntent = new Intent(getApplicationContext(), SettingsActivity.class);
//                        startActivity(setupIntent);
//                        break;
//                    case R.id.nav_about:
//                        Intent bitlyIntent = new Intent(getApplicationContext(), AboutApp.class);
//                        startActivity(bitlyIntent);
//                        break;
//                }
//                menuItem.setChecked(true);
//                myDrawerLayout.closeDrawers();
//                return true;
//            }
//        });
//    }

}
