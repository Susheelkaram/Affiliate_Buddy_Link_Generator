package com.digicular.affiliateBuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digicular.affiliateBuddy.data.AppContract;

import org.json.JSONObject;

import java.lang.reflect.Array;

/*
 * @author Susheel Karam
 * Website - SusheelKaram.com
 * */

public class SettingsActivity extends BaseAppCompatActivity {
    ListView LV_settingAffids;
    TextView TV_bitlyAccountStatus;
    Button BTN_LinkAccount;
    Button BTN_RemoveAccount;
    Switch SW_AutoShorten;

    private static final String BITLY_AUTH_URI = "https://bitly.com/oauth/authorize?client_id=53af793b53f7b9b80b2fbda82b1decebb6bb127c&redirect_uri=affo://bitly.setup/";

    private static final String BASE_URL = "https://api-ssl.bitly.com/oauth/access_token?";
    private static final String CLIENT_ID = "53af793b53f7b9b80b2fbda82b1decebb6bb127c";
    private static final String CLIENT_SECRET = "2fd3c6079747818ddf27262aa1b1a23ac313005b";
    private static final String REDIRECT_URI = "affo://bitly.setup/";

    private static String accessInfo;

    private static String token;
    private static String loginName;
    private Context mContext = this;

    private SharedPreferences appPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Getting required Views
        LV_settingAffids = (ListView) findViewById(R.id.LV_SettingsAffIDListView);

        TV_bitlyAccountStatus = (TextView) findViewById(R.id.textView_BitlyLinkStatus) ;
        BTN_LinkAccount = (Button) findViewById(R.id.btn_LinkBitlyAccount);
        BTN_RemoveAccount = (Button) findViewById(R.id.btn_RemoveBitlyAccount);
        SW_AutoShorten = (Switch) findViewById(R.id.switch_AutoShorten);

