package com.usc.aibo.eventsearchmbl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class eventDtFragment extends Fragment {

    private static final String TAG = "eventDetailFragment";
    private RequestQueue mQue;
    private String eventname;
    private String eventid;


    private String venueDfor;

    private TextView arinfo;
    private TextView venueinfo;
    private TextView timeinfo;
    private TextView cateinfo;
    private TextView priceinfo;
    private TextView ticketstatus;
    private TextView btalinke;
    private TextView smaplink;

    private LinearLayoutCompat processBarContainer;
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_detail_fragment,container,false);

        Bundle bundle = getActivity().getIntent().getExtras();
        eventname = bundle.getString("eventname");
        eventid = bundle.getString("eventid");

        arinfo = (TextView)view.findViewById(R.id.aritistsinfo);
        venueinfo = (TextView)view.findViewById(R.id.venueinfo);
        timeinfo = (TextView)view.findViewById(R.id.timeinfo);
        cateinfo = (TextView)view.findViewById(R.id.cateinfo);
        priceinfo = (TextView)view.findViewById(R.id.priceinfo);
        ticketstatus = (TextView)view.findViewById(R.id.ticketstatus);
        btalinke = (TextView)view.findViewById(R.id.btalinke);
        smaplink = (TextView)view.findViewById(R.id.smaplink);
        Log.i("In","start detail");

        mQue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        getDetailResult();



        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        processBarContainer = (LinearLayoutCompat)view.findViewById(R.id.processBarContainer);
        processBarContainer.setVisibility(View.VISIBLE);

    }
    private void getDetailResult(){
        String url = "http://10.0.2.2:3000/detialinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventid="+eventid;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/detialinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventid="+eventid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processBarContainer.setVisibility(View.GONE);
                       if(response!=null) {
                           try {

                               if (response.has("_embedded")&&response.getJSONObject("_embedded") != null) {
                                   //---------------------Detail Artists Team
                                   if(response.getJSONObject("_embedded").has("attractions")) {
                                       JSONArray attr = response.getJSONObject("_embedded").getJSONArray("attractions");
                                       if (attr != null) {
                                           String atnames = "";
                                           for (int i = 0; i < attr.length() - 1; i++) {
                                               atnames += attr.getJSONObject(i).getString("name") + "|";
                                           }
                                           atnames += attr.getJSONObject(attr.length() - 1).getString("name");
                                           arinfo.setText(atnames);
                                           ((DetailMainActivity) getActivity()).setArtistF(attr.getJSONObject(0).getString("name"));
                                           if (attr.length() >= 2) {
                                               ((DetailMainActivity) getActivity()).setArtistS(attr.getJSONObject(1).getString("name"));
                                           }

                                       }
                                   }
                                   //---------------------Detail Venue
                                   JSONArray venueArr = response.getJSONObject("_embedded").getJSONArray("venues");
                                   venueinfo.setText(venueArr.getJSONObject(0).getString("name"));
                                   ((DetailMainActivity) getActivity()).setVenueD(venueArr.getJSONObject(0).getString("name"));
                               }
                               //---------------------Detail Time
                               if(response.has("dates")) {
                                   String dateD = response.getJSONObject("dates").getJSONObject("start").getString("localDate");
                                   String timeD = "";
                                   if (response.getJSONObject("dates").getJSONObject("start").getString("localTime") != null) {
                                       timeD = response.getJSONObject("dates").getJSONObject("start").getString("localTime");

                                   }
                                   String timeinfost = dateD + " " + timeD;
                                   timeinfo.setText(timeinfost);
                               }

                               //---------------------Detail Category
                               if(response.has("classifications")&&response.getJSONArray("classifications").length()!=0) {
                                   String genre = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name");
                                   String segment = response.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                   String categoryinfoma = genre + "|" + segment;
                                   cateinfo.setText(categoryinfoma);
                                   ((DetailMainActivity) getActivity()).setSegmentD(segment);
                               }

                               //---------------------Detail price range
                               if (response.has("priceRanges")) {
                                   String curD = response.getJSONArray("priceRanges").getJSONObject(0).getString("currency");
                                   String minD = response.getJSONArray("priceRanges").getJSONObject(0).getString("min");
                                   String maxD = response.getJSONArray("priceRanges").getJSONObject(0).getString("max");
                                   String pr = "$"+minD+"~$"+maxD;
                                   priceinfo.setText(pr);
                               }else{
                                   priceinfo.setText("");
                               }

                               //------------ Status
                               if(response.has("dates")&&response.getJSONObject("dates").has("status")&&response.getJSONObject("dates").getJSONObject("status").has("code")) {
                                   ticketstatus.setText(response.getJSONObject("dates").getJSONObject("status").getString("code"));
                               }
                               // ---------------------DBuy Ticket At
                               if(response.has("url")){
                                   String url  = response.getString("url");
                                   btalinke.setClickable(true);
                                   btalinke.setMovementMethod(LinkMovementMethod.getInstance());
                                   String text = "<a href='"+url+"'> Ticketmaster </a>";
                                   btalinke.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                               }


                               // ---------------------Dseat Map
                               if (response.has("seatmap")) {
                                   String smurl = response.getJSONObject("seatmap").getString("staticUrl");
                                   smaplink.setClickable(true);
                                   smaplink.setMovementMethod(LinkMovementMethod.getInstance());
                                   String text2 = "<a href='" + smurl + "'> Seat Map </a>";
                                   smaplink.setText(Html.fromHtml(text2, Html.FROM_HTML_MODE_COMPACT));
                               }
                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "error",Toast.LENGTH_LONG).show();
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });

        mQue.add(jsonObjectRequest);
    }

}
