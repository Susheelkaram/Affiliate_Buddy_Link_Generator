package com.digicular.affiliateBuddy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.text.Selection;
import android.util.Log;
import android.widget.Toast;

import com.digicular.affiliateBuddy.MainActivity;
import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

/*
* Contains Methods for CRUD operations for:
* 1) Generated Affiate Links table
* 2) Affiliate IDs/TAGs table
* */
public class linksProvider extends ContentProvider{

    public static final String LOG_TAG = linksProvider.class.getSimpleName();
    private static final int LINKS = 100;
    private static final int LINK_ID = 101;
    private static final int AFFIDS = 102;
    private static final int AFFID_ID = 103;

    private linksDbHelper mDbHelper;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_LINKS, LINKS);
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_LINKS + "/#", LINK_ID);

        // For Affiliate IDs Table
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_AFFID, AFFIDS);
        sUriMatcher.addURI(linksContract.CONTENT_AUTHORITY, linksContract.PATH_AFFID + "/#", AFFID_ID);
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

            // Cases for Affiliate IDs/TAGs table queries
            case AFFIDS:
                cursor = database.query(linksEntry.AFFID_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case AFFID_ID:
                selection = linksEntry.AFFID_COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(linksEntry.AFFID_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
            // Cases for Affiliate Ids table
            case AFFIDS:
                return insertAffId(uri, contentValues);
            default:
                Log.d(LOG_TAG,"Insertion is not supported for " + uri);
        }
        return null;
    }
    private Uri insertLink(Uri uri, ContentValues contentValues){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String title = contentValues.getAsString(linksEntry.COLUMN_TITLE);
        String link =  contentValues.getAsString(linksEntry.COLUMN_URL);
        Uri newEntryUri;

        // Avoiding entries with empty URLs
        if(link.isEmpty()){
            Toast.makeText(getContext(), "Url should not be empty", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"Url should not be empty");
            return null;
        }

        // Inserting in to database and get the ID of the New entry
        long newRowId = database.insert(linksEntry.TABLE_NAME, null, contentValues);

        // Showing Log Message on Failed insertion
        if(newRowId == -1){
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        newEntryUri = ContentUris.withAppendedId(uri,newRowId);
        if(newEntryUri != null){
            getContext().getContentResolver().notifyChange(newEntryUri, null);
        }

        return newEntryUri;
    }

    /**
    * Inserting Affid in to Affiliate ids table
    */
    private Uri insertAffId(Uri uri, ContentValues contentValues){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String affiliate_id = contentValues.getAsString(linksEntry.AFFID_COLUMN_IDTAG);
        String program_name = contentValues.getAsString(linksEntry.AFFID_COLUMN_PROGRAM_NAME);
        Uri newEntryUri;

        // Avoiding entries with empty URLs
        if(affiliate_id == null || program_name == null){
            Toast.makeText(getContext(), "Enter a valid Affiliate/Link ID", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Inserting into DB
        long newRowId = database.insert(linksEntry.AFFID_TABLE_NAME, null, contentValues);

        // Showing Log Message on Failed insertion
        if(newRowId == -1){
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        newEntryUri = ContentUris.withAppendedId(uri,newRowId);
        if(newEntryUri != null){
            getContext().getContentResolver().notifyChange(newEntryUri, null);
        }

        return newEntryUri;
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
                break;
            case LINK_ID:
                selection = linksEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateLink(uri, contentValues, selection, selectionArgs);
                break;

            // Add cases for Affiliate Ids table
            case AFFIDS:
                rowsUpdated = updateAffId(uri, contentValues, selection, selectionArgs);
                break;
            case AFFID_ID:
                selection = linksEntry.AFFID_COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = updateAffId(uri, contentValues, selection, selectionArgs);
                break;
            default:
                Log.e(LOG_TAG, "Update not supported for " + uri);
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
//            return 0;
//        }
        return database.update(linksEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
     private int updateAffId(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
         SQLiteDatabase database = mDbHelper.getWritableDatabase();
         String affiliate_id = contentValues.getAsString(linksEntry.AFFID_COLUMN_IDTAG);
         String program_name = contentValues.getAsString(linksEntry.AFFID_COLUMN_PROGRAM_NAME);
         if(affiliate_id.isEmpty() || program_name.isEmpty()){
             Toast.makeText(getContext(), "Enter a valid Affiliate/Link ID", Toast.LENGTH_SHORT).show();
             return 0;
         }
         return database.update(linksEntry.AFFID_TABLE_NAME, contentValues, selection, selectionArgs);
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
                break;
            case LINK_ID:
                selection = linksEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(linksEntry.TABLE_NAME, selection, selectionArgs);
                break;

            // TODO: Add cases for Affiliate Ids table
            case AFFIDS:
                rowsDeleted = database.delete(linksEntry.AFFID_TABLE_NAME, selection, selectionArgs);
                break;
            case AFFID_ID:
                selection = linksEntry.AFFID_COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(linksEntry.AFFID_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                Log.d(LOG_TAG, "Deletion not supported for URI " + uri);
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

            // Add cases for Affiliate Ids table
            case AFFIDS:
                return linksEntry.AFFID_CONTENT_LIST_TYPE;
            case AFFID_ID:
                return linksEntry.AFFID_CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Type unknown for URI " + uri + " with match " + matchCode);
        }
    }
}
