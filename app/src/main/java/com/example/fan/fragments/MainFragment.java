package com.example.fan.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.example.fan.R;
import com.example.fan.api.retro.Datum;
import com.example.fan.api.retro.FANAPI;
import com.example.fan.api.retro.NetworkService;
import com.example.fan.utils.Seria;
import com.example.fan.utils.SeriaListAdapter;
import com.example.fan.utils.SeriasGetter;
import com.example.fan.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements Serializable {


    final String SAVED_TEXT = "saved_text";
    protected ListView lv;
    protected ProgressBar pr;//, pr3;
    protected MyAdap adap;
    protected SeriaListAdapter seriaListAdapter;
    protected SwipeRefreshLayout swiperef;
    protected ArrayList<HashMap<String, Object>> data;
    protected HashMap<String, Object> map;
    ArrayList<Seria> Series;
    int[] to = {R.id.textV, R.id.imgV, R.id.textView2};
    String[] from = {"Name", "Icon", "Anno"};
    Toolbar toolbar;
    SharedPreferences sPref;

    int page = 1;

    Utils utils = new Utils();

    String SeriaUrl = "", SeriaName = "";
    SeriasGetter seriasGetter = new SeriasGetter();
    List<Datum> list;
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
        pr.setVisibility(View.VISIBLE);
        //pr3.setVisibility(View.INVISIBLE);
        swiperef = v.findViewById(R.id.swipeRefreshLayoutFrag);

        toolbar = getActivity().findViewById(R.id.toolbar);

        list = new ArrayList<>();


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });


        fillList(0);

