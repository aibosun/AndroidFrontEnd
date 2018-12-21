package com.usc.aibo.eventsearchmbl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class upcomeFragment extends Fragment{

    private static final String TAG = "upcomingFragment";
    private RequestQueue upcomeQue;
    private String venuename;
    private String venueid;

    private RecyclerView upcomingRecyclerView;

    private upcomingRecycler ucRecyclerAdapter;
    private List<JSONObject> upcomingInfoList;

    private View view;
    private Spinner ucTypeSpinner;
    private Spinner ucOrderSpinner;
    private TextView nodataMessage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upcome_fragment,container,false);
        nodataMessage = (TextView) view.findViewById(R.id.nodataMessage);
        venuename = ((DetailMainActivity)getActivity()).getVenueD();
        upcomeQue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        getUpcomingInfo();

        upcomingRecyclerView = (RecyclerView)view.findViewById(R.id.upcomingRecyclerView);
        ucRecyclerAdapter = new upcomingRecycler(getActivity());
        upcomingRecyclerView.setAdapter(ucRecyclerAdapter);
        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ucTypeSpinner = (Spinner) view.findViewById(R.id.ucTypeSpinner);
        ArrayAdapter<CharSequence> typeAdpter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.ucType_arrays,android.R.layout.simple_spinner_dropdown_item);
        typeAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ucTypeSpinner.setAdapter(typeAdpter);
        ucTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if("Default".equals(ucTypeSpinner.getSelectedItem().toString())){
                        ucOrderSpinner.setEnabled(false);
                        ucOrderSpinner.setClickable(false);
                    }else {
                        ucOrderSpinner.setEnabled(true);
                        ucOrderSpinner.setClickable(true);
                        comparingSort();
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ucOrderSpinner = (Spinner) view.findViewById(R.id.ucOrderSpinner);
        ArrayAdapter<CharSequence> orderAdpter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.ucOrder_arrays,android.R.layout.simple_spinner_dropdown_item);
        orderAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ucOrderSpinner.setAdapter(orderAdpter);
        ucOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    comparingSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void comparingSort() {
        if(upcomingInfoList!=null) {
            Collections.sort(upcomingInfoList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        if ("Event Name".equals(ucTypeSpinner.getSelectedItem().toString())) {
                            valA = (String) a.getString("displayName");
                            valB = (String) b.getString("displayName");
                        }
                            if ("Artist".equals(ucTypeSpinner.getSelectedItem().toString())) {
                                valA = (String) a.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                valB = (String) b.getJSONArray("performance").getJSONObject(0).getString("displayName");
                            }
                            if ("Time".equals(ucTypeSpinner.getSelectedItem().toString())) {

                                valA = a.getJSONObject("start").getString("date")+" "+a.getJSONObject("start").getString("time");
                                valB = b.getJSONObject("start").getString("date")+" "+b.getJSONObject("start").getString("time");
                            }

                            if ("Type".equals(ucTypeSpinner.getSelectedItem().toString())) {
                                valA = (String) a.getString("type");
                                valB = (String) b.getString("type");
                            }

                    } catch (JSONException e) {
                        //do something
                    }

                    if ("Ascending".equals(ucOrderSpinner.getSelectedItem().toString())) {
                        Log.d("Ascending", "" + ucOrderSpinner.getSelectedItem().toString());
                        return valA.compareTo(valB);
                    } else {
                        Log.d("des", "" + ucOrderSpinner.getSelectedItem().toString());
                        return -valA.compareTo(valB);
                    }

                }
            });

            ucRecyclerAdapter.notifyDataSetChanged();
        }
    }
    private void getUpcomingInfo(){
        String songkickApiKey = "dsajcjHofU4jTKj0";
        String url = "http://10.0.2.2:3000/upcominginfo?apikey="+songkickApiKey+"&venuename="+venuename;
        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/upcominginfo?apikey="+songkickApiKey+"&venuename="+venuename;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                                if(response.getJSONObject("resultsPage").getJSONObject("results").has("venue")){
                                    venueid  = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("venue").getJSONObject(0).getString("id");
                                    if(!"".equals(venueid)){
                                        getUpcomingCTN();
                                    }
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

        upcomeQue.add(jsonObjectRequest);
    }

    private void getUpcomingCTN(){
        String songkickApiKey = "dsajcjHofU4jTKj0";
        String url = "http://10.0.2.2:3000/upcominginfoctn?apikey="+songkickApiKey+"&venueid="+venueid;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/upcominginfoctn?apikey="+songkickApiKey+"&venueid="+venueid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                                if(response.has("resultsPage")&&response.getJSONObject("resultsPage").has("results")&&response.getJSONObject("resultsPage").getJSONObject("results").has("event")&&response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event").length()!=0) {
                                    JSONArray ucresultList = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
                                    upcomingInfoList = new ArrayList<>();
                                    for (int i = 0; i < Math.min(5, ucresultList.length()); i++) {
                                        upcomingInfoList.add(ucresultList.getJSONObject(i));
                                    }
                                    ucRecyclerAdapter.setUpcomingArray(upcomingInfoList);
                                }else{
                                    ucTypeSpinner.setEnabled(false);
                                    ucTypeSpinner.setClickable(false);
                                    nodataMessage.setVisibility(View.VISIBLE);
                                    upcomingRecyclerView.setVisibility(View.GONE);
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

        upcomeQue.add(jsonObjectRequest);
    }



}
