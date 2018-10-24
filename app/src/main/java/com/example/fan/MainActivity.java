package com.example.fan;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Seria> Series;
    int[] to = {R.id.textV, R.id.imgV, R.id.textView2};
    String[] from = {"Name", "Icon", "Anno"};

    protected boolean add=false;
    protected int page=2;

    protected ListView lv;
    protected ProgressBar pr,pr3;
    protected MyAdap adap;
    protected SwipeRefreshLayout swiperef;

    protected ArrayList<HashMap<String, Object>> data;
    protected HashMap<String, Object> map;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lv = (ListView) findViewById(R.id.ser);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pr = (ProgressBar) findViewById(R.id.progressBar);
        pr3 = (ProgressBar) findViewById(R.id.progressBar3);
        pr.setVisibility(View.INVISIBLE);
        swiperef=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lv.smoothScrollToPosition(0);
            }
        });

        /*
        if (Build.VERSION.SDK_INT >= 23&&!hasPermissions()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
        */
        if(internet()&&savedInstanceState==null) {
            getHref gt = new getHref();
            gt.execute();
        }else  if(savedInstanceState!=null){
            Series=(ArrayList<Seria>) savedInstanceState.getSerializable("Series");

            fill();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<lv.getCount()-1) {
                    Intent intent = new Intent(MainActivity.this, Video.class);
                    Log.d("tnp", "size= " + Series.size() + "/" + (lv.getCount()-1) + " pos= " + position+" "+Series.get(position).getName()+" "+ Series.get(position).getDescription());


                    intent.putExtra("Seria",Series.get(position));
                    startActivity(intent);
                }
            }
        });


        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewpr = inflater.inflate(R.layout.prog, null);
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
                }if(EndList)viewpr.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        swiperef.setColorSchemeColors(Color.RED,Color.YELLOW);
        swiperef.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(internet()) {
                    getHref gt = new getHref();
                    gt.execute();
                }else swiperef.setRefreshing(false);
            }
        });

    }
    @Override
    protected void onDestroy() {

        Intent NotifyService=new Intent(this,Notification_Service.class);
        startService(NotifyService);
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("Series",Series);

        super.onSaveInstanceState(savedInstanceState);
        Log.d("sa","saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            Series=(ArrayList<Seria>) savedInstanceState.getSerializable("Series");
            Log.d("sa","restored");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
    public boolean internet(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (isNetworkOnline(this)) {
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
    public void alarm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    protected int iter=0;

    void fill() {
        if(iter==0)
        data = new ArrayList<>(Series.size());
        else{
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
        if(iter==0) {
            adap = new MyAdap(this, data, R.layout.ser_list_item, from, to);
            lv.setAdapter(adap);
            iter++;
            swiperef.setRefreshing(false);
        }else
        {
            adap.notifyDataSetChanged();
            swiperef.setRefreshing(false);
        }

    }
    void add(){
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
        add=false;
    }
    private boolean hasPermissions(){
        int rs = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};

        for (String perms : permissions){
            if (!(checkCallingOrSelfPermission(perms) == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startService(new Intent(this,Notification_Service.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",0));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",1));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",2));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,All_serials_activity.class).putExtra("param",3));
        } else if (id == R.id.nav_share) {
            startService(new Intent(this,top_layout.class));
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    boolean EndList=false;

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";

    class getHref extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String s, img,queryUrl,anno = "";

            try {
                if (quick_help.CheckResponceCode("http://fanserials-zerkalo.com")&&!EndList) {
                    doc = Jsoup.parse(quick_help.GiveDocFromUrl("http://fanserials-zerkalo.com"));
                    queryUrl=doc.select("div div.l-container div.c-header__inner a.c-header__link").attr("href").substring(2);

                    sPref = getSharedPreferences("URL",MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(SAVED_TEXT,"http://"+queryUrl );
                    ed.commit();


                    if(getIntent().getStringExtra("Uri")!=null) {
                        sPref = getSharedPreferences("URL",MODE_PRIVATE);
                        queryUrl = sPref.getString(SAVED_TEXT, "");
                        queryUrl += getIntent().getStringExtra("Uri");
                    }
                    else queryUrl="http://"+queryUrl+"/new/";
                    Log.d("check", "queryUrl="+queryUrl);

                    Log.d("check", "code = 200");
                    if (add)
                        doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl+"page/" + page+"/"));//Jsoup.connect("http://fanserials.biz/new/page/"+page).userAgent(agent).timeout(10000).get();
                    else {
                        doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl));// Jsoup.connect("http://fanserials.biz/new/").userAgent(agent).timeout(10000).get();
                    }
                    Log.d("check", "give doc");
                    Elements src = doc.select("li div div.item-serial");//li div div div div.field-img
                    Log.d("check", "src=" + doc.toString());
                    int i = 0;
                    if (!add) {
                        Series=new ArrayList<>();
                    }
                    for (Element ss : src) {
                        String name = ss.select("div.serial-bottom div.field-title a").text();
                        anno=ss.select("div.serial-bottom div.field-description a").text();
                        //Log.d("not_main", "an= "+anno);
                        Log.d("tnp", ss.select("div.serial-top div.field-img a").attr("href"));
                        String uri=ss.select("div.serial-top div.field-img a").attr("href");
                        s = ss.select("div.serial-top div.field-img").attr("style");
                        img = (img = s.substring(0, s.indexOf(");"))).substring(img.indexOf("url(") + 4);
                        Seria seria=new Seria(name,uri,img,anno);
                        Series.add(seria);
                        i++;
                    }
                    Log.d("not_main", "size="+Series.size());
                    if(Series.size()%20!=0)EndList=true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d("check","parse end");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(iter==0&&!add)
            pr.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // adapter.add("");
            pr.setVisibility(View.INVISIBLE);
            if(Series!=null)
            if(add)add();
            else fill();
            if(getIntent().getStringExtra("Uri")!=null) {
                toolbar.setTitle(getIntent().getStringExtra("Name"));
                setSupportActionBar(toolbar);
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
            Picasso.with(getApplicationContext()).load(val).error(R.drawable.no_image).placeholder(R.drawable.no_image).into(v);
        }
    }
}