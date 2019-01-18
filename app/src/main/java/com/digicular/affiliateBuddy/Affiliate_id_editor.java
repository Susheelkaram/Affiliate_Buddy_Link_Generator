package com.digicular.affiliateBuddy;

import com.digicular.affiliateBuddy.data.AffIdRecyclerCursorAdapter;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.digicular.affiliateBuddy.data.AffIdCursorAdapter;

public class Affiliate_id_editor extends BaseAppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int AFFID_LOADER = 0;
    protected static String programName = "";
    protected static String[] selectionArgs = new String[1];
    public RecyclerView recyclerView_affIds;

//    private static AffIdCursorAdapter affIdCursorAdapter;
    private  static AffIdRecyclerCursorAdapter affIdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiliate_id_editor);

//        ListView lv_affIds = (ListView) findViewById(R.id.listView_affIds);
        recyclerView_affIds = (RecyclerView) findViewById(R.id.recyclerView_ExistingIds);
        recyclerView_affIds.setLayoutManager(new LinearLayoutManager(this));

        final EditText et_affIdInput = (EditText) findViewById(R.id.editText_AffIdInput);
        Button bt_addAffId = (Button) findViewById(R.id.button_addAffId);

//        affIdCursorAdapter = new AffIdCursorAdapter(this, null);
//        lv_affIds.setAdapter(affIdCursorAdapter)

        Intent programNameIntent = getIntent();
        String intentProgramName = programNameIntent.getStringExtra("SITE_NAME");
        programName = intentProgramName;
        selectionArgs[0] = intentProgramName;

        // Setting Actionbar Title
        String activityTitle = programName.substring(0,1).toUpperCase() + programName.substring(1) + " Setup";
        getSupportActionBar().setTitle(activityTitle);

        getSupportLoaderManager().initLoader(AFFID_LOADER, null, this);

        bt_addAffId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String affIdText = et_affIdInput.getText().toString().replaceAll("\\s+","");
                et_affIdInput.getText().clear();
                if(!affIdText.isEmpty()){
                    ContentValues values = new ContentValues();
                    values.put(linksEntry.AFFID_COLUMN_PROGRAM_NAME, programName);
                    values.put(linksEntry.AFFID_COLUMN_IDTAG, affIdText);

                    getContentResolver().insert(linksEntry.AFFID_CONTENT_URI, values);
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String sortOrder = linksEntry.AFFID_COLUMN_ID + " DESC";
        String selection = linksEntry.AFFID_COLUMN_PROGRAM_NAME + "=?";
        return new CursorLoader(this, linksEntry.AFFID_CONTENT_URI, null, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
//        affIdCursorAdapter.swapCursor(cursor);
        cursor.moveToFirst();
        affIdAdapter = new AffIdRecyclerCursorAdapter(this, cursor);
        recyclerView_affIds.setAdapter(affIdAdapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        affIdCursorAdapter.swapCursor(null);
    }
}
