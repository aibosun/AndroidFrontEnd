package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class artistsFragment extends Fragment {

    private static final String TAG = "artistsFragment";
    private String eventid;
    private String segmentD;
    private String artistF;
    private String artistS;

    private RequestQueue artistQue;


    private TextView artFTitleInfo;
    private TextView aNameInfo;
    private TextView aFollowerInfo;
    private TextView aPopularInfo;
    private TextView aCheckAtInfo;

    private  int lengthfirst;
    private JSONArray firstImgs;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ListView firstImglistView;
    private FirstImgAdapter firstImgAdapter;


    private TextView artSecondTitleInfo;

    private TextView aSecondNameInfo ;
    private TextView aSecondFollowerInfo ;
    private TextView aSecondPopularInfo;
    private TextView aSecondCheckAtInfo;


    private RequestQueue mRequestQueueSecond;
    private ImageLoader mImageLoaderSeconde;
    private int lengthsecond;
    private JSONArray secondImgs;
    private ListView secondImglistView;
    private SecondImgAdapter secondImgAdapter;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.artists_fragment,container,false);
        artistQue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        Log.i("In","start art");

        artFTitleInfo = (TextView)view.findViewById(R.id.artFTitleInfo);

        Bundle bundle = getActivity().getIntent().getExtras();
        eventid = bundle.getString("eventid");


