package com.starostinvlad.fan.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.activities.ExoPlayerActivity;
import com.starostinvlad.fan.api.retro.Datum;

import java.util.List;

import static com.starostinvlad.fan.utils.Utils.PICASSO;

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
        view = LayoutInflater.from(context).inflate(R.layout.ser_list_item, null);

        TextView title = view.findViewById(R.id.seria_title);
        TextView sub_title = view.findViewById(R.id.seria_subtitle);
        ImageView image = view.findViewById(R.id.seria_image);

        title.setText(data.getSerial().getName());
        sub_title.setText(data.getEpisode().getName());

        PICASSO.with(context).load(data.getEpisode().getImages().getSmall())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .into(image);

        view.setOnClickListener(view1 -> openVideo(data));
        return view;
    }

    void openVideo(Datum data) {
        Intent intent = new Intent(context, ExoPlayerActivity.class);
        intent.putExtra("Title", data.getSerial().getName());
        intent.putExtra("SubTitle", data.getEpisode().getName());
        intent.putExtra("URL", data.getEpisode().getUrl());
        context.startActivity(intent);
    }
}
