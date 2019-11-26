package com.example.fan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Video extends AppCompatActivity
        implements Fragment_Interface {

    public static final String SaveTime = "SaveTime";
    public static final String CurrentTime = "CurrentTime";
    public static String previusSeria = "", nextSeria = "";
    static String curUri;
    final String or = "orient";
    public String s2;
    protected String uri;
    ProgressBar pr;
    ArrayList<String> l, names, vidUri, sound_param;
    DescriptionFragment annoFrag;
    VideoFragment videoFragment;
    Button play_pause;
    Toolbar toolbar;
    Seria currentSeria;
    LinearLayout.LayoutParams vidParam, annoParam;
    FrameLayout vidLayout, annoLayout;
    boolean AUTO_HIDE = true;
    int currentPos;
    int last_id = -1;
    int i = 0;
    String description = "";
    String finalySusbtitleRusSource = "", finalySusbtitleEngSource = "";
    String toolbarTit = "";
    TextView duration;
    TextView current_time;
    ImageView top_gradient, bottom_gradient;
    private View decorView;
    private SeekBar seek_video;
    private SharedPreferences mSettings;
    private Button Nextbut, Prevbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.app_bar_video);
        Intent in = getIntent();

        currentSeria = (Seria) in.getSerializableExtra("Seria");
        s2 = currentSeria.getUri();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentSeria.getName());
        toolbar.setSubtitle(currentSeria.getDescription());
        setSupportActionBar(toolbar);

        mSettings = getSharedPreferences(SaveTime, Context.MODE_PRIVATE);

        decorView = getWindow().getDecorView();

        videoFragment = new VideoFragment();
        getFragmentManager().beginTransaction().replace(R.id.vidFrame, videoFragment).commit();

        annoFrag = new DescriptionFragment();
        getFragmentManager().beginTransaction().replace(R.id.annoFrame, annoFrag).commit();

        seek_video = (SeekBar) findViewById(R.id.seekBar);

        play_pause = (Button) findViewById(R.id.buttonPlay);

        top_gradient = (ImageView) findViewById(R.id.top_gradient);
        bottom_gradient = (ImageView) findViewById(R.id.bottom_gradient);

        Nextbut = (Button) findViewById(R.id.button3);
        Prevbut = (Button) findViewById(R.id.button2);

        duration = (TextView) findViewById(R.id.duration);
        current_time = (TextView) findViewById(R.id.current_time);

        pr = (ProgressBar) findViewById(R.id.progressBar2);

        vidLayout = (FrameLayout) findViewById(R.id.vidFrame);
        vidLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (AUTO_HIDE) {
                    Hide();
                    AUTO_HIDE = false;
                } else {
                    Show();
                    AUTO_HIDE = true;
                }