        // App Preferences
        appPreferences = this.getSharedPreferences(AppContract.PREFS_APP_SETTINGS, MODE_PRIVATE);
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch(key){

                    case AppContract.PREF_BITLY_TOKEN:
                        String currentBitlyToken = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);
                        if(currentBitlyToken != null) {
                            TV_bitlyAccountStatus.setText("Account Linked\n" + currentBitlyToken);
                            BTN_LinkAccount.setVisibility(View.GONE);
                            BTN_RemoveAccount.setVisibility(View.VISIBLE);
                        }
                        else {
                            SW_AutoShorten.setChecked(false);
                            TV_bitlyAccountStatus.setText("Account not linked");
                            TV_bitlyAccountStatus.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                        }
                        break;
                }
            }
        };
        appPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        // Retrieving and Displaying Bitly Account linking status
        final String currentBitlyToken = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);
        String currentBitlyLogin = appPreferences.getString(AppContract.PREF_BITLY_NAME, null);
        boolean autoShortenPref = appPreferences.getBoolean(AppContract.PREF_AUTO_SHORTEN, false);

        Log.i("BITLY_SP: ", "Token = " + currentBitlyToken + "\nLogin = " + currentBitlyLogin);

        if(currentBitlyToken != null && currentBitlyLogin != null) {
            BTN_LinkAccount.setVisibility(View.GONE);
            BTN_RemoveAccount.setVisibility(View.VISIBLE);

            TV_bitlyAccountStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp,0);
            TV_bitlyAccountStatus.setText("Account Linked\n" + currentBitlyToken);
            BTN_LinkAccount.setText("Logged in as " + currentBitlyLogin.toUpperCase());
        }
        SW_AutoShorten.setChecked(autoShortenPref);

        // Setting Adapter for ListView of "Sites" to configure Affiliate ID's
        ArrayAdapter<CharSequence> affidSettings = ArrayAdapter.createFromResource(this, R.array.sa_settingsAffid, android.R.layout.simple_list_item_1);
        LV_settingAffids.setAdapter(affidSettings);

        // Auto-shorten Switch
        SW_AutoShorten.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String currentBitlyToken = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);
                if (currentBitlyToken != null) {
                    Editor editor = appPreferences.edit();
                    editor.putBoolean(AppContract.PREF_AUTO_SHORTEN, isChecked);
                    editor.apply();
                }
                else {
                    SW_AutoShorten.setChecked(false);
                    Toast.makeText(getApplicationContext(), "Please link your Bit.ly account first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Bitly setup button action
        BTN_LinkAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri authUri = Uri.parse(BITLY_AUTH_URI);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, authUri);
                startActivity(browserIntent);
            }
        });

        // Disconnect/ Removing existing Bit.ly account
        BTN_RemoveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Asking for confirmation to delete Addiliate ID
                AlertDialog.Builder alertDialogBuilder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.DialogStyle);
                } else {
                    alertDialogBuilder = new AlertDialog.Builder(mContext);
                }
                alertDialogBuilder.setTitle(R.string.txt_confirmBitlyRemoveTitle);
                alertDialogBuilder.setMessage(R.string.txt_confirmBitlyRemove);

                // If User confirms to DELETE
                alertDialogBuilder.setPositiveButton(R.string.txt_deleteDialogPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editor prefEditor = appPreferences.edit();
                        prefEditor.putString(AppContract.PREF_BITLY_NAME, null);
                        prefEditor.putString(AppContract.PREF_BITLY_TOKEN, null);
                        prefEditor.putBoolean(AppContract.PREF_AUTO_SHORTEN, false);
                        prefEditor.apply();

                        SW_AutoShorten.setChecked(false);
                        BTN_LinkAccount.setVisibility(View.VISIBLE);
                        BTN_RemoveAccount.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.txt_successAccountRemoved, Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.txt_deleteDialogNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                AlertDialog deleteDialog = alertDialogBuilder.create();
                deleteDialog.show();
            }
        });

        // Launching Activity to Add/Edit/Delete Affiliate IDs
        // for a specific Site based on User selection
        LV_settingAffids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOptionText = parent.getItemAtPosition(position).toString().toLowerCase();

                Intent configureAffIdIntent = new Intent(getApplicationContext(), Affiliate_id_editor.class);
                configureAffIdIntent.putExtra("SITE_NAME", selectedOptionText);
                startActivity(configureAffIdIntent);
            }
        });


        // Getting Code from Intent and
        // Fetching AccessToken and Login name
        Uri data = this.getIntent().getData();
        String code = "";
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            code = uri.split("/?code=")[1];

            String CODE = code;
            if (CODE != null) {
                getAccessInfo(CODE, this);
            }
        }

    }


    // This method gets Bit.ly AccessToken by using
    // 'code' retrieved from Intent
    protected void getAccessInfo(String code, Context context){
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        // HTTP POST request Volley
        String requestURI = BASE_URL + "client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET + "&code=" + code + "&redirect_uri=" + REDIRECT_URI;
        Log.i("Request: ", requestURI);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String token;
                String login;
                Log.i("Response", "onResponse: " + response);

                String[] data = response.split("(access_token=)|(&login=)|(&apiKey=)");
                token = data[1];
                login = data[2];
                saveTokenLogin(token,login);
                Log.i("ACCESS_INFO", "onResponse: " + token + " - " + login);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error: ", error.toString());
            }
        });

        requestQueue.add(stringRequest);
//        Log.i("ACCESS_INFO2", "onResponse: " + accessInfo);
    }

    protected void saveTokenLogin(String token, String login){
        Log.i("BITLY: ", "Token = " + token + "\nLogin = " + loginName);

        // Saving Access Token to App Preferences
        Editor prefEditor = appPreferences.edit();
        String existingBitlyToken = appPreferences.getString(AppContract.PREF_BITLY_TOKEN, null);
        if (existingBitlyToken == null) {
            prefEditor.putString(AppContract.PREF_BITLY_TOKEN, token);
            prefEditor.putString(AppContract.PREF_BITLY_NAME, login);
            prefEditor.apply();
            Toast.makeText(getApplicationContext(), R.string.txt_successAccountLinked, Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(getApplicationContext(), R.string.txt_accountAlreadyLinked, Toast.LENGTH_SHORT).show();
        }
    }

}
