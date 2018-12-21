package com.usc.aibo.eventsearchmbl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

    public class venueFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "venueFragment";
    private RequestQueue venuemQue;
    private String venuename;

    private TextView nameVenue;
    private TextView addressVenue;
    private TextView cityVenue;
    private TextView phoneVenue;
    private TextView openVenue;
    private TextView grVenue;
    private TextView crVenue;


    private String googleMapApiKey="AIzaSyCDTsY7Kesp9DHySpgaiyrVKjX0reVqI_A";
    private GoogleMap mMap;
    private View view;
    private MapView mMapView;

    private double latmap=0;
    private double lonmap=0;

    public  venueFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.venue_fragment,container,false);
        Log.i("In","start venue");
        nameVenue = (TextView)view.findViewById(R.id.nameVenue);
        addressVenue = (TextView)view.findViewById(R.id.addressVenue);
        cityVenue = (TextView)view.findViewById(R.id.cityVenue);
        phoneVenue = (TextView)view.findViewById(R.id.phoneVenue);
        openVenue = (TextView)view.findViewById(R.id.openVenue);
        grVenue = (TextView)view.findViewById(R.id.grVenue);
        crVenue = (TextView)view.findViewById(R.id.crVenue);


        venuename = ((DetailMainActivity)getActivity()).getVenueD();
        venuemQue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        getVenueInfo();


        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mMapView =(MapView) view.findViewById(R.id.map);
        if(mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity().getBaseContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(latmap!=0&&lonmap!=0) {
            Log.d("location~~~~~~~~~~",latmap+","+lonmap);
            LatLng point = new LatLng(latmap,lonmap);
            mMap.addMarker(new MarkerOptions().position(point).title("Location"));
            CameraPosition lib = CameraPosition.builder().target(new LatLng(latmap,lonmap)).zoom(14).bearing(0).tilt(45).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lib));
        }
    }

    private void getVenueInfo(){
        String url = "http://10.0.2.2:3000/venueinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventname="+venuename;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/venueinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventname="+venuename;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                               JSONObject venueinfomation  = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                                nameVenue.setText(venueinfomation.getString("name"));
                                addressVenue.setText(venueinfomation.getJSONObject("address").getString("line1"));
                                String cityname = venueinfomation.getJSONObject("city").getString("name");
                                String state = venueinfomation.getJSONObject("state").getString("name");
                                cityVenue.setText(cityname+","+state);
                                if(venueinfomation.has("boxOfficeInfo")){
                                    if(venueinfomation.getJSONObject("boxOfficeInfo").has("phoneNumberDetail")){
                                        phoneVenue.setText(venueinfomation.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail"));
                                    }
                                    if(venueinfomation.getJSONObject("boxOfficeInfo").has("openHoursDetail")) {
                                        openVenue.setText(venueinfomation.getJSONObject("boxOfficeInfo").getString("openHoursDetail"));
                                    }
                                }
                                if(venueinfomation.has("generalInfo")){
                                    if(venueinfomation.getJSONObject("generalInfo").has("generalRule")) {
                                        grVenue.setText(venueinfomation.getJSONObject("generalInfo").getString("generalRule"));
                                    }
                                    if(venueinfomation.getJSONObject("generalInfo").has("childRule")) {
                                        crVenue.setText(venueinfomation.getJSONObject("generalInfo").getString("childRule"));
                                    }
                                }
                                latmap=Double.parseDouble(venueinfomation.getJSONObject("location").getString("latitude"));
                                lonmap=Double.parseDouble(venueinfomation.getJSONObject("location").getString("longitude"));

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

        venuemQue.add(jsonObjectRequest);
    }


}