//        if (utils.isNetworkOnline(getContext())) {
//            //Log.d("MainFragment", "download series from internet hash: " + this.hashCode());
//            if (Series == null) {
//                GetSeries gt = new GetSeries(Series, "new");
//                gt.execute();
//            } else if (!Series.isEmpty()) {
//                //Series = (ArrayList<Seria>) savedInstanceState.getSerializable("Series");
//                //Log.d("MainFragment", "must fill list!" + Series.size() + " in: " + this.hashCode());
//                fill(Series);
//            }
//        } else {
//            alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
//        }
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                if (position < lv.getCount() - 1) {
//
//                    //Log.d("MainFragment", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());
//
//                    mInterstitialAd.setAdListener(new AdListener() {
//
//                        @Override
//                        public void onAdFailedToLoad(int i) {
//                            super.onAdFailedToLoad(i);
//                            //Log.d("ADS", "The interstitial failed");
//                        }
//                    });
////                    if (mInterstitialAd.isLoaded()) {
////                        mInterstitialAd.show();
////                        //Log.d("ADS", "The interstitial show.");
////                    } else {
//                    Log.d("ADS", "The interstitial wasn't loaded yet.");
//                    Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
//                    intent.putExtra("Seria", Series.get(position));
//                    MainActivity.newActivity = true;
//                    startActivity(intent);
////                    }
////                    mInterstitialAd.setAdListener(new AdListener() {
////                        @Override
////                        public void onAdClosed() {
////                            Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
////                            // Load the next interstitial.
////                            mInterstitialAd.loadAd(new AdRequest.Builder()
////                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
////                                    .addTestDevice("0AE50CA39585DAB4D218A0C9516422A1").build());
////                            intent.putExtra("Seria", Series.get(position));
////                            MainActivity.newActivity = true;
////                            startActivity(intent);
////                        }
////                    });
//                }
//            }
//        });
//
//
        final LayoutInflater inflater_frag = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewpr = inflater_frag.inflate(R.layout.prog, null);
        lv.addFooterView(viewpr);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (utils.isNetworkOnline(getContext())) {
//                        if (Series.size() % seriasGetter.items == 0) {
//                            GetSeries gt = new GetSeries(Series, "add");
//                            gt.execute();
//                        }
                        fillList(view.getCount());
                    } else {
                        alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
                    }
//                    if (Series.size() % seriasGetter.items != 0)
//                        viewpr.setVisibility(View.INVISIBLE);
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
                if (utils.isNetworkOnline(getContext())) {
//                    GetSeries gt = new GetSeries(Series, "new");
//                    gt.firstly = false;
//                    gt.execute();
                    list.clear();
                    Log.d("refresh", "refresh");
                    fillList(0);
                } else {
                    alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
                    swiperef.setRefreshing(false);
                }
            }
        });

        return v;
    }

    void fillList(final int offset) {
        NetworkService.getInstance()
                .getSerials()
                .getSerials(offset)
                .enqueue(new Callback<FANAPI>() {
                    @Override
                    public void onResponse(@NonNull Call<FANAPI> call, @NonNull Response<FANAPI> response) {
                        FANAPI post = response.body();
                        assert post != null;
                        list.addAll(post.getDatumList());

                        if (seriaListAdapter == null) {
                            seriaListAdapter = new SeriaListAdapter(list, getContext());
                            lv.setAdapter(seriaListAdapter);
                        } else
                            seriaListAdapter.notifyDataSetChanged();

//                        Log.d("retrofit", list.get(list.size() - 1).getSerial().getName());
                        swiperef.setRefreshing(false);
                        pr.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<FANAPI> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
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

    void fill(ArrayList<Seria> serias) {
        try {
            String title = "Не удалось подключиться к сайту!";
            String message = "Возможно сайт в данный момент недоступен, попробуйте позже.";
            if (serias == null) {
                alarm(title, message);
                return;
            }
            if (serias.isEmpty()) {
                if (getArguments() != null) {
                    title = "К сожалению";
                    message = "\"" + getArguments().getString("Title") + "\" недоступен из приложения!";
                }
                lv.setVisibility(View.INVISIBLE);
                alarm(title, message);

            }
            if (data == null) {
                data = new ArrayList<>(serias.size());
            } else {
                data.clear();
                lv.refreshDrawableState();
                adap.notifyDataSetChanged();
            }

            for (Seria seria : serias) {
                map = new HashMap<>();
                map.put("Name", "" + seria.getName());
                map.put("Icon", "" + seria.getImage());
                map.put("Anno", "" + seria.getDescription());
                data.add(map);
            }
            if (data != null) {
                adap = new MyAdap(getContext(), data, R.layout.ser_list_item, from, to);
                lv.setAdapter(adap);
                swiperef.setRefreshing(false);
            } else {
                adap.notifyDataSetChanged();
                swiperef.setRefreshing(false);
            }
            Series = serias;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void add(ArrayList<Seria> serias) {
        data.clear();
        lv.refreshDrawableState();
        adap.notifyDataSetChanged();
        for (Seria seria : serias) {
            map = new HashMap<>();
            map.put("Name", "" + seria.getName());
            map.put("Icon", "" + seria.getImage());
            map.put("Anno", "" + seria.getDescription());
            data.add(map);
        }
        adap.notifyDataSetChanged();
    }

    class GetSeries extends AsyncTask<String, String, Void> {

        ArrayList<Seria> serias;

        boolean firstly = true;

        String command = "";

        public GetSeries(ArrayList<Seria> serias, String command) {
            this.serias = serias;
            this.command = command;
        }


        @Override
        protected Void doInBackground(String... parametr) {
            try {
                Utils utils = new Utils();
                if (utils.CheckResponceCode("https://mrstarostinvlad.000webhostapp.com/actual_adres.php")) {

                    seriasGetter.domain = utils.getActualDomain(getContext());
                    seriasGetter.context = getContext();
                    seriasGetter.setSerias(serias);
                    switch (command) {
                        case "new":
                            if (StringUtils.isNotEmpty(SeriaUrl))
                                serias = seriasGetter.getSeriesOfSerial(SeriaUrl);
                            else
                                serias = seriasGetter.getNewSeries();
                            break;
                        case "add":
                            if (StringUtils.isNotEmpty(SeriaUrl))
                                serias = seriasGetter.addSeriesOfSerial(SeriaUrl);
                            else
                                serias = (seriasGetter.addNewSeries());
                            break;
                    }
                } else {
                    //Log.d("MainFragment", " Something wrong with connection!");
                    return null;
                }
            } catch (Exception e) {
                publishProgress("Внимание", "Превышено время ожидания ответа");
                e.printStackTrace();
                return null;
            }
            //Log.d("MainFragment", "parse end");
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (firstly && !command.equals("add"))
                pr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // adapter.add("");
            pr.setVisibility(View.INVISIBLE);
            if (serias != null)
                if (command.equals("add")) add(serias);
                else fill(serias);
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