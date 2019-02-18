package com.digicular.affiliateBuddy.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class linksContract{

    private linksContract(){}

    public static final String CONTENT_AUTHORITY = "com.digicular.affiliateBuddy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LINKS = "links";

    // Table Names for Affiliate id's of different sites
    public static final String PATH_AFFID = "aff_ids";

//    public static final String FLIPKART_AFFID = "flipkart_ids";
//    public static final String AMAZON_AFFID = "amazon_ids";
//    public static final String GEARBEST_AFFID = "gearbest_ids";

    public static final class linksEntry implements BaseColumns {

        // Links History Table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LINKS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINKS;

        public static final String TABLE_NAME = "affiliate_links";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DATETIME = "date_time";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PROGRAM = "program_name";
        public static final String COLUMN_URL = "url";

        public static String SQL_CREATE_URLS_TABLE = "CREATE TABLE " + linksEntry.TABLE_NAME + "("
                + linksEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + linksEntry.COLUMN_DATETIME + " TEXT, "
                + linksEntry.COLUMN_PROGRAM + " TEXT, "
                + linksEntry.COLUMN_TITLE + " TEXT, "
                + linksEntry.COLUMN_URL + " TEXT NOT NULL);";


        // Affiliate IDs Table
        public static final Uri AFFID_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_AFFID);
        public static final String AFFID_CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AFFID;
        public static final String AFFID_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AFFID;

        public static final String AFFID_TABLE_NAME = "affiliate_ids";
        public static final String AFFID_COLUMN_ID =  BaseColumns._ID;
        public static final String AFFID_COLUMN_IDTAG =  "identifier";
        public static final String AFFID_COLUMN_PROGRAM_NAME = "program_name";

        public static final String AFFID_SQL_CREATE_TABLE = "CREATE TABLE " + linksEntry.AFFID_TABLE_NAME + "("
                + linksEntry.AFFID_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + linksEntry.AFFID_COLUMN_IDTAG + " TEXT NOT NULL, "
                + linksEntry.AFFID_COLUMN_PROGRAM_NAME + " TEXT NOT NULL);";
    }
}
