package com.example.fan.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.example.fan.R;
import com.example.fan.api.SearchJsonApi;
import com.example.fan.api.SeriaJsonClass;
import com.example.fan.utils.CurrentSeriaInfo;
import com.example.fan.utils.RemoteConfig;
import com.example.fan.utils.SharedPref;
import com.example.fan.utils.Utils;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class ExoPlayerActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer player;
    String uri = "";
    SeriaData seriaData;
    TextView description;
    Spinner voicesList;
    String topic = "";
    Button subscribe, next, prev, fullscreen_btn;
    String title = "";
    boolean fullscreen = false;
    private long lastPosition;
    private Toolbar toolbar;
    private LinearLayout description_container;
    private View decorView;
    private int last_id;
    private int id;
    private String subTitle;
    private String url;
    private String seria_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_video);

        playerView = findViewById(R.id.exoplayer_view);
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    Show();
                } else {
                    Hide();
                }
            }
        });
        playerView.setShowBuffering(true);

//        Log.d("currentSeria", "started!");

        description = findViewById(R.id.seria_description);
        voicesList = findViewById(R.id.voicesList);

        subscribe = findViewById(R.id.subscribe_button_exo);
        next = findViewById(R.id.next_btn);
        prev = findViewById(R.id.prev_btn);
        fullscreen_btn = findViewById(R.id.fullscreen_toggle);

        decorView = getWindow().getDecorView();

        title = getIntent().getStringExtra("Title");
        subTitle = getIntent().getStringExtra("SubTitle");
        url = getIntent().getStringExtra("URL");

        toolbar = findViewById(R.id.toolbar_exo);
        if (StringUtils.isNotEmpty(title)) {
            toolbar.setTitle(title);
            toolbar.setSubtitle(subTitle);
        }
        setSupportActionBar(toolbar);

        seriaData = new SeriaData();
        seriaData.execute(url);

        description_container = findViewById(R.id.description_container);


        fullscreen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen) {
                    offFullscreen();
                } else {
                    onFullscreen();
                }
            }
        });


        topic = Utils.translit(title);
