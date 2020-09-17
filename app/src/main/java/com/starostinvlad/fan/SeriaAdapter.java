package com.starostinvlad.fan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.starostinvlad.fan.activities.ExoPlayerActivity;
import com.starostinvlad.fan.utils.Seria;

import java.util.ArrayList;
import java.util.List;

import static com.starostinvlad.fan.utils.Utils.PICASSO;

public class SeriaAdapter extends BaseAdapter {
    List<Seria> seriesList = new ArrayList<>();
    Context context;

    public SeriaAdapter(List<Seria> seriesList, Context context) {
        this.seriesList = seriesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return seriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return seriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        Log.d("Adapter", position+") data: "+datumList.get(position));
        final Seria seria = seriesList.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.ser_list_item, null);

        TextView title = view.findViewById(R.id.seria_title);
        TextView sub_title = view.findViewById(R.id.seria_subtitle);
        ImageView image = view.findViewById(R.id.seria_image);

        title.setText(seria.getName());
        sub_title.setText(seria.getDescription());

        PICASSO.with(context).load(seria.getImage())
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .into(image);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {
                    Intent intent = new Intent(context, ExoPlayerActivity.class);
                    intent.putExtra("Title", seria.getName());
                    intent.putExtra("SubTitle", seria.getDescription());
                    intent.putExtra("URL", seria.getUri());
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }
}
