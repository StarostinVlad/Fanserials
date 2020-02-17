package com.example.fan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.fan.R;
import com.example.fan.activities.ExoPlayerActivity;
import com.example.fan.api.retro.Datum;
import com.example.fan.api.retro.FANAPI;
import com.example.fan.api.retro.NetworkService;
import com.example.fan.api.retro.Viewed;
import com.example.fan.utils.RemoteConfig;
import com.example.fan.utils.SeriaListAdapter;
import com.example.fan.utils.SharedPref;
import com.example.fan.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements Serializable {

    String token;
    private ListView lv;
    private ProgressBar pr;
    private SeriaListAdapter seriaListAdapter;
    private SwipeRefreshLayout swiperef;
    private List<Datum> list;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        token = SharedPref.read(SharedPref.TOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()));

        mInterstitialAd.setAdUnitId(getString(R.string.ad_screen));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        String title = "Новинки";
        toolbar.setTitle(title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        lv = v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = v.findViewById(R.id.progressBarFrag);
        pr.setVisibility(View.VISIBLE);
        swiperef = v.findViewById(R.id.swipeRefreshLayoutFrag);


        list = new ArrayList<>();
        Log.d("domain main", "domain: " + RemoteConfig.read(RemoteConfig.DOMAIN));
        if (Utils.isNetworkOnline(getContext())) {

            if (getArguments() != null) {
                if (getArguments().getBoolean("PROFILE")) {
                    toolbar.setTitle("Лента");
                    fillProfile(0, token);
                }
                if (getArguments().getBoolean("VIEWED")) {
                    toolbar.setTitle("Следующие серии");
                    fillViewed(token);
                }
            } else
                fillNew(0);
        } else {
            Utils.alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
        }

        Log.d("retrofit", getClass().getSimpleName() + " : " + list.size());


        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position < lv.getCount() - 1) {

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
                        intent.putExtra("Title", list.get(position).getSerial().getName());
                        intent.putExtra("SubTitle", list.get(position).getEpisode().getName());
                        intent.putExtra("URL", list.get(position).getEpisode().getUrl());
                        startActivity(intent);
                    }
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
                            intent.putExtra("Title", list.get(position).getSerial().getName());
                            intent.putExtra("SubTitle", list.get(position).getEpisode().getName());
                            intent.putExtra("URL", list.get(position).getEpisode().getUrl());
                            startActivity(intent);
                            // Load the next interstitial.
                            mInterstitialAd.loadAd(new AdRequest.Builder()
                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                    .addTestDevice("0AE50CA39585DAB4D218A0C9516422A1").build());


                        }
                    });
                }
            }
        });


        swiperef.setColorSchemeColors(Color.RED, Color.YELLOW);
        swiperef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkOnline(getContext())) {
                    list.clear();
                    Log.d("refresh", "refresh");
                    if (getArguments() != null) {
                        if (getArguments().getBoolean("PROFILE"))
                            fillProfile(0, token);

                        if (getArguments().getBoolean("VIEWED"))
                            fillViewed(token);
                    } else
                        fillNew(0);
                } else {
                    Utils.alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
                    swiperef.setRefreshing(false);
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
                        && view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (Utils.isNetworkOnline(getContext())) {

                        if (getArguments() != null) {
                            if (getArguments().getBoolean("PROFILE"))
                                fillProfile(view.getCount(), token);

                            if (getArguments().getBoolean("VIEWED"))
                                viewpr.setVisibility(View.INVISIBLE);
                        } else
                            fillNew(view.getCount());
                    } else {
                        Utils.alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
                    }
//                    if (list.size() % seriasGetter.items != 0)
                    viewpr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

        return v;
    }

    private void fillViewed(String token) {

        NetworkService.getInstance()
                .getSerials()
                .getViewed(token).enqueue(new Callback<List<Viewed>>() {
            @Override
            public void onResponse(@NonNull Call<List<Viewed>> call, @NonNull Response<List<Viewed>> response) {
                List<Viewed> post = response.body();
                Log.d("retrofit", String.valueOf(response.code()));

                assert post != null;
                int code;
                if ((code = response.code()) == 200) {
                    List<Datum> datumList = new ArrayList<>();

                    for (Viewed viewed : post) {
                        String topic = Utils.translit(viewed.getNext() != null ? viewed.getNext().getSerial().getName() :
                                viewed.getCurrent().getSerial().getName());
                        if (!SharedPref.containsSubscribe(topic)) {
                            Log.d("retrofit", "subscribe to: " + topic);
                            SharedPref.addSubscribes(topic);
                            FirebaseMessaging.getInstance().subscribeToTopic(topic);
                        }
                        if (viewed.getNext() != null)
                            datumList.add(viewed.getNext());
                    }
                    fill(datumList);

                } else {
                    alert(code);
                }
            }

            @Override
            public void onFailure(Call<List<Viewed>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fillProfile(int offset, String token) {
        NetworkService.getInstance()
                .getSerials()
                .getProfile(offset, token).enqueue(new Callback<FANAPI>() {
            @Override
            public void onResponse(@NonNull Call<FANAPI> call, @NonNull Response<FANAPI> response) {
                FANAPI post = response.body();
                Log.d("retrofit", String.valueOf(response.code()));
                assert post != null;
                int code;
                if ((code = response.code()) == 200) {
                    fill(post.getDatumList());
                } else {
                    alert(code);
                }
            }

            @Override
            public void onFailure(Call<FANAPI> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fillNew(int offset) {
        NetworkService.getInstance()
                .getSerials()
                .getSerials(offset).enqueue(new Callback<FANAPI>() {
            @Override
            public void onResponse(@NonNull Call<FANAPI> call, @NonNull Response<FANAPI> response) {
                FANAPI post = response.body();
                Log.d("retrofit", String.valueOf(response.code()));
                assert post != null;
                int code;
                if ((code = response.code()) == 200) {
                    fill(post.getDatumList());
                } else {
                    alert(code);
                }
            }

            @Override
            public void onFailure(Call<FANAPI> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void fill(List<Datum> data) {
        list.addAll(data);

        if (seriaListAdapter == null) {
            seriaListAdapter = new SeriaListAdapter(list, getContext());
            lv.setAdapter(seriaListAdapter);
        } else
            seriaListAdapter.notifyDataSetChanged();

        if (lv.getAdapter() == null)
            lv.setAdapter(seriaListAdapter);

        Log.d("retrofit", "size: " + list.size() + " : " + lv.getAdapter());
        swiperef.setRefreshing(false);
        pr.setVisibility(View.GONE);
    }

    void alert(int code) {
        String message = "код ошибки: " + code;
        switch (code) {
            case 401:
                message = "проблемы с авторизацией попробуйте позже";
                break;
            case 404:
                message = "Возможно изменился домен, попробуйте позже";
                break;
        }
        Utils.alarm("Что-то пошло не так!", message);
    }
}