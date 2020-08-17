package com.starostinvlad.fan.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.starostinvlad.fan.R;

import java.util.ArrayList;

/**
 * Created by Star on 03.02.2018.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private ArrayList<LiteralOfSerials> groups;
    private Context context;
    public MyExpandableAdapter(Context context, ArrayList<LiteralOfSerials> groups) {
        this.context=context;
        this.groups=groups;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Serial> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Serial child = (Serial) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandablelist_child_style, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.Tittle_text);
        //ImageView iv = (ImageView) convertView.findViewById(R.episodeId.foundSerialPoster);

        tv.setText(child.getName().toString());
        //Picasso.with(getApplicationContext()).load(child.getImage()).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(iv);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Serial> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        LiteralOfSerials group = (LiteralOfSerials) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.expandablelist_group_style, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.Symbol_text);
        tv.setText(group.getName());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}