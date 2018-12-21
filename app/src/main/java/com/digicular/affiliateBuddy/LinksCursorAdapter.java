package com.digicular.affiliateBuddy;
import com.digicular.affiliateBuddy.data.linksContract;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LinksCursorAdapter extends CursorAdapter{


    public LinksCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.item_link, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // TODO: Fill out this method
        // Getting textviews for individual Link item
        TextView hTitle = (TextView) view.findViewById(R.id.textView_hTitle);
        TextView hUrl = (TextView) view.findViewById(R.id.textView_hUrl);
        Button hCopyButton = (Button) view.findViewById(R.id.button_hCopy);
        Button hDeleteButton = (Button) view.findViewById(R.id.button_hDelete);


        // Getting title and url in current row
        String title = cursor.getString(cursor.getColumnIndexOrThrow(linksContract.linksEntry.COLUMN_TITLE));
        final String url = cursor.getString(cursor.getColumnIndexOrThrow(linksContract.linksEntry.COLUMN_URL));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(linksContract.linksEntry.COLUMN_ID));
        final Uri currentEntryUri =  Uri.withAppendedPath(linksContract.linksEntry.CONTENT_URI, Integer.toString(id));


        // Setting values to Textviews
        hTitle.setText(title);
        hUrl.setText(url);
        hCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipDataHUrl = ClipData.newPlainText("Link generated by " + R.string.app_name, url);
                clipboardManager.setPrimaryClip(clipDataHUrl);
                Toast.makeText(context, "Link Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        hDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deletedRows = context.getContentResolver().delete(currentEntryUri, null, null);

                if(deletedRows == 0){
                    Toast.makeText(context, "Unable to delete the Link", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Link deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}