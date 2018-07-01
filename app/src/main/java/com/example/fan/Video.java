package com.example.fan;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class Video extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Fragment_Interface{

    public String s2;
    ProgressBar pr;
    ArrayList<String> l,names,vidUri;
    anno_frag annoFrag;
    video_fragment videoFragment;
    Button play_pause;
    Toolbar toolbar;


    LinearLayout.LayoutParams vidParam,annoParam;
    FrameLayout vidLayout,annoLayout;
    private View decorView;

    boolean AUTO_HIDE=true;
    private SeekBar seek_video;

    public static final String SaveTime = "SaveTime";
    public static final String CurrentTime = "CurrentTime";
    private SharedPreferences mSettings;
    private Button Nextbut,Prevbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.act_video);
        Intent in=getIntent();
        s2=in.getStringExtra("uri").toString();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(in.getStringExtra("name").toString());
        toolbar.setSubtitle(in.getStringExtra("anno").toString());
        setSupportActionBar(toolbar);

        mSettings = getSharedPreferences(SaveTime, Context.MODE_PRIVATE);

        decorView = getWindow().getDecorView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        videoFragment= (video_fragment) getFragmentManager().findFragmentById(R.id.Fvid);

        annoFrag= (anno_frag) getFragmentManager().findFragmentById(R.id.Avid);

        seek_video=(SeekBar)videoFragment.getView().findViewById(R.id.seekBar);

        play_pause=(Button)videoFragment.getView().findViewById(R.id.button);
        play_pause.setVisibility(View.GONE);

        Nextbut=(Button)videoFragment.getView().findViewById(R.id.button3);
        Prevbut=(Button)videoFragment.getView().findViewById(R.id.button2);
        Prevbut.setVisibility(View.GONE);
        Nextbut.setVisibility(View.GONE);

        pr = (ProgressBar) videoFragment.getView().findViewById(R.id.progressBar2);
        pr.setVisibility(View.INVISIBLE);
        vidLayout=(FrameLayout)findViewById(R.id.vidFrame);
        vidLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AUTO_HIDE) {
                    Hide();
                    AUTO_HIDE=false;
                }else {
                    Show();
                    AUTO_HIDE=true;
                }
                return false;
            }
        });
        annoLayout=(FrameLayout)findViewById(R.id.annoFrame);
        vidParam=(LinearLayout.LayoutParams)vidLayout.getLayoutParams();
        annoParam=(LinearLayout.LayoutParams)annoLayout.getLayoutParams();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            vidParam.weight=0.0f;
            annoParam.weight=1.0f;
            Log.d(or,"land");
        }

        if(savedInstanceState!=null){
            currentPos =  savedInstanceState.getInt("current position");
            curUri=savedInstanceState.getString("current uri");
            Log.d("cururi","cururi: "+curUri);
            vidUri=savedInstanceState.getStringArrayList("uri's");
            names=savedInstanceState.getStringArrayList("names");
            l=savedInstanceState.getStringArrayList("l's");
            video_fragment.uri=curUri;

            fillFrag(true,l,vidUri,description);

        }
        else if(internet()) {
            getHref gf = new getHref();
            gf.execute();
        }
        i=0;
    }
    int currentPos;
    static String curUri;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            vidParam.weight=0.0f;
            annoParam.weight=1.0f;
            Log.d(or,"land");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(annoFrag==null)
            Log.d(or,"port");
            vidParam.weight=0.6f;
            annoParam.weight=0.4f;
        }
        Log.d(or,"change config");
    }

    final String or="orient";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putInt("current position",video.getCurrentPosition() );
        savedInstanceState.putString("current uri",curUri );
            savedInstanceState.putStringArrayList("uri's", vidUri);
            savedInstanceState.putStringArrayList("l's", l);
            savedInstanceState.putStringArrayList("names", names);

        super.onSaveInstanceState(savedInstanceState);
        Log.d("sa","saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            curUri=savedInstanceState.getString("current uri");
            vidUri=savedInstanceState.getStringArrayList("uri's");
            l=savedInstanceState.getStringArrayList("l's");
            names=savedInstanceState.getStringArrayList("names");

            Log.d("sa","restored");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        Log.d("sa","Ativity pause");
        SharedPreferences.Editor ed = mSettings.edit();
        ed.putInt(SaveTime, video_fragment.currentPos);
        ed.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {

        video_fragment.currentPos = mSettings.getInt(SaveTime,0);

        Log.d("sa","Ativity resume/curPos= "+mSettings.getInt(SaveTime,0));
        super.onResume();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
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

    private void Hide(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            vidParam.weight=0.0f;
            annoParam.weight=1.0f;
        }else {

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        play_pause.setVisibility(View.GONE);
        seek_video.setVisibility(View.GONE);
        Prevbut.setVisibility(View.GONE);
        Nextbut.setVisibility(View.GONE);
    }
    private void Show(){
        decorView.setVisibility(View.VISIBLE);
        ActionBar actionBar=getSupportActionBar();
        actionBar.show();
        play_pause.setVisibility(View.VISIBLE);
        seek_video.setVisibility(View.VISIBLE);
        if(nextSeria!="")Nextbut.setVisibility(View.VISIBLE);
        if(previusSeria!="")Prevbut.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.video, menu);
        return true;
    }

    int last_id=-1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.low&&last_id!=id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/")+4);
            curUri=tmp+"360/index.m3u8";
            currentPos=video_fragment.video.getCurrentPosition();
            video_fragment.seturi(curUri,currentPos);
            last_id=id;
            return true;
        }
        if (id == R.id.medium&&last_id!=id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/")+4);
            curUri=tmp+"480/index.m3u8";
            currentPos=video_fragment.video.getCurrentPosition();
            video_fragment.seturi(curUri,currentPos);
            last_id=id;
            return true;
        }
        if (id == R.id.high&&last_id!=id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/")+4);
            curUri=tmp+"720/index.m3u8";
            currentPos=video_fragment.video.getCurrentPosition();
            video_fragment.seturi(curUri,currentPos);
            last_id=id;
            return true;
        }
        if (id == R.id.engSub) {
            if(finalySusbtitleEngSource.contains("vtt")){
                try {
                    video_fragment.setSub(finalySusbtitleEngSource);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    int i=0;
    protected String uri;
    public static String previusSeria="",nextSeria="";
    String description="";
    String finalySusbtitleRusSource = "", finalySusbtitleEngSource = "";

    @Override
    public void fillFrag(final boolean rest, ArrayList<String> l, final ArrayList<String> vidUri,String descript) {
        annoFrag.fill(rest,l,vidUri,descript);
    }
String toolbarTit;
    class getHref extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String agent="Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
            try {
                if(quick_help.CheckResponceCode(s2)){
                doc = Jsoup.parse(quick_help.GiveDocFromUrl(s2));//Jsoup.connect(s2).userAgent(agent).timeout(10000).followRedirects(true).get();
                Elements iframe = doc.select("iframe");//doc.select("iframe");
                Elements ulli = doc.select("ul li a");
                description=doc.select(".well p").text();
                names= new ArrayList<String>();
                vidUri= new ArrayList<String>();
                l= new ArrayList<String>();
                previusSeria=(doc.select("a.arrow.prev").attr("href"));
                nextSeria=(doc.select("a.arrow.next").attr("href"));
                Log.d("tmp","prev="+previusSeria );
                Log.d("tmp","next="+nextSeria );
                if(getIntent().getStringExtra("name").toString()=="")toolbarTit=doc.select("h1.page-title").text();
                for (Element ss : iframe) {
                    names.add(ss.attr("src"));
                }
                for (Element ss : ulli) {
                    if (ss.attr("data-sound") != "")
                        l.add(ss.attr("data-sound").replace('#', ' '));
                }
                for(String name:names){
                    if(name.contains("fanserials")||name.contains("umovies")) {
                            try {
                                if(quick_help.CheckResponceCode(name)){
                                    Log.d("con","href= "+name);
                                    doc = Jsoup.parse(quick_help.GiveDocFromUrl(name));
                                    /*
                                    Elements vsrc = doc.getElementsByAttributeValue("type", "text/javascript");
                                    String s = vsrc.toString();
                                    if(s.contains("subtitles")&&s.contains(".vtt")) {
                                        Log.d("subs", "sub found");
                                        String subtitleSourceRus = vsrc.toString();
                                        String subtitleSourceEng = vsrc.toString();
                                        if (s.contains("Russian")){
                                            finalySusbtitleRusSource = subtitleSourceRus.substring(subtitleSourceRus.indexOf("ru"), subtitleSourceRus.indexOf(".vtt") + 4);
                                            finalySusbtitleRusSource = finalySusbtitleRusSource.substring(finalySusbtitleRusSource.lastIndexOf("src:  \"") + 7);
                                            Log.d("subs", "sub(ru)=" + finalySusbtitleRusSource);
                                        }
                                        if(s.contains("English")) {
                                            finalySusbtitleEngSource = subtitleSourceEng.substring(subtitleSourceEng.indexOf("English"), subtitleSourceEng.length());
                                            finalySusbtitleEngSource = finalySusbtitleEngSource.substring(0, finalySusbtitleEngSource.indexOf(".vtt") + 4);
                                            finalySusbtitleEngSource = finalySusbtitleEngSource.substring(finalySusbtitleEngSource.indexOf("src:  \"") + 7);
                                            Log.d("subs", "sub(en)=" + finalySusbtitleEngSource);
                                        }
                                    }
                                    String tmp = "";
                                    tmp = s.substring(0, s.indexOf("index.m3u8") + 10);
                                    tmp = tmp.substring(tmp.lastIndexOf("src:  \"") + 7);
                                    */
                                    String tmp=doc.getElementsByAttribute("data-hls").attr("data-hls");
                                    Log.d("tmp", tmp);
                                    vidUri.add(tmp);
                                }
                            }catch (Exception e){
                                Log.d("tmp","just not work  "+e);
                                e.printStackTrace();
                                vidUri.add("some.mp4");
                            }
                        }
                    else {
                        Log.d("tmp","alt palyer");
                        vidUri.add("some.mp4");
                    }
                }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i=0;i<vidUri.size();i++){
                if(vidUri.get(i).equals("some.mp4")){
                    vidUri.remove(i);
                    l.remove(i);
                    i--;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pr.setVisibility(View.VISIBLE);
            //annoFrag.prVisible(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pr.setVisibility(View.INVISIBLE);
            play_pause.setVisibility(View.VISIBLE);
            if(nextSeria!=""){
                Nextbut.setVisibility(View.VISIBLE);
                Log.d("tmp","next !null");
            }
            if(previusSeria!=""){
                Prevbut.setVisibility(View.VISIBLE);
                Log.d("tmp","prev !null");
            }
            if(toolbarTit!=null)toolbar.setTitle(toolbarTit);
            fillFrag(false,l,vidUri,description);
        }


    }
}
