package com.digicular.affiliateBuddy.data;

import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import android.content.ContentUris;
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

import com.digicular.affiliateBuddy.R;

public class AffIdCursorAdapter extends CursorAdapter{
    public AffIdCursorAdapter(Context context, Cursor cursor){
        super(context,cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_affiliate_id, parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView affId_entry = (TextView) view.findViewById(R.id.textView_affId_entry);
        Button delete_affId = (Button) view.findViewById(R.id.button_delete_affId);

        String affId = cursor.getString(cursor.getColumnIndexOrThrow(linksEntry.AFFID_COLUMN_IDTAG));
        int _id = cursor.getInt(cursor.getColumnIndexOrThrow(linksEntry.AFFID_COLUMN_ID));
        final Uri currentEntryUri = Uri.withAppendedPath(linksEntry.AFFID_CONTENT_URI, Integer.toString(_id));

        affId_entry.setText(affId);
        delete_affId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deletedEntries = context.getContentResolver().delete(currentEntryUri, null, null);

                if(deletedEntries == 0){
                    Toast.makeText(context, "Unable to delete the ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "ID deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
