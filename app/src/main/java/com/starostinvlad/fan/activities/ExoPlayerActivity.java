package com.starostinvlad.fan.activities;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.starostinvlad.fan.BuildConfig;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SeriaAdapter;
import com.starostinvlad.fan.api.SeriaJsonClass;
import com.starostinvlad.fan.utils.CurrentSeriaInfo;
import com.starostinvlad.fan.utils.Seria;
import com.starostinvlad.fan.utils.SharedPref;
import com.starostinvlad.fan.utils.Utils;

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

import static com.starostinvlad.fan.utils.Utils.COOKIE;
import static com.starostinvlad.fan.utils.Utils.DOMAIN;
import static com.starostinvlad.fan.utils.Utils.INTERTESTIAL_AD;
import static com.starostinvlad.fan.utils.Utils.IS_REVIEW;

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
    private String TAG = "ExoPlayerActivity";
    private int oldWidth;
    private int oldHeight;
    private FrameLayout layout;
    private ListView series_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_video);

        if (INTERTESTIAL_AD.isLoaded()) {
            INTERTESTIAL_AD.show();
            INTERTESTIAL_AD.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    INTERTESTIAL_AD.loadAd(new AdRequest.Builder()
                            .build());
                    super.onAdClosed();
                }
            });

        } else {
            INTERTESTIAL_AD.loadAd(new AdRequest.Builder()
                    .build());
        }


        series_list = findViewById(R.id.series_in_sezon);


        layout = findViewById(R.id.frame_container);
        playerView = findViewById(R.id.exoplayer_view);
        playerView.setControllerVisibilityListener(visibility -> {
            if (visibility == View.VISIBLE) {
                Show();
            } else {
                Hide();
            }
        });
        playerView.setShowBuffering(true);

//        Log.d("currentSeria", "started!");

