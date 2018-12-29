package com.digicular.affiliateBuddy;

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

import android.widget.ListView;
import android.widget.TextView;

import com.digicular.affiliateBuddy.data.linksContract;

public class DisplayLinksHistory extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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

        // Tracking changes in DB and updating in View
        getSupportLoaderManager().initLoader(LINKS_LOADER, null, this);

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
//        String sortOrder = linksContract.linksEntry.COLUMN_ID + " DESC";
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = getContentResolver().query(linksContract.linksEntry.CONTENT_URI, null, null, null, sortOrder);
///*
//        cursor.moveToLast();
//        int columnIndexUrl = cursor.getColumnIndex(linksContract.linksEntry.COLUMN_URL);
//        int columnIndexTitle = cursor.getColumnIndex(linksContract.linksEntry.COLUMN_TITLE);
//        int columnIndexDate = cursor.getColumnIndex(linksContract.linksEntry.COLUMN_DATETIME);
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
//*/      //empty_View.setVisibility(View.GONE);
//        ListView lvLinks = (ListView) findViewById(R.id.lv_linksHistory);
//        View empty_View = (View) findViewById(R.id.emptyView);
//
//        lvLinks.setEmptyView(empty_View);
//
//        linksCursorAdapter = new LinksCursorAdapter(this, cursor);
//        lvLinks.setAdapter(linksCursorAdapter);
//    }

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
