package com.example.fan.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.bluelinelabs.logansquare.LoganSquare;
import com.example.fan.R;
import com.example.fan.activities.MainActivity;
import com.example.fan.api.FanserialsAlphabetApi;
import com.example.fan.utils.LiteralOfSerials;
import com.example.fan.utils.MyExpandableAdapter;
import com.example.fan.utils.Serial;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class AllSerialsFragment extends Fragment implements Serializable {

    ExpandableListView elvMain;
    ArrayList<LiteralOfSerials> list;
    int SerialType = 0;

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putSerializable("SerialsList", list);
//
//        super.onSaveInstanceState(savedInstanceState);
//        Log.d("AllSerialsFragment", "saved: "+!savedInstanceState.isEmpty());
//    }

    private ProgressBar pr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        SerialType = getArguments().getInt("SerialType", 1);

//        if (savedInstanceState != null) {
//            Log.d("AllSerialsFragment", "onCreate: "+!savedInstanceState.isEmpty());
//            list = (ArrayList<LiteralOfSerials>) savedInstanceState.getSerializable("SerialsList");
//            Log.d("AllSerialsFragment", "restored" + list.size()+": "+this.hashCode());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.content_all_serials_activity, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        String toolbarTittle;
        switch (SerialType) {
            case 2:
                toolbarTittle = "Мультсериалы";
                break;
            case 3:
                toolbarTittle = "Аниме";
                break;
            case 4:
                toolbarTittle = "Документальные";
                break;
            default:
                toolbarTittle = "Все сериалы";
                break;
        }
        toolbar.setTitle(toolbarTittle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        pr = (ProgressBar) v.findViewById(R.id.progressBar4);

        elvMain = (ExpandableListView) v.findViewById(R.id.ExpandableListOfAllSerials);


        if (list == null) {
            pr.setVisibility(View.VISIBLE);
            GetSerials gt = new GetSerials();
            gt.execute();
        } else {
            fill();
        }
        //return inflater.inflate(R.layout.all_serials_fragment, container, false);
        return v;
    }

    void fill() {
        MyExpandableAdapter adapter = new MyExpandableAdapter(getContext(), list);
        elvMain.setAdapter(adapter);
        elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Log.d("AllSerialsFragment", list.get(groupPosition).getItems().get(childPosition).getName());
                //Log.d("AllSerialsFragment", list.get(groupPosition).getItems().get(childPosition).getUri());

                Bundle args = new Bundle();
                args.putString("Href", list.get(groupPosition).getItems().get(childPosition).getUri());
                args.putString("Title", list.get(groupPosition).getItems().get(childPosition).getName());
                MainFragment bFragment = new MainFragment();
                bFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_act_id, bFragment)
                        .addToBackStack(null).commit();
                MainActivity.lastItem.add(R.id.new_series);

                return false;
            }
        });
    }


    private class GetSerials extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            list = new ArrayList<>();

            String queryUrl = "https://mrstarostinvlad.000webhostapp.com/fanka" + (SerialType - 1) + ".json";
            //Log.d("AllSerialsFragment", "queryUrl " + queryUrl);

            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
            try {
                doc = Jsoup.connect(queryUrl).ignoreContentType(true).get();

                FanserialsAlphabetApi fanserialsAlphabetApi =
                        LoganSquare.parse(doc.body().html(), FanserialsAlphabetApi.class);

                if (fanserialsAlphabetApi.dataOfSerials != null)
                    for (FanserialsAlphabetApi.DataOfSerial dataOfSerial : fanserialsAlphabetApi.dataOfSerials) {
                        ArrayList<Serial> Serials = new ArrayList<>();
                        for (FanserialsAlphabetApi.DataOfSerial.Serial serial : dataOfSerial.serialsList) {
                            //Log.d("AllSerialsFragment", "ser: "+serial.serialTitle +" : "+serial.serialHref);
                            Serial Serial = new Serial();
                            Serial.setName(serial.serialTitle);
                            Serial.setUri(serial.serialHref);
                            Serials.add(Serial);
                        }
                        LiteralOfSerials group = new LiteralOfSerials(dataOfSerial.literalOfSerial, Serials);
                        list.add(group);
                    }
            } catch (Exception e) {
                Log.e("AllSerialsFragment", "error: " + e);
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