package com.digicular.affiliateBuddy.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digicular.affiliateBuddy.DisplayLinksHistory;
import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.R;
import com.digicular.affiliateBuddy.SettingsActivity;
import com.digicular.affiliateBuddy.data.AppContract;
import com.digicular.affiliateBuddy.data.linksContract;
import com.digicular.affiliateBuddy.staticActivities.AboutApp;
import com.digicular.affiliateBuddy.staticActivities.HowToUse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Susheel Karam
 * Website - SusheelKaram.com
 */
public class Helpers {
    private static ClipboardManager clipboardManager;

    // This method copies the Generated URL to clipboard
    public static void copyToClipboard(Context context, String content) {
        // Getting Clipboard manager
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (content != null) {
            ClipData clipDataUrl = ClipData.newPlainText(context.getResources().getString(R.string.txt_LabelClipboardCopy), content);
            clipboardManager.setPrimaryClip(clipDataUrl);
            Toast.makeText(context, R.string.txt_SuccessClipboardCopy, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
    }

    // Share Action
    public static void shareNow(Context context, String shareText) {
        if (!shareText.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(shareIntent, "Share your Generated link using: "));
        } else {
            Toast.makeText(context, R.string.txt_EmptyGeneratedUrl, Toast.LENGTH_SHORT).show();
        }
    }

    // Fetches text from Views on MainActivity
    public static HashMap<String, String> getViewValues(Context context) {
        Activity activity = (Activity) context;
        EditText editText_GeneratedUrl = (EditText) activity.findViewById(R.id.txtInput_generatedUrl);
        TextView textView_ProductTitle = (TextView) activity.findViewById(R.id.textView_productTitle);
        EditText editText_InputUrl = (EditText) activity.findViewById(R.id.txtInput_url);
        TextView textView_appMode = (TextView) activity.findViewById(R.id.TextView_AppMode);

        String title = textView_ProductTitle.getText().toString();
        String inUrl = editText_InputUrl.getText().toString();
        String outUrl = editText_GeneratedUrl.getText().toString();
        String appMode = textView_appMode.getText().toString();

        HashMap<String, String> viewValues = new HashMap<>();
        viewValues.put("title", title);
        viewValues.put("inUrl", inUrl);
        viewValues.put("outUrl", outUrl);
        viewValues.put("appMode", appMode);

        return viewValues;
    }

    // Adds Generated link entry to Database
    public static void addToDb(Context context) {
        Activity activity = (Activity) context;

        HashMap<String, String> viewValues = getViewValues(context);

        // TextView textView_productTitle = (TextView) mActivity.findViewById(R.id.textView_productTitle);
        // EditText generatedUrl = (EditText) mActivity.findViewById(R.id.txtInput_generatedUrl);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy (hh:mm a)");

        String timeNow = dateFormat.format(date);
        String programName = viewValues.get("appMode");
        String title = viewValues.get("title");
        String url = viewValues.get("outUrl");
        // Handling Empty title i.e., Avoiding null value in Title
        if (title.isEmpty()) {
            title = "No Title";
        }

        ContentValues mValues = new ContentValues();
        mValues.put(linksContract.linksEntry.COLUMN_DATETIME, timeNow);
        mValues.put(linksContract.linksEntry.COLUMN_PROGRAM, programName);
        mValues.put(linksContract.linksEntry.COLUMN_URL, url);
        mValues.put(linksContract.linksEntry.COLUMN_TITLE, title);

        // Inserting a new Link entry and getting its Uri
        Uri newLinkUri = context.getContentResolver().insert(linksContract.linksEntry.CONTENT_URI, mValues);

        if (newLinkUri == null) {
            Log.d("DB_INSERT", "Failed! Unable to Add link to database");
//            Toast.makeText(context, "Failed! Unable to Add link to database", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("DB_INSERT", "Success! Link Added to Database");
        }

        // If Product Title is Empty, fetch it and update it on Database
        if (title.isEmpty() || title.equals("No Title")) {
            TitleFetcher getTitle = new TitleFetcher(context);
            getTitle.execute(url, newLinkUri.toString(), Integer.toString(AppContract.getModeCode(programName)));
        }

    }

    protected void setupActionBar(Context context){
        AppCompatActivity activity = (AppCompatActivity) context;
        // Setting custom Toolbar or Action bar as default Actionbar
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

}
