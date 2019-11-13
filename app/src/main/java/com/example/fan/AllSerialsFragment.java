package com.example.fan;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.bluelinelabs.logansquare.LoganSquare;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;


public class AllSerialsFragment extends Fragment implements Serializable {

    final String SAVED_TEXT = "saved_text";
    ExpandableListView elvMain;
    ArrayList<LiteralOfSerials> list;
    int SerialType = 0;
    SharedPreferences sPref;

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
        SerialType = getArguments().getInt("SerialType", 0);

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

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        String toolbarTittle;
        switch (SerialType) {
            case 1:
                toolbarTittle = "Мультсериалы";
                break;
            case 2:
                toolbarTittle = "Аниме";
                break;
            case 3:
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
                Log.d("AllSerialsFragment", list.get(groupPosition).getItems().get(childPosition).getName());
                Log.d("AllSerialsFragment", list.get(groupPosition).getItems().get(childPosition).getUri());

                Bundle args = new Bundle();
                args.putString("Href", list.get(groupPosition).getItems().get(childPosition).getUri());
                args.putString("Title", list.get(groupPosition).getItems().get(childPosition).getName());
                MainFragment bFragment = new MainFragment();
                bFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_act_id, bFragment)
                        .addToBackStack(null).commit();

//                startActivity(new Intent(getContext(),MainActivity.class)
//                        .putExtra("Uri",list.get(groupPosition).getItems().get(childPosition).getUri())
//                        .putExtra("Name",list.get(groupPosition).getItems().get(childPosition).getName()));
                return false;
            }
        });
    }

    void saveFile(String string) throws IOException {
        File path = getContext().getExternalFilesDir(null);
        File file = new File(path, "my-file-episodeName.txt");
        Log.d("AllSerialsFragment", "path=" + path.getAbsolutePath());
        FileOutputStream stream = new FileOutputStream(file);
        try {
            String h = (string);
            stream.write(h.getBytes("UTF8"));
        } finally {
            stream.close();
        }
    }

    private class GetSerials extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            list = new ArrayList<>();

            sPref = getActivity().getSharedPreferences("URL", 0);
            String queryUrl = sPref.getString(SAVED_TEXT, "") + "/alphabet/" + SerialType;
            queryUrl = "https://mrstarostinvlad.000webhostapp.com/fanka"+SerialType+".json";
            Log.d("AllSerialsFragment", "queryUrl " + queryUrl);

            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
            try {
                doc = Jsoup.connect(queryUrl).ignoreContentType(true).get();

//                String decodeDoc = StringEscapeUtils.unescapeHtml(doc.html()).replace("<\\","<")
//                        .replace("\\/","/")
//                        .replace("\\\"","");
//                String decodeDoc = StringEscapeUtils.unescapeJavaScript((doc.body().html()));
//                decodeDoc = decodeDoc.substring(0,decodeDoc.indexOf("\",\"countries"));
//                String find = "alphabet\":\"";
//                decodeDoc = decodeDoc.substring(decodeDoc.indexOf(find)+find.length());


//                Log.d("AllSerialsFragment", "html: " + decodeDoc);

//                doc = Jsoup.parse(decodeDoc);
//                doc = Jsoup.parse(StringEscapeUtils.unescapeHtml((doc.html())));

                FanserialsAlphabetApi fanserialsAlphabetApi =
                        LoganSquare.parse(doc.body().html(), FanserialsAlphabetApi.class);

//                doc = Jsoup.parse(fanserialsAlphabetApi.alphabet);
//
//                Elements literals = doc.select("div.literalOfSerial");
//                for (Element literalOfSerial : literals) {
//                    String liter = StringEscapeUtils.unescapeJava(literalOfSerial.select("div.literal__header").text());
//                    Log.d("AllSerialsFragment", "literalOfSerial: " + literalOfSerial.html());
//                    ArrayList<Serial> Serials = new ArrayList<>();
//                    for (Element li : literalOfSerial.select("ul li")) {
//                        String text = StringEscapeUtils.unescapeJava(li.text());
//                        Log.d("AllSerialsFragment", "li text: " + text);
//                        //Log.d("AllSerialsFragment","li src: "+li.select("img").attr("src"));
//                        Serial Serial = new Serial();
//                        Serial.setName(text);
//                        Serial.setUri(li.select("a").attr("serialHref"));
//                        Serial.setImage(li.select("img").attr("src"));
//                        Serials.add(Serial);
//                    }
//                    LiteralOfSerials group = new LiteralOfSerials(liter, Serials);
//                    list.add(group);
//                }
                if(fanserialsAlphabetApi.dataOfSerials !=null)
                for(FanserialsAlphabetApi.DataOfSerial dataOfSerial : fanserialsAlphabetApi.dataOfSerials){
                    ArrayList<Serial> Serials = new ArrayList<>();
                    for (FanserialsAlphabetApi.DataOfSerial.Serial serial : dataOfSerial.serialsList){
                        Log.d("AllSerialsFragment", "ser: "+serial.serialTitle +" : "+serial.serialHref);
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
//            if(quick_help.CheckResponceCode(queryUrl)){
//                list=quick_help.sendReq(queryUrl+"/alphabet/",SerialType);
//            }
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