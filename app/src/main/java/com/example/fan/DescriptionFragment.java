package com.example.fan;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class DescriptionFragment extends Fragment {
    TextView text;
    ProgressBar pr;
    Spinner sp;

    public ArrayAdapter<String> uu;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d("sa","onAnnoCreateView fragment");
        View v=inflater.inflate(R.layout.description_fragment,container,false);
        text=(TextView) v.findViewById(R.id.textView3);
        sp = (Spinner)v.findViewById(R.id.spinner);
        //Log.d("sa","onAnnoCreateView end fragment");
        return v;
    }
    protected String uri;
    void fill(final boolean rest, ArrayList<String> l, final ArrayList<String> vidUri,String descript){
        try {
            text.setText(descript);
            uu = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, l);
            sp.setAdapter(uu);
            //pr.setVisibility(View.INVISIBLE);

            if(!vidUri.isEmpty())
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {
                    uri = vidUri.get(selectedItemPosition);
                    if (!rest) {
                        Video.curUri = uri;
                        VideoFragment.starting = false;
                        VideoFragment.seturi(uri, 0);
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    text.setText("nothing");
                }
            });
            else for (String vid : vidUri) {
                //Log.d("tmp","vid="+vid );
            }
        }catch (Exception e){
        }
    }
   /* public void prVisible(boolean visible){
        if(visible)pr.setVisibility(View.VISIBLE);
        else pr.setVisibility(View.INVISIBLE);
    }*/
}
