package com.digicular.affiliateBuddy.data;

import com.digicular.affiliateBuddy.data.linksContract.linksEntry;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digicular.affiliateBuddy.R;

public class AffIdRecyclerCursorAdapter extends RecyclerView.Adapter<AffIdRecyclerCursorAdapter.AffIdViewHolder> {
    Context mContext;
    Cursor mCursor;

    public AffIdRecyclerCursorAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public class AffIdViewHolder extends RecyclerView.ViewHolder{
        TextView TV_affIdText;
        Button Button_deleteAffId;
        public AffIdViewHolder(@NonNull View itemView) {
            super(itemView);
            TV_affIdText = (TextView) itemView.findViewById(R.id.textView_affId_entry);
            Button_deleteAffId = (Button) itemView.findViewById(R.id.button_delete_affId);
        }
    }

    @NonNull
    @Override
    public AffIdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        View view = layoutInflater.inflate(R.layout.item_affiliate_id, parent, false);
        View view = layoutInflater.inflate(R.layout.item_affiliate_id, parent, false);
        return new AffIdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AffIdViewHolder affIdViewHolder, final int position) {
        mCursor.moveToPosition(position);

        final String affIdTag = mCursor.getString(mCursor.getColumnIndexOrThrow(linksEntry.AFFID_COLUMN_IDTAG));
        long current_affId_ID = mCursor.getLong(mCursor.getColumnIndexOrThrow(linksEntry.AFFID_COLUMN_ID));
        final Uri current_affIdURI = ContentUris.withAppendedId(linksEntry.AFFID_CONTENT_URI, current_affId_ID);

        affIdViewHolder.TV_affIdText.setText(affIdTag);
        affIdViewHolder.Button_deleteAffId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Asking for confirmation to delete Addiliate ID
                AlertDialog.Builder alertDialogBuilder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.DialogStyle);
                } else {
                    alertDialogBuilder = new AlertDialog.Builder(mContext);
                }
                alertDialogBuilder.setTitle(R.string.txt_deleteDialogTitle);
                alertDialogBuilder.setMessage(R.string.txt_deleteDialogMessage);
                alertDialogBuilder.setPositiveButton(R.string.txt_deleteDialogPositive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deletedEntries = mContext.getContentResolver().delete(current_affIdURI, null, null);

                        if(deletedEntries == 0){
                            Toast.makeText(mContext, "Unable to delete the ID", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(mContext, "ID deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.txt_deleteDialogNegative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                AlertDialog deleteDialog = alertDialogBuilder.create();

                deleteDialog.show();
                Button positiveButton = deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negativeButton.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
//                negativeButton.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                LinearLayout.LayoutParams  LPGravity = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LPGravity.rightMargin = 20;
                negativeButton.setLayoutParams(LPGravity);
            }
        });

    }

    @Override
    public int getItemCount() {
//        return (mCursor != null) ? (mCursor.getCount()) : 0;
        if(mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }


    public void swapCursor(Cursor cursor){

    }

}
