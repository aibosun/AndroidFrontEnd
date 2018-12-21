package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EventResultsActivity extends AppCompatActivity {
    private JSONArray events;
    private int  length=0;
    private ListView reslistView;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    EventAdapter eventAdapter;
    private RequestQueue mQue;
    private LinearLayoutCompat processBarContainer;
    private String  keyword;
    private String  segmentId;
    private String  radius;
    private String  unit;
    private String  geoPoint;
    private LinearLayoutCompat nodataMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_results);
        setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spref = getSharedPreferences("favoriteInfo",Context.MODE_PRIVATE);
        editor = spref.edit();

        mQue = Volley.newRequestQueue(Objects.requireNonNull(EventResultsActivity.this));

        nodataMessage = (LinearLayoutCompat) findViewById(R.id.nodataMessage);

        reslistView = findViewById(R.id.eventlistView);
        reslistView.setVisibility(View.GONE);

        reslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Intent intent = new Intent(EventResultsActivity.this,DetailMainActivity.class);
                try {
                    JSONObject entity = (JSONObject)events.getJSONObject(i);

                    String eid = entity.getString("id");
                    intent.putExtra("eventid",eid);
                    String ename = entity.getString("name");
                    intent.putExtra("eventname",ename);
                    JSONArray venues =entity.getJSONObject("_embedded").getJSONArray("venues");
                    intent.putExtra("venue",venues.getJSONObject(0).getString("name"));
                    String dateinfoma=entity.getJSONObject("dates").getJSONObject("start").getString("localDate")+" "+entity.getJSONObject("dates").getJSONObject("start").getString("localTime");
                    intent.putExtra("date",dateinfoma);
                    String segmentinfo = entity.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                    intent.putExtra("segment",segmentinfo);
                    intent.putExtra("url",entity.getString("url"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        processBarContainer = (LinearLayoutCompat)findViewById(R.id.processBarContainer);
        processBarContainer.setVisibility(View.VISIBLE);
        reslistView.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        events= new JSONArray();
        //IF ITS THE FRAGMENT THEN RECEIVE DATA
        if(extras != null)
        {
            keyword = extras.getString("keyword");
            segmentId = extras.getString("segmentId");
            radius = extras.getString("radius");
            unit = extras.getString("unit");
            geoPoint = extras.getString("geoPoint");
            getEventResult();

        }else{
            Toast.makeText(this, "no info past", Toast.LENGTH_SHORT).show();

        }

    }

    private void getEventResult(){
        String url = "http://10.0.2.2:3000/events?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&keyword="+keyword+"&segmentId="+segmentId+"&radius="+radius+"&unit="+unit+"&geoPoint="+geoPoint+"&sort=date,asc";

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/events?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&keyword="+keyword+"&segmentId="+segmentId+"&radius="+radius+"&unit="+unit+"&geoPoint="+geoPoint+"&sort=date,asc";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processBarContainer.setVisibility(View.GONE);
                        if(response!=null&&response.has("_embedded")){
                            try {
                                JSONObject em = response.getJSONObject("_embedded");
                                if(em!=null) {
                                    events = em.getJSONArray("events");
                                    length = events.length();
                                    eventAdapter = new EventAdapter();
                                    reslistView.setAdapter(eventAdapter);
                                    reslistView.setVisibility(View.VISIBLE);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{

                            nodataMessage.setVisibility(View.VISIBLE);
                            reslistView.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EventResultsActivity.this, "error",Toast.LENGTH_LONG).show();
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });

        mQue.add(jsonObjectRequest);
    }


    public void favoriteAdd(View v)
    {

        RelativeLayout vwParentRow = (RelativeLayout)v.getParent();
        TextView eidinput = (TextView)vwParentRow.getChildAt(1);
        TextView enameinput = (TextView)vwParentRow.getChildAt(2);
        TextView venueinput = (TextView)vwParentRow.getChildAt(3);
        TextView dateinput = (TextView)vwParentRow.getChildAt(4);
        TextView segmentinput = (TextView)vwParentRow.getChildAt(5);
        TextView urlinput = (TextView)vwParentRow.getChildAt(6);
        JSONObject fvrtObj = new JSONObject();
        try {
            fvrtObj.put("id",eidinput.getText());
            fvrtObj.put("name",enameinput.getText());
            fvrtObj.put("venue",venueinput.getText());
            fvrtObj.put("date",dateinput.getText());
            fvrtObj.put("segment",segmentinput.getText());
            fvrtObj.put("url",urlinput.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putString(eidinput.getText().toString(),fvrtObj.toString());
        editor.apply();

        ImageView fvrtBb = (ImageView)vwParentRow.getChildAt(7);
        ImageView fvrtRed = (ImageView)vwParentRow.getChildAt(8);
        fvrtBb.setVisibility(View.INVISIBLE);
        fvrtRed.setVisibility(View.VISIBLE);
        vwParentRow.refreshDrawableState();
        Toast.makeText(this, enameinput.getText()+" was added to favorites", Toast.LENGTH_SHORT).show();

    }

    public void favoriteRemove(View v)
    {

        RelativeLayout vwParentRow = (RelativeLayout)v.getParent();
        TextView eidinput = (TextView)vwParentRow.getChildAt(1);
        editor.remove(eidinput.getText().toString());
        editor.commit();
        ImageView fvrtBb = (ImageView)vwParentRow.getChildAt(7);
        ImageView fvrtRed = (ImageView)vwParentRow.getChildAt(8);
        fvrtBb.setVisibility(View.VISIBLE);
        fvrtRed.setVisibility(View.INVISIBLE);
        vwParentRow.refreshDrawableState();
        TextView enameinput = (TextView)vwParentRow.getChildAt(2);
        String namevalue = enameinput.getText().toString();
        Toast.makeText(EventResultsActivity.this, namevalue+" was removed from favorites",Toast.LENGTH_LONG).show();

    }


    class EventAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

             view = getLayoutInflater().inflate(R.layout.result_item_view,parent,false);
             ImageView imageView = view.findViewById(R.id.categoryImageView);
            TextView eidText = view.findViewById(R.id.eid);
             TextView nameText = view.findViewById(R.id.ename);
             TextView venueText = view.findViewById(R.id.venue);
             TextView dateText = view.findViewById(R.id.dateinfo);
            TextView segmentText = view.findViewById(R.id.segmentinfo);
            TextView urlText = view.findViewById(R.id.urlinfo);
             ImageView favoriteImgBlack = view.findViewById(R.id.favoriteImgBlack);
            ImageView favoriteImgRed= view.findViewById(R.id.favoriteImgRed);

            try {

                JSONObject eventEntity =events.getJSONObject(i);
                if(eventEntity.has("classifications")&&eventEntity.getJSONArray("classifications").length()!=0) {
                    String segmentinfo = eventEntity.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                    segmentText.setText(segmentinfo);
                    switch (segmentinfo) {
                        case "Music":
                            imageView.setImageResource(R.drawable.music_icon);
                            break;
                        case "Sports":
                            imageView.setImageResource(R.drawable.sport_icon);
                            break;
                        case "Arts & Theatre":
                            imageView.setImageResource(R.drawable.art_icon);
                            break;
                        case "Film":
                            imageView.setImageResource(R.drawable.film_icon);
                            break;
                        case "Miscellaneous":
                            imageView.setImageResource(R.drawable.miscellaneous_icon);
                            break;

                    }
                }
                eidText.setText(eventEntity.getString("id"));

                nameText.setText(eventEntity.getString("name"));
                JSONArray venues =eventEntity.getJSONObject("_embedded").getJSONArray("venues");

                venueText.setText(venues.getJSONObject(0).getString("name"));

                dateText.setText(eventEntity.getJSONObject("dates").getJSONObject("start").getString("localDate")+" "+eventEntity.getJSONObject("dates").getJSONObject("start").getString("localTime"));
                urlText.setText(eventEntity.getString("url"));

                favoriteImgBlack.setImageResource(R.drawable.heart_outline_black);
                favoriteImgRed.setImageResource(R.drawable.heart_fill_red);
                favoriteImgRed.setVisibility(View.INVISIBLE);

                if (spref.contains(eventEntity.getString("id"))) {
                    favoriteImgBlack.setVisibility(View.INVISIBLE);
                    favoriteImgRed.setVisibility(View.VISIBLE);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }


}
