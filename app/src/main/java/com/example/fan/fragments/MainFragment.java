package com.example.fan.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import com.example.fan.activities.ExoPlayerActivity;
import com.example.fan.activities.MainActivity;
import com.example.fan.R;
import com.example.fan.utils.Seria;
import com.example.fan.api.FanserJsonApi;
import com.example.fan.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Utils quickHelp = new Utils();

    String SeriaUrl = "", SeriaName = "";
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if (savedInstanceState != null) {
            Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            //Log.d("MainFragment", "restored" + Series.size() + ": " + this.hashCode());
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

        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()));

        mInterstitialAd.setAdUnitId(getString(R.string.ad_screen));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        String title = !Objects.equals(SeriaName, "") ? SeriaName : "Новинки";
        toolbar.setTitle(title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //Activity v = getActivity();
        lv = v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = v.findViewById(R.id.progressBarFrag);
        //pr3 = (ProgressBar) v.getRootView().findViewById(R.episodeId.progressBar3);
        pr.setVisibility(View.INVISIBLE);
        //pr3.setVisibility(View.INVISIBLE);
        swiperef = v.findViewById(R.id.swipeRefreshLayoutFrag);

        toolbar = getActivity().findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });

        if (quickHelp.isNetworkOnline(getContext())) {
            //Log.d("MainFragment", "download series from internet hash: " + this.hashCode());
            if (Series == null) {
                GetSeries gt = new GetSeries();
                gt.execute();
            } else if (!Series.isEmpty()) {
                //Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
                //Log.d("MainFragment", "must fill list!" + Series.size() + " in: " + this.hashCode());
                fill();
            }
        } else {
            alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position < lv.getCount() - 1) {

                    //Log.d("MainFragment", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            //Log.d("ADS", "The interstitial failed");
                        }
                    });
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        //Log.d("ADS", "The interstitial show.");
                    } else {
                    Log.d("ADS", "The interstitial wasn't loaded yet.");
                    Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
                    intent.putExtra("Seria", Series.get(position));
                    MainActivity.newActivity = true;
                    startActivity(intent);
                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
                            // Load the next interstitial.
                            mInterstitialAd.loadAd(new AdRequest.Builder()
                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                    .addTestDevice("0AE50CA39585DAB4D218A0C9516422A1").build());
                            intent.putExtra("Seria", Series.get(position));
                            MainActivity.newActivity = true;
                            startActivity(intent);
                        }
                    });
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
                    if (quickHelp.isNetworkOnline(getContext())) {
                        if (!add && !EndList) {
                            add = true;
                            GetSeries gt = new GetSeries();
                            gt.execute();
                        }
                    } else {
                        alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
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
                if (quickHelp.isNetworkOnline(getContext())) {
                    GetSeries gt = new GetSeries();
                    gt.execute();
                } else {
                    alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
                    swiperef.setRefreshing(false);
                }
            }
        });


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.main_fragment, container, false);
        return v;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("Series", Series);

        super.onSaveInstanceState(savedInstanceState);
        //Log.d("MainFragment", "saved");
    }

    public void alarm(String Title, String message) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(Title)
                    .setMessage(message)
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
    }


    void fill() {
        String title = "Не удалось подключиться к сайту!";
        String message = "Возможно сайт в данный момент недоступен, попробуйте позже.";
        if (Series == null) {
            alarm(title, message);
            return;
        }
        if (Series.isEmpty()) {
            if (getArguments() != null) {
                title = "К сожалению";
                message = "\"" + getArguments().getString("Title") + "\" недоступен из приложения!";
            }
            lv.setVisibility(View.INVISIBLE);
            alarm(title, message);

        }
        if (data == null && Series != null) {
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

    class GetSeries extends AsyncTask<Void, String, Void> {

        Boolean newPage = false;

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String queryUrl;
            try {
                if (Utils.CheckResponceCode("https://mrstarostinvlad.000webhostapp.com/actual_adres.php") && !EndList) {
                    doc = getDocument();

                    if (!add) {
                        Series = new ArrayList<>();
                    }
                    if (!newPage) {
                        Series.addAll(getSeriesOfSerial(doc));
                    } else {
                        Series.addAll(getNewSeries(doc));
                    }
                    //Log.d("MainFragment", "size=" + Series.size());
                    if (Series != null)
                        if (Series.size() % 32 != 0 && !newPage) EndList = true;
                } else {
                    //Log.d("MainFragment", " Something wrong with connection!");
                    return null;
                }
            } catch (Exception e) {
                publishProgress("Внимание", "Превышено время ожидания ответа");
                try {
                    Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/fanserials/getstatistic.php")
                            .data("andv", Build.VERSION.RELEASE)
                            .data("device", Build.DEVICE)
                            .data("model", Build.MODEL)
                            .data("body", e.toString())
                            .get();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                return null;
            }
            //Log.d("MainFragment", "parse end");
            return null;
        }

        ArrayList<Seria> getSeriesOfSerial(Document doc) {
            ArrayList<Seria> Series = new ArrayList<>();
            Elements item_serials = doc.select("li div div.item-serial");//li div div div div.field-img

            for (Element item_serial : item_serials) {
                String title = item_serial.select("div.serial-bottom div.field-title a").text();
                String description = item_serial.select("div.serial-bottom div.field-description a").text();

                String href = item_serial.select("div.serial-top div.field-img a").attr("href");
                //Log.d("MainFragment", "href: " + description);
                Pattern pattern = Pattern.compile("'(.*?)'");
                Matcher matcher = pattern
                        .matcher(item_serial.select("div.serial-top div.field-img")
                                .attr("style"));
                String img = matcher.find() ? matcher.group(1) : "";
                //img = (img = s.substring(0, s.indexOf("?v="))).substring(img.indexOf("episodeUrl('") + 5);

                Seria seria = new Seria(title, href, img, description);
                Series.add(seria);
            }
            return Series;
        }

        ArrayList<Seria> getNewSeries(Document doc) throws IOException {
            //Log.d("MainFragment", doc.body().html());
            ArrayList<Seria> Series = new ArrayList<>();
            FanserJsonApi fanserJsonApi = null;
            fanserJsonApi = LoganSquare.parse(doc.body().html(), FanserJsonApi.class);
            if (fanserJsonApi.dataOfSerials.isEmpty()) {
                Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/fanserials/getstatistic.php")
                        .data("andv", Build.VERSION.RELEASE)
                        .data("device", Build.DEVICE)
                        .data("model", Build.MODEL)
                        .data("body", doc.body().toString())
                        .get();
            } else
                for (FanserJsonApi.DataOfNewSer item : fanserJsonApi.dataOfSerials) {
                    //Log.d("Name ", item.newSerial.serialName + " : " + item.serialEpisode.episodeName + " : " + item.serialEpisode.episodeUrl + " : " + item.serialEpisode.episodeImages.smallImage);
                    Seria seria = new Seria(item.newSerial.serialName, item.serialEpisode.episodeUrl,
                            item.serialEpisode.episodeImages.smallImage, item.serialEpisode.episodeName);
                    Series.add(seria);
                }
            return Series;
        }

        Document getDocument() throws IOException {
            Document doc = null;
            String queryUrl = "";
            //Boolean newPage = false;
            doc = Jsoup.connect("https://mrstarostinvlad.000webhostapp.com/actual_adres.php").timeout(10000).get();
            //queryUrl=doc.select("div div.l-container div.c-header__inner a.c-header__link").attr("serialHref").substring(2);
            queryUrl = doc.select("h1").text();

            sPref = Objects.requireNonNull(getContext()).getSharedPreferences("URL", MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(SAVED_TEXT, "http://" + queryUrl);
            ed.apply();


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
            //Log.d("MainFragment", "queryUrl=" + queryUrl);

            //Log.d("MainFragment", "code = 200");
            if (add) {

                if (newPage)
                    queryUrl += (30 * (page - 1));
                else
                    queryUrl += "page/" + page + "/";
                //Jsoup.connect("http://fanserials.biz/new/page/"+page).userAgent(agent).timeout(10000).get();

            } else {
                if (newPage)
                    queryUrl += "0";
                //Log.d("MainFragment", "query if not add: " + queryUrl);
            }

            //                doc = Jsoup.connect(queryUrl).ignoreContentType(true).get();


            Log.d("proxy", "port: ");
            Connection.Response request = Jsoup.connect(queryUrl)
                    .ignoreContentType(true).method(Connection.Method.GET).execute();
            if (request.statusCode() == 502 || request.statusCode() == 500) {
                publishProgress("Сайт недоступен", "Попробуйте позже");
            } else if (request.statusCode() == 200)
                doc = request.parse();

            return doc;
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
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values != null)
                alarm(values[0], values[1]);
            super.onProgressUpdate(values);
        }

    }

    class MyAdap extends SimpleAdapter {
        MyAdap(Context context,
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