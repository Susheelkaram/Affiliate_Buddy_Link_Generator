package com.digicular.affiliateBuddy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import com.bitly.Bitly;
import com.bitly.Error;
import com.bitly.Response;
import com.digicular.affiliateBuddy.data.AppContract;

import com.digicular.affiliateBuddy.data.SiteDetector;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/*
* @author Susheel Karam
* */

public class MainActivity extends BaseAppCompatActivity{

    // Site Detector Constants
    protected static final int AMAZON_IN = 100;
    protected static final int AMAZON_COM = 101;
    protected static final int FLIPKART = 200;
    protected static final int GEARBEST = 300;

    protected static int GeneratorMode = -1;

    private String txt_inputUrl;
    private String selectedAssociateId;
    private String txt_outputUrl;
    private String tagKeyword = "tag=";
    private String selectedAffiliateId = "";

    private TextView textView_AppMode;
    private TextInputLayout textInputLayout;
    private EditText inputUrl;
    private Spinner idSelector;
    private CheckBox linkShortenCheckbox;
    private MaterialButton buttonGenerate;
    private EditText generatedUrl;
    private static TextView textView_productTitle;
    private Button button_share;

    private Toolbar myToolbar;
    private ImageView headerImageView;
    private DrawerLayout myDrawerLayout;
    private NavigationView myNavigationView;
    private ClipboardManager clipboardManager;

    // Link Shortening
    private static final String ACCESS_TOKEN= "3f1fa331442b1cad7ad50c76c788bff6daf45b44";
    private static LinkShortener linkShortener2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        button_share = (Button) findViewById(R.id.button_share);

        // Clipboard copy
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // Link shortening (Bitly)
        Bitly.initialize(this, ACCESS_TOKEN);
        linkShortener2 = new LinkShortener();

        // Setting custom Toolbar or Action bar as default Actionbar
        setupActionBar();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        headerImageView.setVisibility(View.VISIBLE);


        // Setting up Navigation Drawer
        setupNavDrawer();

