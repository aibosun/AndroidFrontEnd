package com.usc.aibo.eventsearchmbl;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.util.Log;

import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionMainPageAdapter sectionMainPageAdapter;
    private ViewPager viewMainPager;

    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private SharedPreferences sprefLatlon;
    private SharedPreferences.Editor editorLalon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spref = getSharedPreferences("favoriteInfo",Context.MODE_PRIVATE);
        editor = spref.edit();

        sprefLatlon = getSharedPreferences("latlonInfo",Context.MODE_PRIVATE);
        editorLalon = sprefLatlon.edit();

        // -----------search + Favorite Tabs


        viewMainPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewMainPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsnew);
        tabLayout.setupWithViewPager(viewMainPager);


        //--------------Permission setting up  get location by GPS !!!!!! no networkchecking

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //criteria = new Criteria();
        //bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
       // Location location = locationManager.getLastKnownLocation(bestProvider);
        String gpslocationProvider = LocationManager.GPS_PROVIDER;
        String networklocationProvider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(gpslocationProvider);

        if (location == null) {
            location=locationManager.getLastKnownLocation(networklocationProvider);

        }
        editorLalon.putString("currentLat",Double.toString(location.getLatitude()));
        editorLalon.putString("currentLon",Double.toString(location.getLongitude()));
        editorLalon.apply();
        Log.d("Yeah!",location.getLatitude()+","+location.getLongitude());



    }

    private void setupViewPager(ViewPager viewPager){
        SectionMainPageAdapter adapter = new SectionMainPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new searchFragment(),"Search");
        adapter.addFragment(new favoriteFragment(),"Favorite");
        viewPager.setAdapter(adapter);
    }

    public void favoriteRemoveRow(View v)
    {


        RelativeLayout vwParentRow = (RelativeLayout)v.getParent();
        TextView eidinput = (TextView)vwParentRow.getChildAt(1);
        editor.remove(eidinput.getText().toString());
        editor.commit();
        TextView enameinput = (TextView)vwParentRow.getChildAt(2);
        String namevalue = enameinput.getText().toString();
        Toast.makeText(MainActivity.this, namevalue+" was removed from favorites",Toast.LENGTH_LONG).show();
        vwParentRow.removeView(v);
        Set keyset = spref.getAll().keySet();
        if(keyset.size()==0){
            findViewById(R.id.nodataMessage).setVisibility(View.VISIBLE);
            findViewById(R.id.fvrtListView).setVisibility(View.GONE);
        }else {
            findViewById(R.id.nodataMessage).setVisibility(View.GONE);
            findViewById(R.id.fvrtListView).setVisibility(View.VISIBLE);
        }


    }

}