//        description = findViewById(R.id.seria_description);
        View v = getLayoutInflater().inflate(R.layout.seria_header, null);

        description = v.findViewById(R.id.description_seria);

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


        fullscreen_btn.setOnClickListener(v1 -> fullscreen(fullscreen, false));


        Button rewind = findViewById(R.id.rewind);
        Button forward = findViewById(R.id.forward);

        rewind.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(ExoPlayerActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (player != null) {
                        long next_position = player.getCurrentPosition() - 10000;
                        if (next_position > 0)
                            player.seekTo(next_position);
                        Animation animation = AnimationUtils.loadAnimation(ExoPlayerActivity.this, R.anim.alpha_anim);
                        rewind.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                rewind.setText("-10сек.");
                                rewind.setBackgroundResource(R.drawable.rewind_background);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                rewind.setText("");
                                rewind.setBackgroundResource(R.drawable.rewind_button);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                    Log.d("TEST", "Double tap rewind");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        forward.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(ExoPlayerActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (player != null) {
                        long next_position = player.getCurrentPosition() + 10000;
                        if (next_position < player.getDuration())
                            player.seekTo(next_position);
                        Animation animation = AnimationUtils.loadAnimation(ExoPlayerActivity.this, R.anim.alpha_anim);
                        forward.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                forward.setText("+10сек.");
                                forward.setBackgroundResource(R.drawable.forward_background);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                forward.setText("");
                                forward.setBackgroundResource(R.drawable.forward_button);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                    Log.d("TEST", "Double tap forward");
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
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
        Utils.subscribe(id, 1);
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
        Utils.subscribe(id, 0);
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

    @SuppressLint("SourceLockedOrientationActivity")
    private void fullscreen(boolean b, boolean src) {
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        if (!b) {
            fullscreen_btn.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);
            if (params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                oldHeight = params.height;
                oldWidth = params.width;
            }
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (!src)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            fullscreen_btn.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
            if (!src)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            params.height = oldHeight;
            params.width = oldWidth;
        }
        fullscreen = !b;
        layout.setLayoutParams(params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullscreen(false, true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullscreen(true, true);
        }
    }

    void fillSpinner(final ArrayList<CurrentSeriaInfo> currentSerias, String descript) {

//        description.setText(descript);
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
        if (StringUtils.isNotEmpty(uri)) {
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
            if (id == R.id.share_btn) {
                // Create the text message with a string
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String targetUrl = url;
                if (!url.contains("http://"))
                    targetUrl = DOMAIN + url;
                String textMessage = "Смотри сериал " + title + " " + subTitle + " по ссылке: " + targetUrl;
                sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
                sendIntent.setType("text/plain");
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(sendIntent);
                }
                return true;
            }
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

    @Override
    public void onUserLeaveHint() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Hide();
            lastPosition = player.getCurrentPosition();
            PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                    .setAspectRatio(new Rational(16, 9))
                    .build();
            enterPictureInPictureMode(pictureInPictureParams);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if(!isInPictureInPictureMode) {
            player.seekTo(lastPosition);
            Show();
        }
        else{
            Hide();
        }
    }

    class SeriaData extends AsyncTask<String, Void, ArrayList<CurrentSeriaInfo>> {

        String toolbarTit = "";
        String descriptionText = "";
        ArrayList<Seria> seriaList = new ArrayList<>();
        private Map<String, String> cookie;
        private String previusSeria, nextSeria;
        private boolean limited = false;

        @Override
        protected ArrayList<CurrentSeriaInfo> doInBackground(String... serias) {
            Document doc;
//            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Mobile Safari/537.36";
            if (serias == null)
                return null;
            ArrayList<CurrentSeriaInfo> currentSeriaInfo = new ArrayList<>();
            String currentPage = serias[0];
            if (IS_REVIEW.equals(BuildConfig.VERSION_NAME)) {
                limited = true;
                return null;
            }
            if (!currentPage.contains("fanserials")) {
//                String domain = null;
//                domain = RemoteConfig.read(RemoteConfig.DOMAIN);
                currentPage = DOMAIN + currentPage;
            }

            //Log.d("tmp", "current: " + s2);
            try {
                Connection.Response res = Jsoup.connect(currentPage)
//                            .userAgent(agent)
                        .cookies(COOKIE)
//                            .header("host", DOMAIN)
//                            .header("Upgrade-Insecure-Requests"," 1")
                        .timeout(10000)
                        .followRedirects(true)
                        .method(Connection.Method.GET)
                        .execute();
                if (res.statusCode() == 200) {
//                    doc = Jsoup.parse(Utils.GiveDocFromUrl(s2));//Jsoup.connect(s2).userAgent(agent).timeout(10000).followRedirects(true).get();
//                    Connection.Response res = Jsoup.connect("https://fanser.000webhostapp.com/anti_block.php?url="
//                            +currentPage)
                    doc = res.parse();
                    cookie = res.cookies();
//                    Log.d("tmp", res.cookies().toString());

                    Utils.saveFile(doc.html(), ExoPlayerActivity.this);

//                    String domain = RemoteConfig.read(RemoteConfig.DOMAIN);


                    title = doc.select("body > div.wrapper > main > div > div.row > div > div > section > ul > li:nth-child(2) > a > span").text();


                    int numElem = doc.select("body > div.wrapper > main > div > div > div > div > section > ul > li").size() - 1;
                    String sezonHref = doc.select("body > div.wrapper > main > div > div > div > div > section > ul > li:nth-child(" + numElem + ") > a").attr("href");

//                    Log.d(TAG, "href: " + sezonHref);

                    Document sezonHtml = Jsoup.connect(DOMAIN + sezonHref).get();


//                    Log.d(TAG, "html: " + sezonHtml);

                    Elements elementsList = sezonHtml.select("#episode_list > li > div > div");
                    for (Element seria : elementsList) {
                        String desc = seria.select(".serial-bottom div.field-description > a").text();
                        String title = seria.select(".serial-bottom div.field-title > a").text();
                        String href = seria.select(".serial-bottom div.field-title > a").attr("href");
                        String image = seria.select("div.serial-top > div.field-img").attr("style");
                        image = image.substring(image.indexOf("url('") + 5, image.length() - 3);

                        if (!subTitle.equals(desc))
                            seriaList.add(new Seria(title, href, image, desc));

                        Log.d(TAG, "seria: " + desc);
                    }


//                    Document document = Jsoup.connect(DOMAIN + "/api/v1/serials")
//                            .data("query", title).ignoreContentType(true).get();

//                    SearchJsonApi fanserJsonApi = null;
//                    fanserJsonApi = LoganSquare.parse(document.body().html(), SearchJsonApi.class);

//                    id = fanserJsonApi.foundSerialData.get(0).foundSerialId;
//                    String st_id = doc.select(".subscribe-link ul li a").attr("data-id");
                    String st_id = doc.select("ul.subscribe-link li a").attr("data-id");
                    if (st_id != "")
                        id = Integer.parseInt(st_id);
                    else
                        id = 2410;
                    Log.d("ExoPlayerActivity", "id= " + st_id);

                    descriptionText = doc.select(".well div").text();

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
                        if (str.contains("\\/limited\\/")) {
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
            if (url.contains("fanserials") || url.contains("umovies")
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
                Utils.alarm(ExoPlayerActivity.this, "Внимание!", "Данный сериал недосутпен в вашей стране(попробуйте использовать VPN)");
            }

            if (StringUtils.isNotEmpty(toolbarTit)) {
                String toolbarSubTit = toolbarTit.substring(subTitle.length());

                Objects.requireNonNull(getSupportActionBar()).setSubtitle(toolbarSubTit);
                Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            }
            if (!seriaList.isEmpty()) {
                series_list.setAdapter(new SeriaAdapter(seriaList, getApplicationContext()));
                description.setText(descriptionText);
                series_list.addHeaderView(description);
            }
            if (result != null) {
                fillSpinner(result, descriptionText);
            }

            if (StringUtils.isNotEmpty(previusSeria)) {
                prev.setVisibility(View.VISIBLE);
            }
            if (StringUtils.isNotEmpty(nextSeria)) {
                next.setVisibility(View.VISIBLE);
            }
            topic = Utils.translit(title);
//        Log.d("subscribe", "topic:" + topic);
            if (SharedPref.containsSubscribe(topic)) {
                subscribe.setBackgroundColor(getResources().getColor(R.color.Gray));
                subscribe.setText("Отписаться");
            } else {
                subscribe.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                subscribe.setText("Подписаться");
            }

            subscribe.setOnClickListener(v -> {
                v.setEnabled(false);

                Log.d("topic", topic);
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
            });


            next.setOnClickListener(v -> {
                if (StringUtils.isNotEmpty(seriaData.nextSeria)) {
                    openSeria(title, seriaData.nextSeria);
                }
            });
            prev.setOnClickListener(v -> {
                if (StringUtils.isNotEmpty(seriaData.previusSeria)) {
                    openSeria(title, seriaData.previusSeria);
                }
            });

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
//                    annoFrag.fill(true, currentSeriaInfo, descriptionText);
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
