package com.example.fan.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fan.R;
import com.example.fan.activities.ExoPlayerActivity;
import com.example.fan.api.retro.Datum;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeriaListAdapter extends BaseAdapter {

    List<Datum> datumList = null;
    Context context;

    public SeriaListAdapter(List<Datum> datumList, Context context) {
        this.datumList = datumList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datumList.size();
    }

    @Override
    public Object getItem(int position) {
        return datumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        Log.d("Adapter", position+") data: "+datumList.get(position));
        final Datum data = datumList.get(position);
        if(data != null){
            view = LayoutInflater.from(context).inflate(R.layout.ser_list_item, null);

            TextView title = view.findViewById(R.id.seria_title);
            TextView sub_title = view.findViewById(R.id.seria_subtitle);
            ImageView image = view.findViewById(R.id.seria_image);

            title.setText(data.getSerial().getName());
            sub_title.setText(data.getEpisode().getName());

            Picasso.with(context).load(data.getEpisode().getImages().getSmall())
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.no_image)
                    .into(image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ExoPlayerActivity.class);
                    intent.putExtra("Title", data.getSerial().getName());
                    intent.putExtra("SubTitle", data.getEpisode().getName());
                    intent.putExtra("URL", data.getEpisode().getUrl());
                    context.startActivity(intent);
                }
            });
        }else{
            View view_with_ad = LayoutInflater.from(context).inflate(R.layout.ad_in_listview, null);
            AdView ad_view = view_with_ad.findViewById(R.id.in_list_ad);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("DFDC84BFD22F6F12CEFBDB0880FA290B")
                    .addTestDevice("0AE50CA39585DAB4D218A0C9516422A1")
                    .build();
            ad_view.loadAd(adRequest);
            return view_with_ad;
        }

        return view;
    }
}
