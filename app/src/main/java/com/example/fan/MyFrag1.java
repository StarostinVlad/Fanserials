package com.example.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class MyFrag1 extends Fragment {

    ExpandableListView elvMain;
    ArrayList<MyGroup> list;

    private ProgressBar pr;
    int SerialType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        SerialType = getArguments().getInt("SerialType", 0);

//        if (savedInstanceState != null) {
//            Log.d("MyFrag1", "onCreate: "+!savedInstanceState.isEmpty());
//            list = (ArrayList<MyGroup>) savedInstanceState.getSerializable("SerialsList");
//            Log.d("MyFrag1", "restored" + list.size()+": "+this.hashCode());
//        }
    }

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putSerializable("SerialsList", list);
//
//        super.onSaveInstanceState(savedInstanceState);
//        Log.d("MyFrag1", "saved: "+!savedInstanceState.isEmpty());
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_all_serials_activity, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        String toolbarTittle;
        switch(SerialType){
            case 1 :
                toolbarTittle="Мультсериалы";
                break;
            case 2 :
                toolbarTittle="Аниме";
                break;
            case 3 :
                toolbarTittle="Документальные";
                break;
            default:
                toolbarTittle="Все сериалы";
                break;
        }
        toolbar.setTitle(toolbarTittle);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        pr=(ProgressBar)v.findViewById(R.id.progressBar4);

        elvMain = (ExpandableListView) v.findViewById(R.id.ExpandableListOfAllSerials);



        if(list==null) {
            pr.setVisibility(View.VISIBLE);
            getHref gt = new getHref();
            gt.execute();
        }else{
            fill();
        }
        //return inflater.inflate(R.layout.myfrag1, container, false);
        return v;
    }



    void fill(){
        MyExpandableAdapter adapter = new MyExpandableAdapter(getContext(),list);
        elvMain.setAdapter(adapter);
        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d("MyFrag1", list.get(groupPosition).getItems().get(childPosition).getName());
                Log.d("MyFrag1", list.get(groupPosition).getItems().get(childPosition).getUri());

                Bundle args = new Bundle();
                args.putString("Uri", list.get(groupPosition).getItems().get(childPosition).getUri());
                args.putString("Name", list.get(groupPosition).getItems().get(childPosition).getName());
                MyFrag bFragment= new MyFrag();
                bFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_act_id,bFragment)
                        .addToBackStack(null).commit();

//                startActivity(new Intent(getContext(),MainActivity.class)
//                        .putExtra("Uri",list.get(groupPosition).getItems().get(childPosition).getUri())
//                        .putExtra("Name",list.get(groupPosition).getItems().get(childPosition).getName()));
                return false;
            }
        });
    }

    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";


    class getHref extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            list = new ArrayList<MyGroup>();
            ArrayList<Child> ch_list;

            sPref = getActivity().getSharedPreferences("URL",0);
            String queryUrl = sPref.getString(SAVED_TEXT, "");
            Log.d("MyFrag1", "queryUrl " + queryUrl);

            String agent="Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
            String s, img = "";
            if(quick_help.CheckResponceCode(queryUrl)){
                list=quick_help.sendReq(queryUrl+"/alphabet/",SerialType);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            pr.setVisibility(View.INVISIBLE);
            fill();
            super.onPostExecute(result);
        }

    }

}