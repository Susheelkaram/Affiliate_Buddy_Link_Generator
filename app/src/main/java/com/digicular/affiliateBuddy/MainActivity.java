package com.digicular.affiliateBuddy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import com.digicular.affiliateBuddy.data.linksDbHelper;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private String txt_inputUrl;
    private String selectedAssociateId;
    private String txt_outputUrl;
    private String tagKeyword = "tag=";

    private TextInputEditText inputUrl;
    private Spinner idSelector;
    private RadioGroup radioLongShort;
    private MaterialButton buttonGenerate;
    private TextInputEditText generatedUrl;
    private static TextView textView_productTitle;

    private Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private NavigationView myNavigationView;
    private ClipboardManager clipboardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inputUrl = (TextInputEditText) findViewById(R.id.txtInput_url);
        idSelector = (Spinner) findViewById(R.id.affId_selector);
        buttonGenerate = (MaterialButton) findViewById(R.id.button_generate);
        generatedUrl = (TextInputEditText) findViewById(R.id.txtInput_generatedUrl);
        textView_productTitle = (TextView) findViewById(R.id.textView_productTitle);

        // Setting custom Toolbar or Action bar as default Actionbar
        myToolbar = (Toolbar) findViewById(R.id.Toolbar_myToolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);


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
                    case R.id.nav_myLinks:
                        Intent historyIntent = new Intent(getApplicationContext(), DisplayLinksHistory.class);
                        startActivity(historyIntent);
                }
                menuItem.setChecked(true);
                myDrawerLayout.closeDrawers();
                return true;
            }
        });



        // Clipboard copy
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // ArrayAdapter to populate options(Affiliate Id's) to Spinner
        ArrayAdapter<CharSequence> adapter_affid = ArrayAdapter.createFromResource(this, R.array.array_aff_ids, android.R.layout.simple_spinner_item);
        adapter_affid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idSelector.setAdapter(adapter_affid);
        selectedAssociateId = idSelector.getSelectedItem().toString();

        // Custom fonts
//        Typeface opensans_Regular = Typeface.createFromAsset(getAssets(), "fonts/opensans_regular.ttf");
//        Typeface opensans_Light = Typeface.createFromAsset(getAssets(), "fonts/opensans_light.ttf");


        // Getting data (URL) from share action
        Intent inShareIntent = getIntent();
        String inShareAction = inShareIntent.getAction();
        String inSharetype =  inShareIntent.getType();
        if(Intent.ACTION_SEND.equals(inShareAction) && inSharetype != null){
            if("text/plain".equals(inSharetype)){
                handleSendText(inShareIntent);
            }
        }

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
        String url = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //textView_productTitle.setText("Fetching Product Name...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String title = "";
            url = strings[1];
            Document doc;
            try {
                doc = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36").get();
                Element productTitle = doc.select("span#productTitle").first();
                title = productTitle.text();
            }
            catch (IOException IOe){
                IOe.printStackTrace();
            }

            return title;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ContentValues updateTitleValues = new ContentValues();
            updateTitleValues.put(linksEntry.COLUMN_TITLE, s);
            Uri linkUri = Uri.parse(url);
            int updatedNo = getContentResolver().update(linkUri, updateTitleValues, null, null);
            Toast.makeText(MainActivity.this, "No. of cells updated for Uri " + linkUri + " is " + Integer.toString(updatedNo),Toast.LENGTH_SHORT).show();
            textView_productTitle.setText(s);
        }

    }
