package com.digicular.affiliateBuddy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.Selection;
import android.util.Log;
import android.widget.Toast;

import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;


public class linksProvider extends ContentProvider{

    public static final String LOG_TAG = linksProvider.class.getSimpleName();
    private static final int LINKS = 100;
    private static final int LINK_ID = 101;
    private linksDbHelper mDbHelper;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_LINKS, LINKS);
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_LINKS + "/#", LINK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper =  new linksDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        final int matchCode = sUriMatcher.match(uri);

        switch (matchCode){
            case LINKS:
                cursor = database.query(linksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case LINK_ID:
                selection = linksEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(linksEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot resolve invalid URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int matchCode = sUriMatcher.match(uri);

        switch (matchCode) {
            case LINKS:
                return insertLink(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertLink(Uri uri, ContentValues contentValues){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String title = contentValues.getAsString(linksEntry.COLUMN_TITLE);
        String link =  contentValues.getAsString(linksEntry.COLUMN_URL);
        if(link.isEmpty()){
            throw new IllegalArgumentException("Url should not be empty");
        }
        long id = database.insert(linksEntry.TABLE_NAME, null, contentValues);
        if(id == -1){
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        final int matchCode =  sUriMatcher.match(uri);
        switch(matchCode){
            case LINKS:
                rowsUpdated = updateLink(uri, contentValues, selection, selectionArgs);
            case LINK_ID:
                selection = linksEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateLink(uri, contentValues, selection, selectionArgs);

            default:
                Log.d(LOG_TAG, "Update not supported for " + uri);
        }
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    private int updateLink(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String link = contentValues.getAsString(linksEntry.COLUMN_URL);
//        if(link == null){
//            Log.d(LOG_TAG, "Failed to update row(s) for " + uri);
//            Toast.makeText(getContext(),"Failed to update row(s) for " + uri, Toast.LENGTH_SHORT).show();
//            return 0;
//        }
//        if(contentValues.size() == 0){
//            Log.d(LOG_TAG, "No new changes to update.");
//            Toast.makeText(getContext(),"No new changes to update." , Toast.LENGTH_SHORT).show();
//
//            return 0;
//        }
       return database.update(linksEntry.TABLE_NAME, contentValues, selection, selectionArgs);

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int matchCode = sUriMatcher.match(uri);
        switch (matchCode){
            case LINKS:
                rowsDeleted =  database.delete(linksEntry.TABLE_NAME, selection, selectionArgs);
            case LINK_ID:
                selection = linksEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(linksEntry.TABLE_NAME, selection, selectionArgs);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        int matchCode = sUriMatcher.match(uri);
        switch (matchCode){
            case LINKS:
                return linksEntry.CONTENT_LIST_TYPE;
            case LINK_ID:
                return linksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Type unknown for URI " + uri + " with match " + matchCode);
        }
    }
}
