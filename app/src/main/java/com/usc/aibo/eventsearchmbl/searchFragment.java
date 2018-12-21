package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import android.os.Handler;

public class searchFragment extends Fragment{
    private static final String TAG = "searchFragment";


    private AppCompatAutoCompleteTextView keywordInput;
    private Spinner categoryspinner;
    private TextView distanceInput;
    private Spinner unitSpinner;
    private RadioGroup radioLocationGroupBT;
    private TextView locationInput;
    private Button searchBT;
    private Button clearBT;
    private SharedPreferences sprefLatlon;
    private View view;
    private Intent intent ;
    private RequestQueue mQue;
    private AutoCplAdapter autoCplAdapter;
    private Timer timer;
    private static final int TRIGGER_AUTO_COMPLETE = 500;
    private static final long AUTO_COMPLETE_DELAY = 500;
    private Handler handler;
    private KeyListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.search_fragment,container,false);
        sprefLatlon = this.getActivity().getSharedPreferences("latlonInfo",Context.MODE_PRIVATE);

        mQue= Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
//----------------------------------autocomplete function starts
        keywordInput = (AppCompatAutoCompleteTextView)view.findViewById(R.id.keywordInput);


        autoCplAdapter= new AutoCplAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        keywordInput.setAdapter(autoCplAdapter);


        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
            }

        };
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(keywordInput.getText())) {
                        goCallAutoCpl(keywordInput.getText().toString());
                    }
                }
                return false;
            }
        });
       keywordInput.addTextChangedListener(fieldValidatorTextWatcher);

        keywordInput.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        keywordInput.setText(autoCplAdapter.getObject(position));
                    }
                });
//----------------------------------autocomplete function ends

        categoryspinner = (Spinner)view.findViewById(R.id.categoryspinner);
        distanceInput = (TextView)view.findViewById(R.id.distanceInput);
        unitSpinner = (Spinner)view.findViewById(R.id.unitSpinner);
        radioLocationGroupBT = (RadioGroup)view.findViewById(R.id.radioLocation);
        locationInput=(TextView)view.findViewById(R.id.locationInput);
        listener = locationInput.getKeyListener();
        locationInput.setKeyListener(null);
        radioLocationGroupBT.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.locationRB0:
                        locationInput.setText("");
                        locationInput.setInputType(InputType.TYPE_NULL);
                        locationInput.setKeyListener(null);
                        break;
                    case R.id.locationRB1:
                        locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
                        locationInput.setKeyListener(listener);
                        break;
                }

            }
        });
        //--------------search Button
        searchBT = (Button)view.findViewById(R.id.searchBT);
        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventResultAct();
            }
        });
        //--------------clear Button
        clearBT = (Button)view.findViewById(R.id.clearBT);
        clearBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.keyInvalidation).setVisibility(View.GONE);
                view.findViewById(R.id.locationvalidation).setVisibility(View.GONE);
                keywordInput.setText("");
                categoryspinner.setSelection(0);
                locationInput.setText("");
                distanceInput.setText("");
                unitSpinner.setSelection(0);
                radioLocationGroupBT.check(R.id.locationRB0);

                //getActivity().getSharedPreferences("favoriteInfo",Context.MODE_PRIVATE).edit().clear().commit();;

            }
        });


        return view;



    }

    private void startEventResultAct() {
        int selectedBTId = radioLocationGroupBT.getCheckedRadioButtonId();
        String adress = locationInput.getText().toString();
        if(keywordInput.getText().toString().trim().length()==0||(selectedBTId==R.id.locationRB1&&adress.trim().length()==0)){
            Toast.makeText(getActivity(), "Please fix all fields with errors",Toast.LENGTH_SHORT).show();

            if(keywordInput.getText().toString().trim().length()==0){
                view.findViewById(R.id.keyInvalidation).setVisibility(View.VISIBLE);
            }

            if(selectedBTId==R.id.locationRB1&&adress.trim().length()==0){
                view.findViewById(R.id.locationvalidation).setVisibility(View.VISIBLE);
            }
            return;
        }

        intent = new Intent(getActivity(),EventResultsActivity.class);
        intent.putExtra("keyword",keywordInput.getText().toString());
        String seletedCtgry = categoryspinner.getSelectedItem().toString();
        String segmentId="";

        switch (seletedCtgry){
            case "All":
                segmentId="";
                break;
            case "Music" :
                segmentId="KZFzniwnSyZfZ7v7nJ";
                break;
            case "Sports":
                segmentId="KZFzniwnSyZfZ7v7nE";
                break;
            case "Arts&Theatre" :
                segmentId="KZFzniwnSyZfZ7v7na";
                break;
            case "Film":
                segmentId="KZFzniwnSyZfZ7v7nn";
                break;
            case "Miscellaneous" :
                segmentId="KZFzniwnSyZfZ7v7n1";
                break;

        }
        intent.putExtra("segmentId",segmentId);
        intent.putExtra("radius",distanceInput.getText().toString());
        String unit ="";
        if(unitSpinner.getSelectedItem().toString().equals("Miles")){
            unit = "miles";
        }else{
            unit = "km";
        }
        intent.putExtra("unit",unit);

        String Latgo ="";
        String Longo ="";
        if(selectedBTId==R.id.locationRB0){
            Latgo = sprefLatlon.getString("currentLat","");
            Longo = sprefLatlon.getString("currentLon","");
            goCallGeoHash(Latgo,Longo);
        }else{
            goCallGeoCoding(adress);
        }
    }
    private void goCallGeoCoding(String address){
        String url = "http://10.0.2.2:3000/geocoding?key=AIzaSyCDTsY7Kesp9DHySpgaiyrVKjX0reVqI_A&address="+address;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/geocoding?key=AIzaSyCDTsY7Kesp9DHySpgaiyrVKjX0reVqI_A&address="+address;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if(response!=null){
                            try {
                                JSONObject entity = response.getJSONArray("results").getJSONObject(0);
                                if(entity!=null) {
                                    JSONObject location = entity.getJSONObject("geometry").getJSONObject("location");
                                    goCallGeoHash(location.getString("lat"),location.getString("lng"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });

        mQue.add(jsonObjectRequest);
    }


    private void  goCallGeoHash(final String lat, final String lon){
        String url = "http://10.0.2.2:3000/geohash?lat="+lat+"&lon="+lon;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/geohash?lat="+lat+"&lon="+lon;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("various location",lat+","+lon+"  "+response);
                        intent.putExtra("geoPoint",response);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQue.add(stringRequest);
    }

    private void goCallAutoCpl(String keyword){
        String url = "http://10.0.2.2:3000/autocomplete?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&keyword="+keyword;
        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/autocomplete?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&keyword="+keyword;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<String> autoCplArray= new ArrayList<>();
                        if(response!=null&&response.has("_embedded")){
                            try {
                                JSONArray entityList = response.getJSONObject("_embedded").getJSONArray("attractions");
                                if(entityList!=null&&entityList.length()!=0) {
                                    for(int i=0;i<entityList.length();i++){
                                        autoCplArray.add(entityList.getJSONObject(i).getString("name"));
                                        //arrayAdapter.add(entityList.getJSONObject(i).getString("name"));
                                    }
                                    autoCplAdapter.setData(autoCplArray);
                                    autoCplAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });

        mQue.add(jsonObjectRequest);
    }


}