//----------------First Artist Calling
        aNameInfo = (TextView)view.findViewById(R.id.aNameInfo);
        aFollowerInfo = (TextView)view.findViewById(R.id.aFollowerInfo);
        aPopularInfo = (TextView)view.findViewById(R.id.aPopularInfo);
        aCheckAtInfo = (TextView)view.findViewById(R.id.aCheckAtInfo);

        Context context = getContext();
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(8);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });


        firstImglistView = view.findViewById(R.id.firstImgListView);
        setListViewHeightBasedOnChildren(firstImglistView);
        firstImglistView.setOnTouchListener(new OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        firstImgAdapter= new FirstImgAdapter();
        firstImglistView.setAdapter(firstImgAdapter);


//---------------------Second Person Starts
        artSecondTitleInfo = (TextView)view.findViewById(R.id.artSecondTitleInfo);
        aSecondNameInfo = (TextView)view.findViewById(R.id.aSecondNameInfo);
        aSecondFollowerInfo = (TextView)view.findViewById(R.id.aSecondFollowerInfo);
        aSecondPopularInfo = (TextView)view.findViewById(R.id.aSecondPopularInfo);
        aSecondCheckAtInfo = (TextView)view.findViewById(R.id.aSecondCheckAtInfo);



        mRequestQueueSecond = Volley.newRequestQueue(context);
        mImageLoaderSeconde = new ImageLoader(mRequestQueueSecond, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(16);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });



        secondImglistView = view.findViewById(R.id.secondImgListView);
        setListViewHeightBasedOnChildren(secondImglistView);
        secondImglistView.setOnTouchListener(new OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        secondImgAdapter = new SecondImgAdapter();
        secondImglistView.setAdapter(secondImgAdapter);
        if(eventid!=null&&!"".equals(eventid)){
            getDetailForArtist();
        }

        return view;
    }

    private void getDetailForArtist(){
        String url = "http://10.0.2.2:3000/detialinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventid="+eventid;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/detialinfo?apikey=xF34U9ON4RI6uaaIMUirrSbb8hOGKVhb&eventid="+eventid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {

                                if (response.has("_embedded")&&response.getJSONObject("_embedded") != null) {
                                    //---------------------Detail Artists Team
                                    if(response.getJSONObject("_embedded").has("attractions")) {
                                        segmentD= response.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");

                                        JSONArray attr = response.getJSONObject("_embedded").getJSONArray("attractions");
                                        if (attr != null&&attr.length()!=0) {
                                            artistF=attr.getJSONObject(0).getString("name");
                                            if("Music".equals(segmentD)) {
                                                getMusicalArt("first", artistF);
                                                view.findViewById(R.id.firstMusical).setVisibility(View.VISIBLE);
                                            }
                                            getImageResult("first",artistF);

                                            if (attr.length() >= 2) {
                                                artistS=attr.getJSONObject(1).getString("name");
                                                    if("Music".equals(segmentD)){
                                                        getMusicalArt("second",artistS);
                                                        view.findViewById(R.id.secondMusical).setVisibility(View.VISIBLE);
                                                    }
                                                    getImageResult("second",artistS);
                                            }

                                        }
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

        artistQue.add(jsonObjectRequest);
    }


    private void getMusicalArt(final String flag,final String artistname){
        String url = "http://10.0.2.2:3000/artistinfo?artistname="+artistname;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/artistinfo?artistname="+artistname;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                                if(response.getInt("statusCode")==200){
                                    JSONArray artArray = response.getJSONObject("body").getJSONObject("artists").getJSONArray("items");
                                    //-----------1
                                    if("first".equals(flag)){
                                        JSONObject firstPerson = new JSONObject();
                                        for(int i=0;i<artArray.length();i++){
                                          if(artistname.equals(artArray.getJSONObject(i).getString("name"))){

                                              firstPerson.put("name",artArray.getJSONObject(i).getString("name"));
                                              firstPerson.put("followers",artArray.getJSONObject(i).getJSONObject("followers").getString("total"));
                                              firstPerson.put("popularity",artArray.getJSONObject(i).getString("popularity"));
                                              firstPerson.put("spurl",artArray.getJSONObject(i).getJSONObject("external_urls").getString("spotify"));

                                              break;
                                          }
                                        }
                                          if(firstPerson!=null) {
                                              aNameInfo.setText(firstPerson.getString("name"));
                                              aPopularInfo.setText(firstPerson.getString("popularity"));
                                              aFollowerInfo.setText(firstPerson.getString("followers"));
                                              String smurl = firstPerson.getString("spurl");
                                              aCheckAtInfo.setClickable(true);
                                              aCheckAtInfo.setMovementMethod(LinkMovementMethod.getInstance());
                                              String text = "<a href='" + smurl + "'> Spotify </a>";
                                              aCheckAtInfo.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                                          }



                                    }else{
                                        JSONObject secondPerson = new JSONObject();
                                        for(int i=0;i<artArray.length();i++){
                                            if(artistname.equals(artArray.getJSONObject(i).getString("name"))){
                                                secondPerson = artArray.getJSONObject(i);
                                                break;
                                            }
                                        }
                                        if(secondPerson!=null) {
                                            aSecondNameInfo.setText(secondPerson.getString("name"));
                                            aSecondFollowerInfo.setText(secondPerson.getJSONObject("followers").getString("total"));
                                            aSecondPopularInfo.setText(secondPerson.getString("popularity"));
                                            String smurlss = secondPerson.getJSONObject("external_urls").getString("spotify");
                                            aSecondCheckAtInfo.setClickable(true);
                                            aSecondCheckAtInfo.setMovementMethod(LinkMovementMethod.getInstance());
                                            String text = "<a href='" + smurlss + "'> Spotify </a>";
                                            aSecondCheckAtInfo.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
                                        }
                                    }

                                }else{
                                    getToken(flag,artistname);
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

        artistQue.add(jsonObjectRequest);
    }

    private void getToken(final String flag,final String artistname){
        String url = "http://10.0.2.2:3000/artisttokeninfo";

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/artisttokeninfo";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                                if("yes".equals(response.getString("token"))) {
                                    getMusicalArt(flag,artistname);
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

        artistQue.add(jsonObjectRequest);
    }



    private void getImageResult(final String flag,final String artistname){
        String cx = "001833434290300447219:3ufbaufswls";
        String imgApiKey = "AIzaSyDmqxBsVHfo9d9qNEl30SipPI2y1EmXxGk";
        String url = "http://10.0.2.2:3000/imageinfo?q="+artistname+"&cx="+cx+"&imgSize=huge&num=8&searchType=image&key="+imgApiKey;

        String url2 = "http://tecketapi-env.yy2xtzbncj.us-east-2.elasticbeanstalk.com/imageinfo?q="+artistname+"&cx="+cx+"&imgSize=huge&num=8&searchType=image&key="+imgApiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(response!=null) {
                            try {
                                if("first".equals(flag)){
                                    artFTitleInfo.setText(artistname);
                                    lengthfirst = response.getJSONArray("items").length();
                                     firstImgs = response.getJSONArray("items");
                                     if(lengthfirst!=0){
                                         Log.d("back image........","bakc~~~~~");
                                         firstImgAdapter.notifyDataSetChanged();

                                     }
                                }else{
                                    artSecondTitleInfo.setText(artistname);

                                    lengthsecond = response.getJSONArray("items").length();
                                    secondImgs = response.getJSONArray("items");
                                    Log.d("back",""+lengthsecond);
                                    if(lengthsecond!=0){

                                        secondImglistView.deferNotifyDataSetChanged();
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

        artistQue.add(jsonObjectRequest);
    }


    class FirstImgAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lengthfirst;
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

            view = getLayoutInflater().inflate(R.layout.first_img_item,parent,false);

            NetworkImageView fItemImg =(NetworkImageView) view.findViewById(R.id.firstimgitemview);

            try {

                fItemImg.setImageUrl(firstImgs.getJSONObject(i).getString("link"),mImageLoader);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    class SecondImgAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lengthsecond;
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

            view = getLayoutInflater().inflate(R.layout.second_img_item,parent,false);

            NetworkImageView sItemImg =(NetworkImageView) view.findViewById(R.id.secondimgitemview);

            try {

                sItemImg.setImageUrl(secondImgs.getJSONObject(i).getString("link"),mImageLoaderSeconde);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

