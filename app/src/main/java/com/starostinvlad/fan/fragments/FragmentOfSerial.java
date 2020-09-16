package com.starostinvlad.fan.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.starostinvlad.fan.R;
import com.starostinvlad.fan.activities.ExoPlayerActivity;
import com.starostinvlad.fan.utils.Seria;
import com.starostinvlad.fan.utils.SeriasGetter;
import com.starostinvlad.fan.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import static com.starostinvlad.fan.utils.Utils.PICASSO;

public class FragmentOfSerial extends Fragment {
    private ArrayList<Seria> Series;
    private Toolbar toolbar;
    private String SeriaUrl = "", SeriaName = "";
    private SeriasGetter seriasGetter;
    private ListView lv;
    private ProgressBar pr;
    private InterstitialAd mInterstitialAd;
    private MyAdap mainAdap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if (getArguments() != null) {
            SeriaUrl = getArguments().getString("Href");
            SeriaName = getArguments().getString("Title");
//            Log.d("fragment of serial", SeriaUrl);
        }
        seriasGetter = new SeriasGetter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()));

        mInterstitialAd.setAdUnitId(getString(R.string.ad_screen));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        String title = SeriaName;
        toolbar.setTitle(title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        lv = v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = v.findViewById(R.id.progressBarFrag);
        pr.setVisibility(View.VISIBLE);


        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });


        if (Utils.isNetworkOnline(getContext())) {
            //Log.d("MainFragment", "download series from internet hash: " + this.hashCode());
            if (Series == null) {
                GetSeries gt = new GetSeries("new", mainAdap);
                gt.execute();
            }
//            else if (!Series.isEmpty()) {
//                //Log.d("MainFragment", "must fill list!" + Series.size() + " in: " + this.hashCode());
//                fill(Series);
////            }
//            }
            else {
                Utils.alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.", getContext());
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
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                        //Log.d("ADS", "The interstitial show.");
//                    } else {
//                        Log.d("ADS", "The interstitial wasn't loaded yet.");
                        if (Series != null) {
                            Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
                            intent.putExtra("Title", Series.get(position).getName());
                            intent.putExtra("SubTitle", Series.get(position).getDescription());
                            intent.putExtra("URL", Series.get(position).getUri());
                            startActivity(intent);
                        }
//                    }
//                    mInterstitialAd.setAdListener(new AdListener() {
//                        @Override
//                        public void onAdClosed() {
//                            Intent intent = new Intent(getContext(), ExoPlayerActivity.class);
//                            // Load the next interstitial.
//                            mInterstitialAd.loadAd(new AdRequest.Builder()
//                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                                    .addTestDevice("0AE50CA39585DAB4D218A0C9516422A1").build());
//                            intent.putExtra("Seria", Series.get(position));
//                            MainActivity.newActivity = true;
//                            startActivity(intent);
//                        }
//                    });
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
//                            Toast.makeText(getContext(), "size:" + Series.size(), Toast.LENGTH_SHORT).show();
                            if (Series.size() % 32 == 0) {
                                GetSeries gt = new GetSeries("add", mainAdap);
                                gt.execute();
                            }
                        } else {
                            Utils.alarm("Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.", getContext());
                        }
                        if (Series.size() % 32 != 0)
                            viewpr.setVisibility(View.INVISIBLE);
                    }
                }


                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
        return v;

    }


    class GetSeries extends AsyncTask<String, String, ArrayList<Seria>> {

        MyAdap adap;
        boolean firstly = true;
        String command = "";

        GetSeries(String command, MyAdap adap) {
            this.adap = adap;
            this.command = command;
        }

        @Override
        protected ArrayList<Seria> doInBackground(String... parametr) {
            try {
                ArrayList<Seria> serias = new ArrayList<>();


                seriasGetter.context = getContext();
//                    Log.d("query", "url: " + serias.get(0).uri);
                if (seriasGetter.getSerias() == null)
                    seriasGetter.setSerias(serias);
                switch (command) {
                    case "new":
                        serias = seriasGetter.getSeriesOfSerial(SeriaUrl);
                        break;
                    case "add":
                        serias = seriasGetter.addSeriesOfSerial(SeriaUrl);
                        break;
                }

                return serias;
            } catch (Exception e) {
                publishProgress("Внимание", "Превышено время ожидания ответа");
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (firstly && !command.equals("add"))
                pr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Seria> result) {
            super.onPostExecute(result);
            // adapter.add("");
            pr.setVisibility(View.INVISIBLE);
            if (adap == null && result != null) {
//                Log.d("post", "adap null");
                adap = new MyAdap(result, getContext());
                mainAdap = adap;
                lv.setAdapter(adap);
            }
//            Log.d("post", "changed");
            adap.notifyDataSetChanged();
            Series = result;
            if (getArguments() != null) {
                toolbar.setTitle(getArguments().getString("Title"));
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values != null)
                Utils.alarm(values[0], values[1], getContext());
            super.onProgressUpdate(values);
        }

    }

    class MyAdap extends BaseAdapter {

        ArrayList<Seria> serias;
        private Context context;

        public MyAdap(ArrayList<Seria> serias, Context context) {
            this.serias = serias;
            this.context = context;
        }

        @Override
        public int getCount() {
            return serias.size();
        }

        @Override
        public Object getItem(int position) {
            return serias.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.ser_list_item, parent, false);
            }

            TextView title = view.findViewById(R.id.seria_title);
            TextView sub_title = view.findViewById(R.id.seria_subtitle);
            ImageView image = view.findViewById(R.id.seria_image);

            final Seria data = serias.get(position);

            title.setText(data.getName());
            sub_title.setText(data.getDescription());

            PICASSO.with(context).load(data.getImage()).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(image);
            return view;
        }
    }

}
