package com.starostinvlad.fan.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.api.retro.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchListAdapter extends BaseAdapter {

    List<SearchResult> searchResults = null;
    Context context;

    public SearchListAdapter(List<SearchResult> searchResults, Context context) {
        this.searchResults = searchResults;
        this.context = context;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.search_ser_list_item, parent, false);
        }

        TextView title = view.findViewById(R.id.search_title);
        TextView sub_title = view.findViewById(R.id.search_subtitle);
        ImageView image = view.findViewById(R.id.search_img);

        final SearchResult data = searchResults.get(position);

        title.setText(data.getName());
        sub_title.setText(data.getDescription());

        Picasso.with(context).load(data.getPoster().getLarge()).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(image);
        return view;
    }
}