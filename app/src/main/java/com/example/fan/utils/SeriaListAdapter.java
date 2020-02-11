package com.example.fan.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fan.R;
import com.example.fan.api.retro.Datum;
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ser_list_item, parent, false);
        }

        TextView title = view.findViewById(R.id.seria_title);
        TextView sub_title = view.findViewById(R.id.seria_subtitle);
        ImageView image = view.findViewById(R.id.seria_image);

        final Datum data = datumList.get(position);

        title.setText(data.getSerial().getName());
        sub_title.setText(data.getEpisode().getName());

        Picasso.with(context).load(data.getEpisode().getImages().getSmall()).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(image);
        return view;
    }
}
