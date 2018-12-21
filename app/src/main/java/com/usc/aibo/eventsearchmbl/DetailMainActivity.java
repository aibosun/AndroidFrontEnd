package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.View;


import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailMainActivity extends AppCompatActivity {



    private SectionDetailPageAdapter sectionDetailPageAdapter;
    private ViewPager viewDetailPager;
    private String eventid;
    private String eventname;
    private String venue;
    private String date;
    private String segment;
    private String url;

    private String venueD;
    private String segmentD;
    private String artistF;
    private String artistS;

    private SharedPreferences spref;
    private ImageView fvrtBT;
    private ImageView twitterBT;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_main);

        //sectionDetailPageAdapter =

        Bundle bundle = getIntent().getExtras();
        eventid = bundle.getString("eventid");
        eventname = bundle.getString("eventname");
        venue=bundle.getString("venue");
        date=bundle.getString("date");
        segment=bundle.getString("segment");
        url=bundle.getString("url");

        // ----------- Four Tabs
        viewDetailPager = (ViewPager) findViewById(R.id.infocontainer);
        setupViewPager(viewDetailPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.infotabs);
        tabLayout.setupWithViewPager(viewDetailPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.artist);
        tabLayout.getTabAt(2).setIcon(R.drawable.venue);
        tabLayout.getTabAt(3).setIcon(R.drawable.upcoming);

        // bar & Tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.infotoolbar);
        toolbar.setTitle(eventname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        spref = this.getSharedPreferences("favoriteInfo",Context.MODE_PRIVATE);
        editor = spref.edit();
        fvrtBT = (ImageView) findViewById(R.id.fvrtBT);

        if (spref.contains(eventid)) {

            fvrtBT.setImageResource(R.drawable.heart_fill_red);

        }else{

            fvrtBT.setImageResource(R.drawable.heart_fill_white);
        }

        fvrtBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spref.contains(eventid)){
                    fvrtBT.setImageResource(R.drawable.heart_fill_white);
                    editor.remove(eventid);
                    editor.commit();
                    Toast.makeText(DetailMainActivity.this, eventname+" was removed from favorites",Toast.LENGTH_LONG).show();
                }else{
                    fvrtBT.setImageResource(R.drawable.heart_fill_red);
                    JSONObject fvrtObj = new JSONObject();
                    try {
                        fvrtObj.put("id",eventid);
                        fvrtObj.put("name",eventname);
                        fvrtObj.put("venue",venue);
                        fvrtObj.put("date",date);
                        fvrtObj.put("segment",segment);
                        fvrtObj.put("url",url);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString(eventid,fvrtObj.toString());
                    editor.apply();
                    Toast.makeText(DetailMainActivity.this, eventname+" was added to favorites",Toast.LENGTH_LONG).show();

                }
            }
        });

        twitterBT = (ImageView) findViewById(R.id.twitterBT);
        twitterBT.setImageResource(R.drawable.twitter_ic);


        twitterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweettext="Check out "+eventname+" at "+venueD+". Website: "+url;
                String hash = "CSCI571EventSearch,";
                String url = "https://twitter.com/intent/tweet?text="+tweettext+"&hashtags="+hash;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager){
        SectionDetailPageAdapter adapter = new SectionDetailPageAdapter(getSupportFragmentManager());
        adapter.addFragment(0,new eventDtFragment(),"Detail");
        adapter.addFragment(1,new artistsFragment(),"Artists");
        adapter.addFragment(2,new venueFragment(),"Venue");
        adapter.addFragment(3,new upcomeFragment(),"Upcoming");
        viewPager.setAdapter(adapter);
    }

    public String getVenueD() {
        return venueD;
    }
    public void setVenueD(String venueD) {
        this.venueD = venueD;
    }
    public String getSegmentD() {
        return segmentD;
    }

    public void setSegmentD(String segmentD) {
        this.segmentD = segmentD;
    }

    public String getArtistF() {
        return artistF;
    }

    public void setArtistF(String artistF) {
        this.artistF = artistF;
    }

    public String getArtistS() {
        return artistS;
    }

    public void setArtistS(String artistS) {
        this.artistS = artistS;
    }



}
