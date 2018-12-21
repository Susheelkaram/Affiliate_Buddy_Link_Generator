package com.digicular.affiliateBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
/*
* @author Susheel Karam (Digicular.com)
*
* */
public class BaseAppCompatActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private DrawerLayout myDrawerLayout;
    private NavigationView myNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar);

        // Setting custom Toolbar or Action bar as default Actionbar
        myToolbar = (Toolbar) findViewById(R.id.Toolbar_myToolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

//
//        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
//        myNavigationView = (NavigationView) findViewById(R.id.myNavigationView);
//        myNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.nav_home:
//                        Intent homeIntent =  new Intent(getApplicationContext(), MainActivity.class);
//                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(homeIntent);
//                    case R.id.nav_myLinks:
//                        Intent historyIntent = new Intent(getApplicationContext(), DisplayLinksHistory.class);
//                        startActivity(historyIntent);
//                }
//                menuItem.setChecked(true);
//                myDrawerLayout.closeDrawers();
//                return true;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                myDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
