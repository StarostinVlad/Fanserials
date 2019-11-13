//package com.example.fan;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.webkit.WebView;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.squareup.picasso.Picasso;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MainActivityFrag extends AppCompatActivity         {
//
//    WebView webView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_frag);
//        webView =(WebView) findViewById(R.episodeId.WebId);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        ParserMoonwalk parserMoonwalk= new ParserMoonwalk("http://moonwalk.cc/serial/7d8f345200ac0c8fcd013db2ee159e00/iframe?season=1&episode=4","1","1");
//        parserMoonwalk.execute();
//
//    }
//
//    public class ParserMoonwalk extends AsyncTask<Void, Void, Void> {
//        String episodeUrl, cur_series, act, video;
//
//        TextView pb_text;
//
//        public ParserMoonwalk(String episodeUrl, String cur_series, String act) {
//            this.episodeUrl = episodeUrl;
//            this.cur_series = cur_series;
//            this.act = act;
//        }
//
//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//        }
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Log.d("mydebug", video);
////            webView.loadUrl(video);
//            //Log.d("mydebug",webView.getUrl());
//            super.onPostExecute(aVoid);
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//            video = parsingMoonwalk(episodeUrl);
//            Log.d("mydebug", video);
//            return null;
//        }
//
//        private String parsingMoonwalk(String episodeUrl) {
////            Document iframe = Getdata(episodeUrl);
////            if (iframe.select("body").text().contains("недоступен")) {
////                Log.d("mydebug", "видео недоступно");
////                return "error";
////            } else {
////                //token
////                String token = iframe.select("serialTitle").first().text().split(" ")[1];
////                Log.d("mydebug", "token :" + token);
////                //mw_pid
//////            String mw_pid = iframe.html().split("mw_pid: ")[1].split(",")[0];
////                String mw_pid = iframe.html().split("serial_token: '")[1].split("',")[0];
////                Log.d("post manifests", "mw_pid :" + mw_pid);
////
////                String vid_token = iframe.html().split("video_token: '")[1].split("',")[0];
////                Log.d("post manifests", "video_token: " + vid_token);
////                //p_domain_id
//////            String p_domain_id = iframe.html().split("p_domain_id: ")[1].split(",")[0];
////                String p_domain_id = iframe.html().split("domain_id: ")[1].split(",")[0];
////                Log.d("post manifests", "p_domain_id :" + p_domain_id);
////                //X-Access-Level
//////            String X_Access_Level = iframe.html().split("X-Access-Level': '")[1].split("'")[0];
////                Log.d("post manifests", "X-Access-Level :" + iframe.html());
////                String X_Access_Level = iframe.html().split("ref: '")[1].split("'")[0];
////                Log.d("post manifests", "ref :" + X_Access_Level);
////                //Log.d("post manifests", "X-Access-Level :" + X_Access_Level);
////                Document js_data = Getdata("http://clastarti.cc/serial/" + mw_pid+"/iframe?serialEpisode="+cur_series+"&ref="+X_Access_Level);
////                Document jsdata = Postdata1("http://clastarti.cc/stats/event",p_domain_id,vid_token);
////                String q ="yFop9xI2+HSMJ0owpuWrms+MlhD+t5WDcFKlI4kJn99ApsEcoy5BAd3KyTRCQnONFHzhjLccgD1t\\nlMOF2bbE69qADyQVscA5h3xmy5lAZm8EKHyZM75HwlJEluLml52Ep1yHwBgFAHGZr6FpCUU/oxIA\\nVV/JewVgTJCRz6cQmZtHNauspXgAitY5zBqQyu6/PryY+aHmzKQKDTSRYZJ2hR0";
////                Document vs_data = Postdata("http://clastarti.cc/vs",q,X_Access_Level,"http://clastarti.cc/serial/" + mw_pid+"/iframe?serialEpisode="+cur_series+"&ref="+X_Access_Level);
////                Log.d("post manifests", "event :" + vs_data.html());
////                Log.d("post manifests", "ref :" + js_data.html());
////                //mw_key
////                String mw_key ="cbn983jknmd";
////                Log.d("post manifests", "mw_key :" + mw_key);
////                Document jdata = Postdata("http://clastarti.cc/vs","",X_Access_Level);
////                Log.d("post manifests", "mw_key :" + jdata);
////                //x params
////                String x1 = js_data.html().split("mw_key:\"")[1].split("\",")[1].split(":\"")[0];
////                String x2 = js_data.html().split("mw_key:\"")[1].split(":\"")[1].split("\"")[0];
////                Log.d("post manifests", x1 + ":" + x2);
////
//////            //content_type
//////            String content_type = iframe.html().split("content_type: '")[1].split("'")[0];
////                String content_type = "0";
//////            Log.d("post manifests", "content_type :" + content_type);
//////            //x params
//////            String x1 = iframe.html().split("window\\['")[3].split("\\[")[1].split("]")[0];
//////            x1 = x1.replace("'","").replace(" ","").replace("+","");
//////            String x2 = iframe.html().split("window\\['")[3].split("=")[1].split(";")[0];
//////            x2 = x2.replace("'","").replace(" ","").replace("+","");
//////            Log.d("post manifests", x1 + ":" + x2);
////
////                //post on http://moonwalk.cc/manifests/video/[token]/all
////                Document manifests = Postdata("http://moonwalk.cc/manifests/video/"+token+"/all",
////                        content_type, mw_key);
////                if (manifests.text().contains("manifest_mp4")){
////                    String mp4_url = manifests.text().split("manifest_mp4\":\"")[1].split("\"")[0].replace("\\u0026", "&");
////                    Document video = Getdata(mp4_url);
////                    String mp4 = "";
////                    if (video.text().contains("360"))
////                        mp4 = mp4 + "|360|" + video.text().split("360\":\"")[1].split("\"")[0];
////                    if (video.text().contains("480"))
////                        mp4 = mp4 + "|480|" + video.text().split("480\":\"")[1].split("\"")[0];
////                    if (video.text().contains("720"))
////                        mp4 = mp4 + "|720|" + video.text().split("720\":\"")[1].split("\"")[0];
////                    if (video.text().contains("1080"))
////                        mp4 = mp4 + "|1080|" + video.text().split("1080\":\"")[1].split("\"")[0];
////                    return mp4;
////                }else {
////                    String m3u8_url = manifests.text().split("manifest_m3u8\": \"")[1].split("\"")[0];
////                    return m3u8_url;
////                }
////                return "http://clastarti.cc/serial/" + mw_pid+"/iframe?serialEpisode="+cur_series+"&ref="+X_Access_Level;
//                return GetFindData("jnxfzyst ljvj[jpzqrb").body().html();
////            }
//        }
//        private Document Getdata(String episodeUrl){
//            try {
//                Document htmlDoc = Jsoup.connect(episodeUrl)
//                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
//                        .referrer("http://coldfilm.ru/")
//                        .timeout(50000).ignoreContentType(true).get();
//                Log.d("mydebug","connected to " + episodeUrl);
//                return htmlDoc;
//            } catch (Exception e) {
//                Log.d("mydebug","connected false to " + episodeUrl);
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        private Document Postdata(String episodeUrl, String q, String ref,String referer){
//            try {
//                Document htmlDoc = Jsoup.connect(episodeUrl)
////                    .dataOfSerials("content_type", content_type)
//                        .dataOfSerials("q", q)
//                        .dataOfSerials("ref", ref)
//                        .header("X-Requested-With", "XMLHttpRequest")
//                        .header("Accept-Encoding", "gzip, deflate")
//                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36")
//                        .referrer(referer).timeout(50000).ignoreContentType(true).post();
//                Log.d("mydebug","post to " + episodeUrl);
//                return htmlDoc;
//            } catch (Exception e) {
//                Log.d("mydebug","post false to " + episodeUrl);
//                e.printStackTrace();
//                return null;
//            }
//        }
//        private Document Postdata1(String episodeUrl, String domain_id, String video_token){
//            try {
//                Document htmlDoc = Jsoup.connect(episodeUrl)
////                    .dataOfSerials("content_type", content_type)
//                        .dataOfSerials("episodeName", "click")
//                        .dataOfSerials("vast_null", "false")
//                        .dataOfSerials("adb", "false")
//                        .dataOfSerials("domain_id", domain_id)
//                        .dataOfSerials("video_token", video_token)
//                        .header("X-Requested-With", "XMLHttpRequest")
//                        .header("Accept-Encoding", "gzip, deflate")
//                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36")
//                        .referrer("http://moonwalk.cc/").timeout(50000).ignoreContentType(true).post();
//                Log.d("mydebug","post to " + episodeUrl);
//                return htmlDoc;
//            } catch (Exception e) {
//                Log.d("mydebug","post false to " + episodeUrl);
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        private Document GetFindData(String query){
//            try {
//                Document htmlDoc = Jsoup.connect("http://fanserials.gives/api/v1/serials?query="
//                        + URLEncoder.encode(query, "UTF-8"))
//                        .timeout(50000).ignoreContentType(true).get();
//                Log.d("mydebug","connected to " + episodeUrl);
//                return htmlDoc;
//            } catch (Exception e) {
//                Log.d("mydebug","connected false to " + episodeUrl);
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//    }
//
//}