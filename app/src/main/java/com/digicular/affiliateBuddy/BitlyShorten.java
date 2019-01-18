package com.digicular.affiliateBuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitly.Bitly;
import com.bitly.Error;
import com.bitly.Response;

public class BitlyShorten extends AppCompatActivity {
    EditText etShortLink;
    Button btnShorten;
    TextView tvShortLink;
    public static final String LOG_TAG = "BitlyShorten.class";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitly_shorten);

        etShortLink = (EditText) findViewById(R.id.ET_LongLink);
        btnShorten = (Button) findViewById(R.id.BTN_Shorten);
        tvShortLink = (TextView) findViewById(R.id.TV_ShortLink);

        Bitly.initialize(this, "3f1fa331442b1cad7ad50c76c788bff6daf45b44");

        btnShorten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lonkLink = etShortLink.getText().toString();
                if (lonkLink != null){
                    Bitly.shorten(lonkLink, new Bitly.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            String shortLink = response.getBitlink();
                            tvShortLink.setText(shortLink);
                        }

                        @Override
                        public void onError(Error error) {
                            //Toast.makeText(getApplicationContext(), "Error: Cannot create Bitlink", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Bitlink_error: " + error.getErrorMessage());
                        }
                    });
                }
            }
        });

    }
}
