package com.example.fan.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fan.R;
import com.example.fan.api.retro.NetworkService;
import com.example.fan.api.retro.SearchResult;
import com.example.fan.utils.SearchListAdapter;
import com.example.fan.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    protected ArrayList<HashMap<String, Object>> data;
    String queryString = "";
    TextView message;
    private ListView lv;
    private SearchListAdapter searchListAdapter;
    private List<SearchResult> list;

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();

    }

    public void search(String query) {
        if (Utils.isNetworkOnline(getContext())) {
            if (query.length() > 3) {
                list.clear();
//                Log.d("refresh", "refresh");
                fillList(query);
            } else {
                lv.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText("введите не менее 3х символов");
            }
        } else {
            Utils.alarm(getContext(),"Отсутствует доступ к интернету!", "Для работы приложения необходим доступ к сети интернет.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);

        lv = v.findViewById(R.id.search_serial_list);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        message = v.findViewById(R.id.search_message);

        message.setText("введите не менее 3х символов");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putString("Href", list.get(position).getUrl());
                args.putString("Title", list.get(position).getName());
                FragmentOfSerial bFragment = new FragmentOfSerial();
                bFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.main_act_id, bFragment).commit();
                //Log.d("MainFragment", "size= " + Series.size() + "/" + (lv.getCount() - 1) + " pos= " + position + " " + Series.get(position).getName() + " " + Series.get(position).getDescription());

            }
        });

        return v;
    }

    private void fillList(final String query) {
        NetworkService.getInstance()
                .getSerials()
                .getSearch(query).enqueue(new Callback<List<SearchResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchResult>> call, @NonNull Response<List<SearchResult>> response) {
                List<SearchResult> post = response.body();
//                Log.d("retrofit search", String.valueOf(response.code()));
                assert post != null;
                if (post.size() == 0) {
                    lv.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText("По вашему запросу\nничего не найдено");
                } else {
                    message.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);

                }
                list.addAll(post);

                if (searchListAdapter == null) {
                    searchListAdapter = new SearchListAdapter(list, getContext());
                    lv.setAdapter(searchListAdapter);
                } else
                    searchListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<SearchResult>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