//        Log.d("subscribe", "topic:" + topic);
        if (SharedPref.containsSubscribe(topic)) {
            subscribe.setBackgroundColor(getResources().getColor(R.color.Gray));
            subscribe.setText("Отписаться");
        } else {
            subscribe.setBackgroundColor(getResources().getColor(R.color.colorOrange));
            subscribe.setText("Подписаться");
        }

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                Log.d("topic",topic);
                if (Utils.isNetworkOnline(ExoPlayerActivity.this))
                    try {
                        topic = Utils.translit(title);
                        if (!SharedPref.containsSubscribe(topic)) {
                            subscribe();
                        } else {
                            unsubscribe();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isNotEmpty(seriaData.nextSeria)) {
                    openSeria(title, seriaData.nextSeria);
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isNotEmpty(seriaData.previusSeria)) {
                    openSeria(title, seriaData.previusSeria);
                }
            }
        });


    }

    void openSeria(String title, String url) {
        Intent nextSeriaIntent = new Intent(ExoPlayerActivity.this, ExoPlayerActivity.class);
        nextSeriaIntent.putExtra("Title", title);
        nextSeriaIntent.putExtra("URL", url);
        nextSeriaIntent.putExtra("SubTitle", "");
        startActivity(nextSeriaIntent);
    }

    void subscribe() {
        Utils.subscibe(id, 1);
        FirebaseMessaging.getInstance()
                .subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Вы подписались на: " +
                                title;
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        } else {
                            subscribe.setText("Отписаться");
                            subscribe.setBackgroundColor(getResources().getColor(R.color.Gray));
                            SharedPref.addSubscribes(topic);
                        }
                        Toast.makeText(ExoPlayerActivity.this, msg,
                                Toast.LENGTH_SHORT).show();

                        subscribe.setEnabled(true);
                    }
                });
    }

    void unsubscribe() {
        Utils.subscibe(id, 0);
        FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Подписка на " +
                                title + " отменена";
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        } else {
                            subscribe.setText("Подписаться");
                            subscribe.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                            SharedPref.removeSubscribe(topic);
//                                        sPref.edit().putBoolean(topic, false).apply();
                        }
                        Toast.makeText(ExoPlayerActivity.this, msg,
                                Toast.LENGTH_SHORT).show();

                        subscribe.setEnabled(true);
                    }
                });
    }

    void onFullscreen() {
        fullscreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullscreen_btn.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);

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
        description_container.setVisibility(View.GONE);
    }

    void offFullscreen() {
        fullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);

        fullscreen_btn.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
        description_container.setVisibility(View.VISIBLE);
        decorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onFullscreen();
            //Log.d(or, "land");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            offFullscreen();
        }
        //Log.d(or, "change config");
    }

    void fillSpinner(final ArrayList<CurrentSeriaInfo> currentSerias, String descript) {

        description.setText(descript);
        ArrayList<String> titleList = new ArrayList<>();
        for (CurrentSeriaInfo item : currentSerias)
            titleList.add(item.Title);
        voicesList.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, titleList));

        //pr.setVisibility(View.INVISIBLE);

        if (!currentSerias.isEmpty()) {
            voicesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {
                    startvideo(currentSerias.get(selectedItemPosition).Url);
//                    Log.d("currentSeria", "url:" + uri);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    description.setText("nothing");
                }
            });

        }
    }

    void startvideo(String uri, long progress) {
        startvideo(uri);
        player.seekTo(progress);
    }

    void startvideo(String uri) {
        this.uri = uri;
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ExoPlayerActivity.this,
                Util.getUserAgent(ExoPlayerActivity.this, "exo-demo"));
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(uri));
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private void Hide() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
        } else {

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void Show() {
        decorView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
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
            tmp = uri.substring(0, uri.indexOf("hls/") + 4);
            uri = tmp + "360/index.m3u8";
            startvideo(uri, player.getCurrentPosition());
            last_id = id;
            return true;
        }
        if (id == R.id.medium && last_id != id) {
            String tmp = "";
            tmp = uri.substring(0, uri.indexOf("hls/") + 4);
            uri = tmp + "480/index.m3u8";
            startvideo(uri, player.getCurrentPosition());
            last_id = id;
            return true;
        }
        if (id == R.id.high && last_id != id) {
            String tmp = "";
            tmp = uri.substring(0, uri.indexOf("hls/") + 4);
            uri = tmp + "720/index.m3u8";
            startvideo(uri, player.getCurrentPosition());
            last_id = id;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(player);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!uri.isEmpty())
            startvideo(uri);

        if (player != null) {
            player.seekTo(lastPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (player != null)
            lastPosition = player.getCurrentPosition();
        if (StringUtils.isNotEmpty(seria_id) && lastPosition > ((player != null ? player.getDuration() : 0) / 2))
            Utils.putToViewed(seria_id, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.setPlayer(null);
        player.release();
        player = null;
    }

    class SeriaData extends AsyncTask<String, Void, ArrayList<CurrentSeriaInfo>> {

        String toolbarTit = "";
        String description = "";
        private Map<String, String> cookie;
        private String previusSeria, nextSeria;
        private boolean limited = false;

        @Override
        protected ArrayList<CurrentSeriaInfo> doInBackground(String... serias) {
            Document doc;
            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Mobile Safari/537.36";
            if (serias == null)
                return null;
            ArrayList<CurrentSeriaInfo> currentSeriaInfo = new ArrayList<>();
            String currentPage = serias[0];
            if (!currentPage.contains("fanserials")) {
                String domain = null;
                domain = RemoteConfig.read(RemoteConfig.DOMAIN);
                currentPage = domain + currentPage;
            }
            //Log.d("tmp", "current: " + s2);
            try {

                if (Utils.CheckResponceCode(currentPage)) {
//                    doc = Jsoup.parse(Utils.GiveDocFromUrl(s2));//Jsoup.connect(s2).userAgent(agent).timeout(10000).followRedirects(true).get();
                    Connection.Response res = Jsoup.connect(currentPage).userAgent(agent).timeout(10000).followRedirects(true).method(Connection.Method.GET).execute();
                    doc = res.parse();
                    cookie = res.cookies();
//                    Log.d("tmp", res.cookies().toString());

                    Utils.saveFile(doc.html(), ExoPlayerActivity.this);

                    String domain = RemoteConfig.read(RemoteConfig.DOMAIN);

                    Document document = Jsoup.connect(domain + "/api/v1/serials")
                            .data("query", title).ignoreContentType(true).get();

                    SearchJsonApi fanserJsonApi = null;
                    fanserJsonApi = LoganSquare.parse(document.body().html(), SearchJsonApi.class);

                    id = fanserJsonApi.foundSerialData.get(0).foundSerialId;

                    description = doc.select(".well div").text();

                    seria_id = (doc.select("#complain").attr("data-id"));


                    previusSeria = (doc.select("a.arrow.prev").attr("href"));
                    nextSeria = (doc.select("a.arrow.next").attr("href"));
//                    Log.d("tmp", "prev=" + previusSeria);
//                    Log.d("tmp", "next=" + nextSeria);

                    if (StringUtils.isEmpty(subTitle))
                        toolbarTit = doc.select("h1.page-title").text();

                    Elements iframe = doc.select("#players.player-component script");//doc.select("iframe");
                    for (Element ss : iframe) {
                        String str = ss.toString();
//                        Log.d("tmp", "json= " + str);
                        if (str.contains("/limited/")) {
                            limited = true;
                            return null;
                        }
//                        if(str.contains("/limited/")){
//
//                        }
                        str = "{\"uris\":[" + str.substring(0, str.indexOf("]';</script>")).substring(str.indexOf(" = '[") + 5).replace("\\/", "/") + "]}";
                        SeriaJsonClass seriaJsonClass = LoganSquare.parse(str, SeriaJsonClass.class);
//                        Log.d("json ", "player: " + seriaJsonClass.uris.get(0).title);

                        for (int i = 0; i < seriaJsonClass.uris.size(); i++) {

                            String player = seriaJsonClass.uris.get(i).player;
//                            Log.d("tmp", "frame=" + player);
                            String title = seriaJsonClass.uris.get(i).title;
                            String hls;
                            if ((hls = getSeria(seriaJsonClass.uris.get(i).player, currentPage)) != null)
                                currentSeriaInfo.add(
                                        new CurrentSeriaInfo(
                                                title,
                                                player,
                                                hls
                                        ));

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return currentSeriaInfo;
        }


        String getSeria(String url, String referer) throws IOException, JSONException {
            Document doc;
            String hls = "";
            if (url.contains("limited")) {
                return null;
            } else if (url.contains("fanserials") || url.contains("umovies")
                    || url.contains("seplay") || url.contains("player")
                    || url.contains("toplay")) {

//                Log.d("tmp", "referer: " + referer + " player url: " + url);
                Connection.Response sub_res = Jsoup.connect(url).cookies(cookie)
                        .method(Connection.Method.GET).referrer(referer).execute();
//                                if (Utils.CheckResponceCode(episodeName)) {
                if (sub_res.statusCode() == 200) {
//                    Log.d("con", "serialHref= " + url);
//                                    doc = Jsoup.parse(Utils.GiveDocFromUrl(episodeName));
                    doc = sub_res.parse();
//                    Log.d("tmp", sub_res.cookies().toString());

                    String tmp = doc.getElementsByAttribute("data-config").attr("data-config");
//                    Log.d("tmp", tmp);
                    JSONObject jsonDATA = new JSONObject(tmp);

//                    Log.d("tmp", "js= " + jsonDATA.get("hls"));
                    hls = jsonDATA.get("hls").toString();
                }

            } else {
                return null;
            }
            return hls;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
            //videoFragment.pr.setVisibility(View.VISIBLE);
            //annoFrag.prVisible(true);
        }

        @Override
        protected void onPostExecute(ArrayList<CurrentSeriaInfo> result) {
            super.onPostExecute(result);

            if (limited) {
                Utils.alarm("Внимание!", "Данный сериал недосутпен в вашей стране(попробуйте использовать VPN)");
                finish();
            }

            if (StringUtils.isNotEmpty(toolbarTit)) {
                String toolbarSubTit = toolbarTit.substring(subTitle.length());

                Objects.requireNonNull(getSupportActionBar()).setSubtitle(toolbarSubTit);
                Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            }
            if (result != null) {
                fillSpinner(result, description);
            }

            if (StringUtils.isNotEmpty(previusSeria)) {
                prev.setVisibility(View.VISIBLE);
            }
            if (StringUtils.isNotEmpty(nextSeria)) {
                next.setVisibility(View.VISIBLE);
            }

//            videoFragment.pr.setVisibility(View.INVISIBLE);
//            videoFragment.btn.setVisibility(View.VISIBLE);
//            if (!nextSeria.isEmpty()) {
//                videoFragment.Next.setVisibility(View.VISIBLE);
//                Log.d("tmp", "next !null");
//            }
//            if (!previusSeria.isEmpty()) {
//                videoFragment.Prev.setVisibility(View.VISIBLE);
//                Log.d("tmp", "prev !null");
//            }
//            if (!toolbarTit.isEmpty()) toolbar.setTitle(toolbarTit);
////            setSupportActionBar(toolbar);
//            if (currentSeriaInfo != null)
//                if (!currentSeriaInfo.isEmpty())
//                    annoFrag.fill(true, currentSeriaInfo, description);
//                else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
//                    builder.setTitle("Важное сообщение!")
//                            .setMessage("Что-то пошло не так!")
//                            .setCancelable(false)
//                            .setNegativeButton("ОК",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            finish();
//                                            dialog.cancel();
//                                        }
//                                    });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
        }


    }
}
