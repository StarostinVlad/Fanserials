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

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements Serializable {


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

    String SeriaUrl = "", SeriaName = "";
    private AdView mAdView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if (savedInstanceState != null) {
            Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            Log.d("MainFragment", "restored" + Series.size() + ": " + this.hashCode());
        }
        if (getArguments() != null) {
            SeriaUrl = getArguments().getString("Href");
            SeriaName = getArguments().getString("Title");
        }
    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
//            Log.d("MainFragment", "restored" + Series.size()+": "+this.hashCode());
//        }
//        super.onViewStateRestored(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F6D85ACD484BBC108E0F262D8160FA17")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("admob","ad load");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("admob","ad not load");
            }
        });
        mAdView.loadAd(adRequest);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Новинки");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //Activity v = getActivity();
        lv = (ListView) v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = (ProgressBar) v.findViewById(R.id.progressBarFrag);
        //pr3 = (ProgressBar) v.getRootView().findViewById(R.episodeId.progressBar3);
        pr.setVisibility(View.INVISIBLE);
        //pr3.setVisibility(View.INVISIBLE);
        swiperef = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayoutFrag);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });

        if (Series == null && internet()) {
            Log.d("MainFragment", "download series from internet hash: " + this.hashCode());
            GetSeries gt = new GetSeries();
            gt.execute();
        } else {
            //Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            Log.d("MainFragment", "must fill list!" + Series.size() + " in: " + this.hashCode());
            fill();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < lv.getCount() - 1) {
                    Intent intent = new Intent(getContext(), Video.class);
                    Log.d("MainFragment", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());


                    intent.putExtra("Seria", Series.get(position));
                    MainActivity.newActivity = true;
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
                        GetSeries gt = new GetSeries();
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
                    GetSeries gt = new GetSeries();
                    gt.execute();
                } else swiperef.setRefreshing(false);
            }
        });


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.main_fragment, container, false);
        return v;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("Series", Series);

        super.onSaveInstanceState(savedInstanceState);
        Log.d("MainFragment", "saved");
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

    class GetSeries extends AsyncTask<Void, Void, Void> {

        Boolean newPage = false;

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String queryUrl;
            try {
                if (quick_help.CheckResponceCode("https://mrstarostinvlad.000webhostapp.com/actual_adres.php") && !EndList) {
                    doc = getDocument();

                    if (!add) {
                        Series = new ArrayList<>();
                    }
                    if (!newPage) {
                        Series.addAll(getSeriesOfSerial(doc));
                    } else {
                        Series.addAll(getNewSeries(doc));
                    }
                    Log.d("MainFragment", "size=" + Series.size());
                    if (Series.size() % 32 != 0 && !newPage) EndList = true;
                } else {
                    Log.d("MainFragment", " Something wrong with connection!");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("MainFragment", "parse end");
            return null;
        }

        ArrayList<Seria> getSeriesOfSerial(Document doc) {
            ArrayList<Seria> Series = new ArrayList<>();
            Elements item_serials = doc.select("li div div.item-newSerial");//li div div div div.field-img

            for (Element item_serial : item_serials) {
                String title = item_serial.select("div.newSerial-bottom div.field-serialTitle a").text();
                String description = item_serial.select("div.newSerial-bottom div.field-serialDescription a").text();
                Log.d("MainFragment", item_serial.select("div.newSerial-top div.field-img a").attr("serialHref"));
                String href = item_serial.select("div.newSerial-top div.field-img a").attr("serialHref");

                Pattern pattern = Pattern.compile("'(.*?)'");
                Matcher matcher = pattern
                        .matcher(item_serial.select("div.newSerial-top div.field-img")
                                .attr("style"));
                String img = matcher.find() ? matcher.group(1) : "";
                //img = (img = s.substring(0, s.indexOf("?v="))).substring(img.indexOf("episodeUrl('") + 5);

                Seria seria = new Seria(title, href, img, description);
                Series.add(seria);
            }
            return Series;
        }

        ArrayList<Seria> getNewSeries(Document doc) {
            Log.d("MainFragment", doc.body().html());
            ArrayList<Seria> Series = new ArrayList<>();
            FanserJsonApi fanserJsonApi = null;
            try {
                fanserJsonApi = LoganSquare.parse(doc.body().html(), FanserJsonApi.class);
                for (FanserJsonApi.DataOfNewSer item : fanserJsonApi.dataOfSerials) {
                    Log.d("Name ", item.newSerial.serialName + " : " + item.serialEpisode.episodeName + " : " + item.serialEpisode.episodeUrl + " : " + item.serialEpisode.episodeImages.smallImage);
                    Seria seria = new Seria(item.newSerial.serialName, item.serialEpisode.episodeUrl,
                            item.serialEpisode.episodeImages.smallImage, item.serialEpisode.episodeName);
                    Series.add(seria);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Series;
        }

        Document getDocument() {
            Document doc = null;
            String queryUrl = "";
            //Boolean newPage = false;
            try {
                doc = Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/actual_adres.php").get();
                //queryUrl=doc.select("div div.l-container div.c-header__inner a.c-header__link").attr("serialHref").substring(2);
                queryUrl = doc.select("h1").text();

                sPref = getContext().getSharedPreferences("URL", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(SAVED_TEXT, "http://" + queryUrl);
                ed.commit();


                if (getArguments() != null) {
                    sPref = getContext().getSharedPreferences("URL", MODE_PRIVATE);
                    queryUrl = sPref.getString(SAVED_TEXT, "");
                    String href = "";
                    if ((href = getArguments().getString("Href")).contains("http"))
                        queryUrl = href;
                    else
                        queryUrl += href;

                    if (queryUrl.lastIndexOf("/") < queryUrl.length() - 1)
                        queryUrl += "/";

                    newPage = false;

                } else {
                    queryUrl = "http://" + queryUrl + "/api/v1/episodes?limit=30&offset=";
                    newPage = true;
                }
                Log.d("MainFragment", "queryUrl=" + queryUrl);

                Log.d("MainFragment", "code = 200");
                if (add) {

                    if (newPage)
                        queryUrl += (30 * (page - 1));
                    else
                        queryUrl += "page/" + page + "/";//Jsoup.connect("http://fanserials.biz/new/page/"+page).userAgent(agent).timeout(10000).get();

                } else {
                    if (newPage)
                        queryUrl += "0";
                    Log.d("MainFragment", "query if not add: " + queryUrl);
                }
                doc = Jsoup.connect(queryUrl).ignoreContentType(true).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
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
            if (getArguments() != null) {
                toolbar.setTitle(getArguments().getString("Title"));
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
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