        //Browser launch
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            Log.i("MyApp", "Deep link clicked " + uri);
        }
        // Getting Affiliate Id selection
        idSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAffiliateId = idSelector.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Detecting change in Input URL and  Selecting Mode based on it
        inputUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newInputUrl = inputUrl.getText().toString();
                setAppMode(newInputUrl);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Getting data (URL) from Share action from Shopping apps & Browser
        Intent inShareIntent = getIntent();
        String inShareAction = inShareIntent.getAction();
        String inSharetype =  inShareIntent.getType();
        if(Intent.ACTION_SEND.equals(inShareAction) && inSharetype != null){
            if("text/plain".equals(inSharetype)){
                handleSendText(inShareIntent);
            }
        }

        // Share Out option - Sharing Generated link
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = generatedUrl.getText().toString();
                shareNowMain(MainActivity.this, shareText);
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

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                myDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ScrapingTask extends AsyncTask<String, Void, String> {
        // Parameters
        String entryUriString = "";
        String linkUrl = "";
        String htmlSelector;
        int siteCode;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView_productTitle.setText("Fetching Product Name...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String title = "";
            linkUrl = strings[0];
            entryUriString = strings[1];
            siteCode = Integer.valueOf(strings[2]);
            Document doc;

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
            }
            try {
                doc = Jsoup.connect(linkUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36").get();
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
            updateTitleValues.put(linksEntry.COLUMN_TITLE, s);
            Uri entryUri = Uri.parse(entryUriString);
            int updatedNo = getContentResolver().update(entryUri, updateTitleValues, null, null);
//            Toast.makeText(MainActivity.this, "No. of cells updated for Uri " + entryUri + " is " + Integer.toString(updatedNo),Toast.LENGTH_SHORT).show();
            textView_productTitle.setText(s);
        }

    }

    public void generateClick(View view){
        generatedUrl.setText("");
        int generateResponse = generateLink();
        if(generateResponse == 0) {
            if (!linkShortenCheckbox.isChecked()){
                addToDb();
            }
        }
    }


    public int generateLink(){
        txt_inputUrl = inputUrl.getText().toString();
        idSelector = (Spinner) findViewById(R.id.affId_selector);

        if (GeneratorMode != -1){
            Cursor spinnerCursor = (Cursor) idSelector.getSelectedItem();
            if(spinnerCursor != null) {
                selectedAffiliateId = spinnerCursor.getString(spinnerCursor.getColumnIndex(linksEntry.AFFID_COLUMN_IDTAG)).toString();
                LinkGenerator linkGenerator = new LinkGenerator();
                txt_outputUrl = linkGenerator.generate(txt_inputUrl, GeneratorMode, selectedAffiliateId);
                generatedUrl.setText(txt_outputUrl);

                // Shortening the Link
                if (linkShortenCheckbox.isChecked()){
                    linkShortener2.shorten(txt_outputUrl);
                }

                return 0;
            }
            else if (spinnerCursor == null){
                Toast.makeText(this, R.string.txt_EmptyAffId, Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(this, R.string.txt_InvalidUrl, Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    // This method copies the Generated URL to clipboard
    public void copyToClipboard(View view){
        if (txt_outputUrl != null) {
            ClipData clipDataUrl = ClipData.newPlainText(getResources().getString(R.string.txt_LabelClipboardCopy), txt_outputUrl);
            clipboardManager.setPrimaryClip(clipDataUrl);
            Toast.makeText(this, R.string.txt_SuccessClipboardCopy, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
    }
    protected boolean isValidUrl(String urlText){
        if(urlText.isEmpty()){
            return false;
        }
        return true;
    }
    protected void handleSendText(Intent intent){
        // Clearing Old information in the Views
        textView_productTitle.setText("");
        inputUrl.setText("");
        generatedUrl.setText("");

        // Catching Shared text and splitting into URl and extra description (Product title)
        String[] sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).split("(https:\\/\\/|http:\\/\\/)") ;
        String sharedDescription = sharedText[0];
        String sharedUrl = "https://" + sharedText[1];


        if (sharedText != null){
            setAppMode(sharedUrl);
            textView_productTitle.setText("");
            inputUrl.setText("");
            textView_productTitle.setText(sharedDescription);
            inputUrl.setText(sharedUrl);
        }
        generateClick(null);

    }

    public void addToDb(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy (hh:mm a)");
        String timeNow = dateFormat.format(date);
        String programName = AppContract.getTypeString(GeneratorMode);
        String title = textView_productTitle.getText().toString();
        String url = generatedUrl.getText().toString();

        ContentValues mValues = new ContentValues();
        mValues.put(linksEntry.COLUMN_DATETIME, timeNow);
        mValues.put(linksEntry.COLUMN_PROGRAM, programName);
        mValues.put(linksEntry.COLUMN_URL, url);

        if(title.isEmpty()){
            title = "No Title";
        }
        // Handling Empty title i.e., Avoiding null value in Title
        mValues.put(linksEntry.COLUMN_TITLE, title);


        // long newLinkEntryId =  mDb.insert(linksEntry.TABLE_NAME, null, mValues);
        Uri newLinkUri = getContentResolver().insert(linksEntry.CONTENT_URI, mValues);

        if(newLinkUri == null){
            Toast.makeText(this, "Failed! Unable to Add link to database", Toast.LENGTH_SHORT).show();
        }
        else {
//            Toast.makeText(this, "Success! Link Added to Database", Toast.LENGTH_SHORT).show();
        }

        // If Product Title is Empty, fetch it and update it on Database
        if(title.isEmpty() || title.equals("No Title")){
            ScrapingTask getTitle = new ScrapingTask();
            getTitle.execute(url, newLinkUri.toString(), Integer.toString(GeneratorMode));
//            ContentValues updateTitleValues = new ContentValues();
//            updateTitleValues.put(linksEntry.COLUMN_TITLE, "New title");
//            int updatedNo = getContentResolver().update(newLinkUri, updateTitleValues, null, null);
            //getContentResolver().update();
        }

    }
    public void viewLinkHistory(View view){
        Intent intent = new Intent(this, DisplayLinksHistory.class);
        startActivity(intent);
    }

    protected void setupActionBar(){
        // Setting custom Toolbar or Action bar as default Actionbar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }
    protected void setupNavDrawer(){
        // Setting up Navigation Drawer
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        myNavigationView = (NavigationView) findViewById(R.id.myNavigationView);
        myNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        Intent homeIntent =  new Intent(getApplicationContext(), MainActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        break;
                    case R.id.nav_myLinks:
                        Intent historyIntent = new Intent(getApplicationContext(), DisplayLinksHistory.class);
                        startActivity(historyIntent);
                        break;
                    case R.id.nav_settings:
                        Intent setupIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(setupIntent);
                        break;
                    case R.id.nav_about:
                        Intent bitlyIntent = new Intent(getApplicationContext(), BitlyShorten.class);
                        startActivity(bitlyIntent);
                        break;
                }
                menuItem.setChecked(true);
                myDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    // Method to detect the Site and Set the AppMode
    // to generate Links appropriately
    protected void setAppMode(String url){
        if (url != null){
            textView_AppMode = (TextView) findViewById(R.id.TextView_AppMode);
            SiteDetector siteDetector = new SiteDetector();
            GeneratorMode = siteDetector.detectSite(url);

            // Indicating Site name in a TextView
            if(GeneratorMode != -1){
                textView_AppMode.setText(AppContract.getTypeString(GeneratorMode));
            }
            else textView_AppMode.setText("Unknown");

            // Set Spinner data (AFF ids) to Spinner based on Mode
            idSelector = (Spinner) findViewById(R.id.affId_selector);
            String selection = linksEntry.AFFID_COLUMN_PROGRAM_NAME + "=?";
            String[] selectionArgs = new String[] {String.valueOf(AppContract.getTypeString(GeneratorMode))};
            Cursor affIds = getContentResolver().query(linksEntry.AFFID_CONTENT_URI, null, selection, selectionArgs, null);
            int[] adapterRowViews = new int[]{android.R.id.text1};
            SimpleCursorAdapter CA_affIds = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, affIds, new String[] {linksEntry.AFFID_COLUMN_IDTAG}, adapterRowViews, 0 );
            CA_affIds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idSelector.setAdapter(CA_affIds);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid URL", Toast.LENGTH_SHORT).show();
        }
    }

    // Share Action
    public void shareNowMain(Context context, String shareText){
        if(!shareText.isEmpty()){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(shareIntent, "Share your Generated link using: "));
        }
        else {
            Toast.makeText(this, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
        }
    }

    public class LinkShortener {
        String shortLink = "";
        public static final String LOG_TAG = "BitlyShorten.class";

        public String shorten(String longLink){
            Bitly.Callback bitlyCallback = new Bitly.Callback() {
                @Override
                public void onResponse(Response response) {
                    shortLink = response.getBitlink();
                    Log.d("RESPONSE", "onResponse: " + shortLink);
                    if (shortLink != null) {
                        generatedUrl.setText(shortLink);
                    }
                    addToDb();
                }

                @Override
                public void onError(Error error) {
                    Log.d("BITLY_ERROR", "Bitlink_error: " + error.getErrorMessage());
                    addToDb();
                }
            };
            Bitly.shorten(longLink, bitlyCallback);

            return shortLink;
        }
    }
}
