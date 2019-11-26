package com.example.fan;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    protected ListView lv;
    protected ProgressBar pr;//, pr3;
    protected MyAdap adap;
    protected ArrayList<HashMap<String, Object>> data;
    protected HashMap<String, Object> map;
    ArrayList<Seria> Series;
    int[] to = {R.id.textV, R.id.imgV, R.id.textView2};
    String[] from = {"Name", "Icon", "Anno"};
    String queryString = "";

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void search(String query) {
        GetSeries gt = new GetSeries();
        gt.execute(query);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        lv = (ListView) v.findViewById(R.id.serFrag);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setVisibility(View.VISIBLE);
        pr = (ProgressBar) v.findViewById(R.id.progressBarFrag);
        pr.setVisibility(View.INVISIBLE);


        GetSeries gt = new GetSeries();
        gt.execute(queryString);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putString("Href", Series.get(position).getUri());
                args.putString("Title", Series.get(position).getName());
                MainFragment bFragment = new MainFragment();
                bFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_act_id, bFragment)
                        .addToBackStack(null).commit();
                //Log.d("MainFragment", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());

            }
        });

        return v;
    }

    void fill() {
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
            adap = new MyAdap(getActivity(), data, R.layout.search_ser_list_item, from, to);
            lv.setAdapter(adap);
        }

    }

    class GetSeries extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if (strings.length > 0) {
                String query = strings[0];
                try {
                    Series = searchSerials(query);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.d("SearchFrag", "parse end");
            }
            return null;
        }


        ArrayList<Seria> searchSerials(String query) {
            ArrayList<Seria> Serials = new ArrayList<>();
            String SAVED_TEXT = "saved_text";
            SharedPreferences sPref = getActivity().getSharedPreferences("URL", 0);
            String queryUrl = sPref.getString(SAVED_TEXT, "");
            try {
                Document doc = Jsoup.connect(queryUrl+"/api/v1/serials").ignoreContentType(true).data("query", query).get();
                //Log.d("SearchFrag", doc.body().html());

                SearchJsonApi fanserJsonApi = null;
                fanserJsonApi = LoganSquare.parse(doc.body().html(), SearchJsonApi.class);
                for (SearchJsonApi.FoundSerials item : fanserJsonApi.foundSerialData) {
                    //Log.d("Name ", item.foundSerialName + " : " + item.foundSerialUrl + " : " + item.serialDescription + " : " + item.foundSerialPoster.smallImage);
                    Seria seria = new Seria(item.foundSerialName, item.foundSerialUrl, item.foundSerialPoster.smallImage, item.serialDescription);
                    Serials.add(seria);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Serials;
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
            super.onPostExecute(result);
            fill();
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
            Picasso.with(getActivity()).load(val).error(R.drawable.noimage_port).placeholder(R.drawable.noimage_port).into(v);
        }
    }
}
