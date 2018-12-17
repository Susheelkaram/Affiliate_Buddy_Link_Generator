package com.digicular.affiliateBuddy.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class linksContract{

    private linksContract(){}

    public static final String CONTENT_AUTHORITY = "com.digicular.affiliateBuddy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LINKS = "links";

    public static final class linksEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LINKS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINKS;

        public static final String TABLE_NAME = "affiliate_links";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_DATETIME = "date_time";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";

        public static String SQL_CREATE_URLS_TABLE = "CREATE TABLE " + linksEntry.TABLE_NAME + "("
                + linksEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + linksEntry.COLUMN_DATETIME + " TEXT, "
                + linksEntry.COLUMN_TITLE + " TEXT, "
                + linksEntry.COLUMN_URL + " TEXT NOT NULL);";

    }
}
