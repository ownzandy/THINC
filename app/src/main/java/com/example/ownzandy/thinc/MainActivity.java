package com.example.ownzandy.thinc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private HashMap<String, ArrayList<String>> myData;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get data
        myData = (HashMap<String, ArrayList<String>>) this.getIntent().getExtras().get("myData");
        pass = this.getIntent().getExtras().getString("authKey");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_camera_alt_white_12dp);

        //Dynamically change patient name/title of page
        ArrayList<String> nameData = myData.get("name");
        String name = "";
        for (String s : nameData) {
            name += s + " ";
        }
        getSupportActionBar().setTitle(name.trim());
        //Toast.makeText(this, name.trim(),Toast.LENGTH_LONG).show();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent QRIntent = new Intent(MainActivity.this, QRActivity.class);
                QRIntent.putExtra("authKey", pass);
                startActivity(QRIntent);
            }
        });


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Allergies"));
        tabLayout.addTab(tabLayout.newTab().setText("Medication"));
        tabLayout.addTab(tabLayout.newTab().setText("Diagnosis"));
        tabLayout.addTab(tabLayout.newTab().setText("Procedures"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //fragments call this method to get data
    public HashMap<String, ArrayList<String>> getDataMap(){
        return myData;
    }
}