//    public void insertDummy(View v){
//        linksDbHelper mDbHelper =  new linksDbHelper(this);
//        SQLiteDatabase mDb =  mDbHelper.getReadableDatabase();
//
//        ContentValues mValues = new ContentValues();
//        mValues.put(linksEntry.COLUMN_DATETIME, "2.27PM - 18/11/2018");
//        mValues.put(linksEntry.COLUMN_TITLE, "Reebok shoe for Men");
//        mValues.put(linksEntry.COLUMN_URL, "https://www.Reebok.com");
//
//        long newRowId = mDb.insert(linksEntry.TABLE_NAME, null, mValues);
//        displayDatabaseInfo(mDb);
//    }
    public void generateClick(View view){
        generateLink();
        addToDb(null);
    }
    public void generateLink(){
        txt_inputUrl = inputUrl.getText().toString();
        if (isValidUrl(txt_inputUrl)){
            if(txt_inputUrl.contains("?")){
                txt_inputUrl = txt_inputUrl.split("\\?")[0];
                txt_outputUrl = txt_inputUrl + "?" + tagKeyword + selectedAssociateId;
                //txt_outputUrl = txt_inputUrl + "&" + tagKeyword + selectedAssociateId;
            }
            else if(txt_inputUrl.charAt(txt_inputUrl.length() - 1) != '/'){
                txt_outputUrl = txt_inputUrl + "/?" + tagKeyword + selectedAssociateId;
            }
            else {
                txt_outputUrl = txt_inputUrl + "?" + tagKeyword + selectedAssociateId;
            }
            generatedUrl.setText(txt_outputUrl);
        }
        else {
            Toast.makeText(this, "Enter a Valid Amazon URL", Toast.LENGTH_SHORT).show();
        }

    }
    public void copyToClipboard(View view){
        ClipData clipDataUrl = ClipData.newPlainText("Link generated by " + R.string.app_name, txt_outputUrl);
        clipboardManager.setPrimaryClip(clipDataUrl);
        Toast.makeText(this, "Link Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    protected boolean isValidUrl(String urlText){
        if(urlText.isEmpty() || !urlText.startsWith("https://www.amazon.")){
            return false;
        }
        else return true;
    }
    protected void handleSendText(Intent intent){
        // Catching Shared text and splitting into URl and extra description (Product title)
        String[] sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).split("https://") ;
        String sharedDescription = sharedText[0];
        String sharedUrl = "https://" + sharedText[1];

//        if(sharedDescription.isEmpty()){
//            Context context = this;
//            ScrapingTask getTitle = new ScrapingTask();
//            getTitle.execute(sharedUrl);
//            sharedDescription = textView_productTitle.getText().toString();
//        }

        if (sharedText != null){
            textView_productTitle.setText("");
            inputUrl.setText("");
            textView_productTitle.setText(sharedDescription);
            inputUrl.setText(sharedUrl);
        }
        generateClick(null);

    }

    public void addToDb(View view){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy (hh:mm a)");
        String timeNow = dateFormat.format(date);
        String title = textView_productTitle.getText().toString();
        String url = generatedUrl.getText().toString();

        ContentValues mValues = new ContentValues();
        mValues.put(linksEntry.COLUMN_DATETIME, timeNow);

        if(title.isEmpty()){
            title = "No Title";
        }
        // Handling Empty title i.e., Avoiding null value in Title
        mValues.put(linksEntry.COLUMN_TITLE, title);


        mValues.put(linksEntry.COLUMN_URL, url);

//        long newLinkEntryId =  mDb.insert(linksEntry.TABLE_NAME, null, mValues);
        Uri newEntryUri = getContentResolver().insert(linksEntry.CONTENT_URI, mValues);

        if(newEntryUri == null){
            Toast.makeText(this, "Failed! Unable to Add link to database", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Success! Link Added to Database", Toast.LENGTH_SHORT).show();
        }

        // If Product Title is Empty, fetch it and update it on Database
        if(title.isEmpty() || title.equals("No Title")){
            ScrapingTask getTitle = new ScrapingTask();
            getTitle.execute(url, newEntryUri.toString());
            //getContentResolver().update();
        }

    }
    public void viewLinkHistory(View view){
        Intent intent = new Intent(this, DisplayLinksHistory.class);
        startActivity(intent);
    }



//    private void displayDatabaseInfo() {
////        // To access our database, we instantiate our subclass of SQLiteOpenHelper
////        // and pass the context, which is the current activity.
////        linksDbHelper mDbHelper = new linksDbHelper(this);
////
////        // Create and/or open a database to read from it
////        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//        String[] projection = {};
//        String selection = "";
//        String[] selectionArgs = {};
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = getContentResolver().query(linksEntry.CONTENT_URI, null, null, null, null);
//
//        cursor.moveToLast();
//        int columnIndexUrl = cursor.getColumnIndex(linksEntry.COLUMN_URL);
//        int columnIndexTitle = cursor.getColumnIndex(linksEntry.COLUMN_TITLE);
//        int columnIndexDate = cursor.getColumnIndex(linksEntry.COLUMN_DATETIME);
//        String lastLine = cursor.getString(columnIndexDate) + " - " + cursor.getString(columnIndexTitle) + " - " + cursor.getString(columnIndexUrl);
//        try {
//            // Display the number of rows in the Cursor (which reflects the number of rows in the
//            // pets table in the database).
//            TextView displayView = (TextView) findViewById(R.id.display_db);
//            displayView.setText("Number of rows in pets database table: " + lastLine);
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }
}
