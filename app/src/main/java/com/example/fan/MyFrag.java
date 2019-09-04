package com.example.fan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyFrag extends Fragment {


    final String SAVED_TEXT = "saved_text";
    protected boolean add = false;
    protected int page = 2;
    protected ListView lv;
    protected ProgressBar pr;//, pr3;
    protected MyAdap adap;
    protected SwipeRefreshLayout swiperef;
    protected ArrayList<HashMap<String, Object>> data;
    protected HashMap<String, Object> map;
    protected int iter = 0;
    ArrayList<Seria> Series;
    int[] to = {R.id.textV, R.id.imgV, R.id.textView2};
    String[] from = {"Name", "Icon", "Anno"};
    Toolbar toolbar;
    boolean EndList = false;
    SharedPreferences sPref;

    String SeriaUrl="",SeriaName="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if (savedInstanceState != null) {
            Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            Log.d("MyFrag", "restored" + Series.size()+": "+this.hashCode());
        }
        if(getArguments()!=null) {
            SeriaUrl = getArguments().getString("Uri");
            SeriaName = getArguments().getString("Name");
        }
    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
//            Log.d("MyFrag", "restored" + Series.size()+": "+this.hashCode());
//        }
//        super.onViewStateRestored(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.myfrag, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Новинки");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //Activity v = getActivity();
        lv = (ListView) v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = (ProgressBar) v.findViewById(R.id.progressBarFrag);
        //pr3 = (ProgressBar) v.getRootView().findViewById(R.id.progressBar3);
        pr.setVisibility(View.INVISIBLE);
        //pr3.setVisibility(View.INVISIBLE);
        swiperef = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutFrag);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });

        if (Series == null && internet()) {
            Log.d("MyFrag", "download series from internet hash: " + this.hashCode());
            getHref gt = new getHref();
            gt.execute();
        } else {
            //Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            Log.d("MyFrag", "must fill list!" + Series.size() + " in: "+this.hashCode());
            fill();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < lv.getCount() - 1) {
                    Intent intent = new Intent(getContext(), Video.class);
                    Log.d("MyFrag", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());


                    intent.putExtra("Seria", Series.get(position));
                    startActivity(intent);
                }
            }
        });


        final LayoutInflater inflater_frag = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewpr = inflater_frag.inflate(R.layout.prog, null);
        lv.addFooterView(viewpr);

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (lv.getLastVisiblePosition() - lv.getHeaderViewsCount() -
                        lv.getFooterViewsCount()) >= (adap.getCount() - 1)) {
                    if (!add && !EndList && internet()) {
                        add = true;
                        getHref gt = new getHref();
                        gt.execute();
                    }
                    if (EndList) viewpr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        swiperef.setColorSchemeColors(Color.RED, Color.YELLOW);
        swiperef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internet()) {
                    getHref gt = new getHref();
                    gt.execute();
                } else swiperef.setRefreshing(false);
            }
        });


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.myfrag, container, false);
        return v;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("Series", Series);

        super.onSaveInstanceState(savedInstanceState);
        Log.d("MyFrag", "saved");
    }

    public boolean internet() {
        if (isNetworkOnline(getContext())) {
            return true;
        } else {
            alarm();
            return false;
        }
    }

    public boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public void alarm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Важное сообщение!")
                .setMessage("Отсутствует доступ к сети!")
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void fill() {
        if (Series.size() == 0) {
            lv.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Важное сообщение!")
                    .setMessage("К сожалению данный сериал недоступен из приложения!")
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    getActivity().finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

        }
        if (data == null) {
            data = new ArrayList<>(Series.size());
        } else {
            data.clear();
            lv.refreshDrawableState();
            adap.notifyDataSetChanged();
        }

        for (Seria seria : Series) {
            map = new HashMap<>();
            map.put("Name", "" + seria.getName());
            map.put("Icon", "" + seria.getImage());
            map.put("Anno", "" + seria.getDescription());
            data.add(map);
        }
        if (data != null) {
            adap = new MyAdap(getContext(), data, R.layout.ser_list_item, from, to);
            lv.setAdapter(adap);
            iter++;
            swiperef.setRefreshing(false);
        } else {
            adap.notifyDataSetChanged();
            swiperef.setRefreshing(false);
        }
    }

    void add() {
        data.clear();
        lv.refreshDrawableState();
        adap.notifyDataSetChanged();
        for (Seria seria : Series) {
            map = new HashMap<>();
            map.put("Name", "" + seria.getName());
            map.put("Icon", "" + seria.getImage());
            map.put("Anno", "" + seria.getDescription());
            data.add(map);
        }
        adap.notifyDataSetChanged();
        page++;
        add = false;
    }

    class getHref extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String s, img, queryUrl, anno = "";

            try {
                if (quick_help.CheckResponceCode("https://mrstarostinvlad.000webhostapp.com/actual_adres.php") && !EndList) {
                    doc = Jsoup.parse(quick_help.GiveDocFromUrl("https://mrstarostinvlad.000webhostapp.com/actual_adres.php"));
                    //queryUrl=doc.select("div div.l-container div.c-header__inner a.c-header__link").attr("href").substring(2);
                    queryUrl = doc.select("h1").text();

                    sPref = getContext().getSharedPreferences("URL", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(SAVED_TEXT, "http://" + queryUrl);
                    ed.commit();


                    if (getArguments()!=null) {
                        sPref = getContext().getSharedPreferences("URL", MODE_PRIVATE);
                        queryUrl = sPref.getString(SAVED_TEXT, "");
                        queryUrl += getArguments().getString("Uri")+"/";
                    } else queryUrl = "http://" + queryUrl + "/new/";
                    Log.d("MyFrag", "queryUrl=" + queryUrl);

                    Log.d("MyFrag", "code = 200");
                    if (add)
                        doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl + "page/" + page + "/"));//Jsoup.connect("http://fanserials.biz/new/page/"+page).userAgent(agent).timeout(10000).get();
                    else {

                        Log.d("MyFrag", "query hmmm= " + queryUrl);

                        doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl));// Jsoup.connect("http://fanserials.biz/new/").userAgent(agent).timeout(10000).get();

                    }
                    Log.d("MyFrag", "give doc");
                    Elements src = doc.select("li div div.item-serial");//li div div div div.field-img
                    Log.d("MyFrag", "src=" + doc.toString());
                    int i = 0;
                    if (!add) {
                        Series = new ArrayList<>();
                    }
                    for (Element ss : src) {
                        String name = ss.select("div.serial-bottom div.field-title a").text();
                        anno = ss.select("div.serial-bottom div.field-description a").text();
                        //Log.d("MyFrag", "an= "+anno);
                        Log.d("MyFrag", ss.select("div.serial-top div.field-img a").attr("href"));
                        String uri = ss.select("div.serial-top div.field-img a").attr("href");
                        s = ss.select("div.serial-top div.field-img").attr("style");
                        img = (img = s.substring(0, s.indexOf("?v="))).substring(img.indexOf("url('") + 5);
                        Seria seria = new Seria(name, uri, img, anno);
                        Series.add(seria);
                        i++;
                    }
                    Log.d("MyFrag", "size=" + Series.size());
                    if (Series.size() % 32 != 0) EndList = true;
                } else {
                    Log.d("MyFrag", " Something wrong with connection!");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("MyFrag", "parse end");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (iter == 0 && !add)
                pr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // adapter.add("");
            pr.setVisibility(View.INVISIBLE);
            if (Series != null)
                if (add) add();
                else fill();
            if (getArguments()!=null) {
                toolbar.setTitle(getArguments().getString("Name"));
                ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            }
        }

    }

    class MyAdap extends SimpleAdapter {
        public MyAdap(Context context,
                      List<? extends Map<String, Object>> data, int resource,
                      String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewImage(ImageView v, String val) {
            Picasso.with(getContext()).load(val).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(v);
        }
    }

}