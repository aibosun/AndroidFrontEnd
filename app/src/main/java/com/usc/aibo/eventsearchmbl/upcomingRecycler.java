package com.usc.aibo.eventsearchmbl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class upcomingRecycler extends RecyclerView.Adapter<upcomingRecycler.ViewHolder> {

    private LayoutInflater inflater;
    //private ItemClickListener mClickListener;
    
    private List<JSONObject> upcomingArray;

    public void setUpcomingArray(List<JSONObject> upcomingArray) {
        this.upcomingArray = upcomingArray;
        this.notifyItemRangeInserted(0, upcomingArray.size() - 1);
    }

    public upcomingRecycler(Context context){
        inflater=LayoutInflater.from(context);
        this.upcomingArray=new ArrayList<>();
    }

    @NonNull
    @Override
    public upcomingRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.upcoming_item,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull upcomingRecycler.ViewHolder viewHolder, int i) {
        JSONObject currentEn = null;
        try {
            currentEn = upcomingArray.get(i);
            viewHolder.ucName.setText(currentEn.getString("displayName"));
            viewHolder.ucPerform.setText(currentEn.getJSONArray("performance").getJSONObject(0).getString("displayName"));
            String dateinfo=currentEn.getJSONObject("start").getString("date");
            if(currentEn.getJSONObject("start").getString("time")!="null"){
                dateinfo=dateinfo+" "+currentEn.getJSONObject("start").getString("time");
            }
            viewHolder.ucDate.setText(dateinfo);
            viewHolder.ucType.setText(currentEn.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return upcomingArray.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ucName;
        TextView ucPerform;
        TextView ucDate;
        TextView ucType;
        ViewHolder(View itemView) {
            super(itemView);
            ucName = (TextView)itemView.findViewById(R.id.ucName);
            ucPerform = (TextView)itemView.findViewById(R.id.ucPerform);
            ucDate = (TextView)itemView.findViewById(R.id.ucDate);
            ucType = (TextView)itemView.findViewById(R.id.ucType);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            String url = "";
            try {
                url= upcomingArray.get(getAdapterPosition()).getString("uri");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(browserIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //currentEn.getJSONObject("uri")
        }
    }

}
