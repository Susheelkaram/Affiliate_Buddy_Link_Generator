package com.digicular.affiliateBuddy;

import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.digicular.affiliateBuddy.data.AffIdCursorAdapter;

public class Affiliate_id_editor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int AFFID_LOADER = 0;

    private static AffIdCursorAdapter affIdCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiliate_id_editor);

        ListView lv_affIds = (ListView) findViewById(R.id.listView_affIds);
        final EditText et_affIdInput = (EditText) findViewById(R.id.editText_AffIdInput);
        Button bt_addAffId = (Button) findViewById(R.id.button_addAffId);

        affIdCursorAdapter = new AffIdCursorAdapter(this, null);
        lv_affIds.setAdapter(affIdCursorAdapter);

        getSupportLoaderManager().initLoader(AFFID_LOADER, null, this);

        bt_addAffId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String affIdText = et_affIdInput.getText().toString();
                String programName = "amazon";
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
        return new CursorLoader(this, linksEntry.AFFID_CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        affIdCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        affIdCursorAdapter.swapCursor(null);
    }
}
