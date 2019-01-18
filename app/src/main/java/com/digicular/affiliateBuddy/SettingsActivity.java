package com.digicular.affiliateBuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.lang.reflect.Array;

public class SettingsActivity extends BaseAppCompatActivity {
    ListView LV_settingAffids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LV_settingAffids = (ListView) findViewById(R.id.LV_SettingsAffIDListView);

        getSupportActionBar().setTitle("Settings");

        ArrayAdapter<CharSequence> affidSettings = ArrayAdapter.createFromResource(this, R.array.sa_settingsAffid, android.R.layout.simple_list_item_1);
        LV_settingAffids.setAdapter(affidSettings);

        LV_settingAffids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOptionText = parent.getItemAtPosition(position).toString().toLowerCase();

                Intent configureAffIdIntent = new Intent(getApplicationContext(), Affiliate_id_editor.class);
                configureAffIdIntent.putExtra("SITE_NAME", selectedOptionText);
                startActivity(configureAffIdIntent);
            }
        });

    }

}