//                play_pause.setVisibility(View.GONE);
                return false;
            }
        });
        annoLayout = (FrameLayout) findViewById(R.id.annoFrame);
        vidParam = (LinearLayout.LayoutParams) vidLayout.getLayoutParams();
        annoParam = (LinearLayout.LayoutParams) annoLayout.getLayoutParams();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            vidParam.weight = 0.0f;
            annoParam.weight = 1.0f;
            //Log.d(or, "land");
        }


        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt("current position");
            curUri = savedInstanceState.getString("current uri");
            //Log.d("cururi", "cururi: " + curUri);
            vidUri = savedInstanceState.getStringArrayList("uri's");
            names = savedInstanceState.getStringArrayList("names");
            l = savedInstanceState.getStringArrayList("l's");
            VideoFragment.uri = curUri;

            fillFrag(true, l, vidUri, description);

        } else if (internet()) {
            getHref gf = new getHref();
            gf.execute();
        }
        //Log.d(or, "end create");
        i = 0;
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            vidParam.weight = 0.0f;
            annoParam.weight = 1.0f;
            //Log.d(or, "land");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (annoFrag == null)
                //Log.d(or, "port");
            vidParam.weight = 0.6f;
            annoParam.weight = 0.4f;
            decorView.setVisibility(View.VISIBLE);
        }
        //Log.d(or, "change config");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState.putInt("current position",video.getCurrentPosition() );
        savedInstanceState.putString("current uri", curUri);
        savedInstanceState.putStringArrayList("uri's", vidUri);
        savedInstanceState.putStringArrayList("l's", l);
        savedInstanceState.putStringArrayList("names", names);

        super.onSaveInstanceState(savedInstanceState);
        //Log.d("sa", "saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            curUri = savedInstanceState.getString("current uri");
            vidUri = savedInstanceState.getStringArrayList("uri's");
            l = savedInstanceState.getStringArrayList("l's");
            names = savedInstanceState.getStringArrayList("names");

            //Log.d("sa", "restored");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        //Log.d("sa", "Ativity pause");
        SharedPreferences.Editor ed = mSettings.edit();
        ed.putInt(SaveTime, VideoFragment.currentPos);
        ed.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {

        VideoFragment.currentPos = mSettings.getInt(SaveTime, 0);

        //Log.d("sa", "Ativity resume/curPos= " + mSettings.getInt(SaveTime, 0));
        super.onResume();
    }

    public boolean internet() {
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
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void Hide() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            vidParam.weight = 0.0f;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            annoParam.weight = 1.0f;
        } else {

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        videoFragment.durationTextView.setVisibility(View.GONE);
        videoFragment.current_time.setVisibility(View.GONE);

        videoFragment.top_gradient.setVisibility(View.GONE);
        videoFragment.bottom_gradient.setVisibility(View.GONE);
//        play_pause.setVisibility(View.GONE);
        videoFragment.btn.setVisibility(View.GONE);
//        seek_video.setVisibility(View.GONE);
        videoFragment.video_seek.setVisibility(View.GONE);
//        Prevbut.setVisibility(View.GONE);
        videoFragment.Prev.setVisibility(View.GONE);
//        Nextbut.setVisibility(View.GONE);
        videoFragment.Next.setVisibility(View.GONE);
    }

    private void Show() {
        decorView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        videoFragment.durationTextView.setVisibility(View.VISIBLE);
        videoFragment.current_time.setVisibility(View.VISIBLE);

        videoFragment.top_gradient.setVisibility(View.VISIBLE);
        videoFragment.bottom_gradient.setVisibility(View.VISIBLE);

//        play_pause.setVisibility(View.VISIBLE);
        videoFragment.btn.setVisibility(View.VISIBLE);
//        seek_video.setVisibility(View.VISIBLE);
        videoFragment.video_seek.setVisibility(View.VISIBLE);
        if (nextSeria != "") videoFragment.Next.setVisibility(View.VISIBLE);
        if (previusSeria != "") videoFragment.Prev.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.low && last_id != id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/") + 4);
            curUri = tmp + "360/index.m3u8";
            currentPos = VideoFragment.video.getCurrentPosition();
            VideoFragment.seturi(curUri, currentPos);
            last_id = id;
            return true;
        }
        if (id == R.id.medium && last_id != id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/") + 4);
            curUri = tmp + "480/index.m3u8";
            currentPos = VideoFragment.video.getCurrentPosition();
            VideoFragment.seturi(curUri, currentPos);
            last_id = id;
            return true;
        }
        if (id == R.id.high && last_id != id) {
            String tmp = "";
            tmp = curUri.substring(0, curUri.indexOf("hls/") + 4);
            curUri = tmp + "720/index.m3u8";
            currentPos = VideoFragment.video.getCurrentPosition();
            VideoFragment.seturi(curUri, currentPos);
            last_id = id;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fillFrag(final boolean rest, ArrayList<String> l,
                         final ArrayList<String> vidUri, String descript) {
        annoFrag.fill(rest, l, vidUri, descript);
    }

    Map cookie;

    class getHref extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... parametr) {
            Document doc;
            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Mobile Safari/537.36";
            if (!s2.contains("fanserials")) {
                SharedPreferences sPref;
                String SAVED_TEXT = "saved_text";
                sPref = getSharedPreferences("URL", MODE_PRIVATE);
                String queryUrl = sPref.getString(SAVED_TEXT, "");
                s2 = queryUrl + s2;
            }
            //Log.d("tmp", "current: " + s2);
            try {
                if (quick_help.CheckResponceCode(s2)) {
//                    doc = Jsoup.parse(quick_help.GiveDocFromUrl(s2));//Jsoup.connect(s2).userAgent(agent).timeout(10000).followRedirects(true).get();
                    Connection.Response res = Jsoup.connect(s2).userAgent(agent).timeout(10000).followRedirects(true).method(Connection.Method.GET).execute();
                    doc = res.parse();
                    cookie = res.cookies();
                    Log.d("tmp", res.cookies().toString());
                    //Elements iframe = doc.select("iframe");//doc.select("iframe");
//                    Elements ulli = doc.select("ul.nav.nav-tabs.tabs-voice.mobile-tabs-scroll.show-for-smallImage-only li a");
                    description = doc.select(".well div").text();
                    names = new ArrayList<String>();
                    vidUri = new ArrayList<String>();
                    l = new ArrayList<String>();
                    previusSeria = (doc.select("a.arrow.prev").attr("href"));
                    nextSeria = (doc.select("a.arrow.next").attr("href"));
                    Log.d("tmp", "prev=" + previusSeria);
                    Log.d("tmp", "next=" + nextSeria);
                    Seria seria = (Seria) getIntent().getSerializableExtra("Seria");
                    if (seria.getName() == "")
                        toolbarTit = doc.select("h1.page-title").text();
                    /*
                    for (Element ss : ulli) {
                        if (ss.text() != "") {
                            l.add(ss.text());
                            Log.d("tmp", "sound" + ss.text());
                        }
                    }*/

                    Elements iframe = doc.select("#players.player-component script");//doc.select("iframe");
                    for (Element ss : iframe) {
                        String str = ss.toString();
                        Log.d("tmp", "json= " + str);
                        str = "{\"uris\":[" + str.substring(0, str.indexOf("]';</script>")).substring(str.indexOf(" = '[") + 5).replace("\\/", "/") + "]}";
                        SeriaJsonClass seriaJsonClass = LoganSquare.parse(str, SeriaJsonClass.class);
                        Log.d("json ", "player: " + seriaJsonClass.uris.get(0).title);
//                        JSONObject jsonDATA = new JSONObject(str);
//                        JSONArray jsonUris = jsonDATA.getJSONArray("uris");
                        for (int i = 0; i < seriaJsonClass.uris.size(); i++) {
//                            JSONObject soundURI = jsonUris.getJSONObject(i);
//                            str = soundURI.getString("player");
                            str = seriaJsonClass.uris.get(i).player;
                            Log.d("tmp", "frame=" + str);
                            names.add(str);
//                            l.add(soundURI.getString("name"));
                            l.add(seriaJsonClass.uris.get(i).title);
                        }
                    }

                    for (String name : names) {
                        if (name.contains("limited")) {
                            publishProgress("Недоступно в данной стране, воспользуйтесь VPN");
                            break;
                        } else if (name.contains("fanserials") || name.contains("umovies") || name.contains("seplay") || name.contains("player")) {
                            try {
                                Connection.Response sub_res = Jsoup.connect(name).cookies(cookie)
                                        .method(Connection.Method.GET).referrer(s2).execute();
//                                if (quick_help.CheckResponceCode(episodeName)) {
                                if (sub_res.statusCode() == 200) {
                                    Log.d("con", "serialHref= " + name);
//                                    doc = Jsoup.parse(quick_help.GiveDocFromUrl(episodeName));
                                    doc = sub_res.parse();
                                    Log.d("tmp", sub_res.cookies().toString());

                                    String tmp = doc.getElementsByAttribute("data-config").attr("data-config");
                                    Log.d("tmp", tmp);
                                    JSONObject jsonDATA = new JSONObject(tmp);

                                    Log.d("tmp", "js= " + jsonDATA.get("hls"));

                                    vidUri.add(jsonDATA.get("hls").toString());
                                }
                            } catch (Exception e) {
                                Log.d("tmp", "just not work  " + e);
                                e.printStackTrace();
                                vidUri.add("some.mp4");
                            }
                        } else {
                            Log.d("tmp", "alt player");
                            vidUri.add("some.mp4");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            for (int i = 0; i < vidUri.size(); i++) {
                if (vidUri.get(i).equals("some.mp4")) {
                    vidUri.remove(i);
                    l.remove(i);
                    i--;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
            builder.setTitle("Важное сообщение!")
                    .setMessage(values[0])
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //videoFragment.pr.setVisibility(View.VISIBLE);
            //annoFrag.prVisible(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            videoFragment.pr.setVisibility(View.INVISIBLE);
            videoFragment.btn.setVisibility(View.VISIBLE);
            if (!nextSeria.equals("")) {
                videoFragment.Next.setVisibility(View.VISIBLE);
                Log.d("tmp", "next !null");
            }
            if (!previusSeria.equals("")) {
                videoFragment.Prev.setVisibility(View.VISIBLE);
                Log.d("tmp", "prev !null");
            }
            if (!toolbarTit.equals("")) toolbar.setTitle(toolbarTit);
            setSupportActionBar(toolbar);
            if(vidUri.size()>=1)
                fillFrag(false, l, vidUri, description);
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
                builder.setTitle("Важное сообщение!")
                        .setMessage("Что-то пошло не так!")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }


    }
    class getVideoUri extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            if(strings.length==0)
                return  null;
            if(strings[0].equals(""))
                return null;
            if (strings[0].contains("limited")) {
                publishProgress("Недоступно в данной стране, воспользуйтесь VPN");
                return null;
            } else if (strings[0].contains("seplay")) {
                try {
                    Connection.Response sub_res = Jsoup.connect(strings[0]).cookies(cookie)
                            .method(Connection.Method.GET).referrer(s2).execute();
//                                if (quick_help.CheckResponceCode(episodeName)) {
                    if (sub_res.statusCode() == 200) {
                        Log.d("con", "serialHref= " + strings[0]);
//                                    doc = Jsoup.parse(quick_help.GiveDocFromUrl(episodeName));
                        Document doc = sub_res.parse();
                        Log.d("tmp", sub_res.cookies().toString());

                        String tmp = doc.getElementsByAttribute("data-config").attr("data-config");
                        Log.d("tmp", tmp);
                        JSONObject jsonDATA = new JSONObject(tmp);

                        Log.d("tmp", "js= " + jsonDATA.get("hls"));

                        vidUri.add(jsonDATA.get("hls").toString());
                    }
                } catch (Exception e) {
                    Log.d("tmp", "just not work  " + e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void publishProgress(String s) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
            builder.setTitle("Важное сообщение!")
                    .setMessage(s)
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
}
