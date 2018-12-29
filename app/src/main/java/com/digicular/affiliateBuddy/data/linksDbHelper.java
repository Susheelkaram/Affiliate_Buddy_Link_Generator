package com.digicular.affiliateBuddy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

public final class linksDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = linksDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "links" ;
    public static final int DATABASE_VERSION = 1 ;


    public linksDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDb) {
        // Creating Links table
        sqliteDb.execSQL(linksEntry.SQL_CREATE_URLS_TABLE);

        // Creating Affiliate Ids table
        sqliteDb.execSQL(linksEntry.AFFID_SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
         // Deleting and Creating Links table if already exists
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + linksEntry.TABLE_NAME);

         // Deleting and Creating Affiliate Id's table if already exists
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + linksEntry.AFFID_TABLE_NAME);
         onCreate(sqLiteDatabase);
    }
}
