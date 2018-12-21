package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

public class favoriteFragment extends Fragment {

    private static final String TAG = "favoriteFragment";
    private ListView fvrtlistView;

    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    public   FvrtAdapter fvrtAdapter;
    private TextView nodataMessage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment,container,false);
        nodataMessage = (TextView)view.findViewById(R.id.nodataMessage);
        fvrtlistView = (ListView)view.findViewById(R.id.fvrtListView);
        fvrtAdapter = new FvrtAdapter();
        spref = this.getActivity().getSharedPreferences("favoriteInfo",Context.MODE_PRIVATE);
        editor = spref.edit();
        Set keyset = spref.getAll().keySet();
        List keylist = new ArrayList(keyset);
        if(keylist.size()==0){
            nodataMessage.setVisibility(View.VISIBLE);
            fvrtlistView.setVisibility(View.GONE);
        }else {
            nodataMessage.setVisibility(View.GONE);
            fvrtlistView.setVisibility(View.VISIBLE);
        }


        fvrtlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(getActivity(),DetailMainActivity.class);
                try {
                    Set keyset = spref.getAll().keySet();
                    List keylist = new ArrayList(keyset);
                    if(keyset.size()!=0) {
                        String keyinfo = keylist.get(i).toString();
                        JSONObject entity = new JSONObject(spref.getString(keyinfo,""));

                        String eid = entity.getString("id");
                        intent.putExtra("eventid", eid);
                        String ename = entity.getString("name");
                        intent.putExtra("eventname", ename);
                        String venues = entity.getString("venue");
                        intent.putExtra("venue", venues);
                        String dateinfoma = entity.getString("date");
                        intent.putExtra("date", dateinfoma);
                        String segmentinfo = entity.getString("segment");
                        intent.putExtra("segment", segmentinfo);

                        intent.putExtra("url",entity.getString("url"));
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        fvrtlistView.setAdapter(fvrtAdapter);


        return view;
    }




    class FvrtAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return spref.getAll().size();
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

            view = getLayoutInflater().inflate(R.layout.fvrt_item_view,parent,false);
            ImageView imageView = view.findViewById(R.id.categoryImageView);
            TextView eidText = view.findViewById(R.id.eid);
            TextView nameText = view.findViewById(R.id.ename);
            TextView venueText = view.findViewById(R.id.venue);
            TextView dateText = view.findViewById(R.id.dateinfo);
            TextView segmentText = view.findViewById(R.id.segmentinfo);
            TextView urlText = view.findViewById(R.id.urlinfo);
            ImageView favoriteImgRed= view.findViewById(R.id.favoriteImgRed);
            Set keyset = spref.getAll().keySet();
            List keylist = new ArrayList(keyset);
            if(keylist.size()!=0) {
                String keyinfo = keylist.get(i).toString();
                try {
                    JSONObject fvrtEntity = new JSONObject(spref.getString(keyinfo,""));

                    String segmentinfo = fvrtEntity.getString("segment");
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

                    eidText.setText(fvrtEntity.getString("id"));

                    nameText.setText(fvrtEntity.getString("name"));
                    venueText.setText(fvrtEntity.getString("venue"));

                    dateText.setText(fvrtEntity.getString("date"));
                    urlText.setText(fvrtEntity.getString("url"));

                    favoriteImgRed.setImageResource(R.drawable.heart_fill_red);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return view;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        fvrtAdapter.notifyDataSetChanged();
        Set keyset = spref.getAll().keySet();
        List keylist = new ArrayList(keyset);
        if(keylist.size()==0){
            nodataMessage.setVisibility(View.VISIBLE);
            fvrtlistView.setVisibility(View.GONE);
        }else {
            nodataMessage.setVisibility(View.GONE);
            fvrtlistView.setVisibility(View.VISIBLE);
        }
    }
    
}

