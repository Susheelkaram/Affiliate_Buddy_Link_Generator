package com.digicular.affiliateBuddy;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.ListView;
import android.widget.TextView;

import com.digicular.affiliateBuddy.data.linksContract;

public class DisplayLinksHistory extends BaseAppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LINKS_LOADER = 0;

    private static LinksCursorAdapter linksCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_links_history);

        ListView lvLinks = (ListView) findViewById(R.id.lv_linksHistory);
        View empty_View = (View) findViewById(R.id.emptyView);


        // Setting Empty view to show when the listView is empty
        lvLinks.setEmptyView(empty_View);

        linksCursorAdapter = new LinksCursorAdapter(this, null);
        lvLinks.setAdapter(linksCursorAdapter);

        // Tracking changes in DB and updating in View (CursorLoader)
        getSupportLoaderManager().initLoader(LINKS_LOADER, null, this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {};
        String sortOrder = linksContract.linksEntry.COLUMN_ID + " DESC";
        return new CursorLoader(this, linksContract.linksEntry.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        linksCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        linksCursorAdapter.swapCursor(null);
    }
}
