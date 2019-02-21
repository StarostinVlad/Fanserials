package com.example.fan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Star on 27.01.2018.
 */

public class Notification_Service extends Service {
    private ArrayList<String> uris;
    private ArrayList<String> anno;
    private ArrayList<String> names;
    private ArrayList<String> last_names;
    private ArrayList<Seria> new_series;
    private ArrayList<String> new_anno;
    private ArrayList<String> previousNewSeries;

    SharedPreferences lastSeries;
    SharedPreferences sPref;

    NotificationManager Notify_Man;
    private ArrayList<Seria> series;

    @Override
    public void onCreate() {
        Notify_Man=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        sPref = getSharedPreferences("URL",MODE_PRIVATE);

        super.onCreate();
    }
    public boolean internet(){
        if (isNetworkOnline(this)) {
            return true;
        } else {
            return false;
        }
    }


    int i =0;

    final String SAVED_TEXT = "saved_text";

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
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                last_names = new ArrayList<String>();
                previousNewSeries = new ArrayList<String>();

                String queryUrl = sPref.getString(SAVED_TEXT, "");

                if(intent.getSerializableExtra("Series")!=null) {
                    series = (ArrayList<Seria>) intent.getSerializableExtra("Series");
                    //series= (ArrayList<Seria>) series.subList(0,20);
                }
                else
                    series=new ArrayList<Seria>();
                while(true) {
                    if (internet()) {
                        Document doc;
                        String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
                        String s, img = "";
                        if(quick_help.CheckResponceCode(queryUrl+"/new/")){
                            doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl+"/new/"));
                            Elements src = doc.select("li div div.item-serial");



                            new_series = new ArrayList<Seria>();
                            for (Element ss : src) {
                                String name = ss.select("div.serial-bottom div.field-title a").text();
                                String anno=ss.select("div.serial-bottom div.field-description a").text();
                                //Log.d("not_main", "an= "+anno);
                                Log.d("tnp", ss.select("div.serial-top div.field-img a").attr("href"));
                                String uri=ss.select("div.serial-top div.field-img a").attr("href");
                                s = ss.select("div.serial-top div.field-img").attr("style");
                                img = (img = s.substring(0, s.indexOf(");"))).substring(img.indexOf("url(") + 4);
                                Seria seria=new Seria(name,uri,img,anno);
                                new_series.add(seria);

                            }
                            if (series.size() == 0) {
                                series.addAll(new_series);
                                Log.d("service", "last name null");
                            } else {
                                Log.d("service", "last name not null");

                                ArrayList<String> series_names = new ArrayList<>();
                                for (Seria seria : series)
                                    series_names.add(seria.getName());

                                for (Seria new_seria : new_series) {
                                    if (!series_names.contains(new_seria.getName())) {
                                        i++;

                                        NotifySend(i, new_seria.getName(), new_seria.getDescription());
                                    }
                                }
                                series.addAll(new_series);
                                new_series.clear();
                            }
                        }
                        else{
                            doc = Jsoup.parse(quick_help.GiveDocFromUrl("https://mrstarostinvlad.000webhostapp.com/actual_adres.php"));
                            queryUrl=doc.select("h1").text();

                            sPref = getSharedPreferences("URL",MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString(SAVED_TEXT,"http://"+queryUrl );
                            ed.commit();
                        }
                        try {
                            Thread.sleep(300000);
                            NotifySend(i, "Пока ничего", "но я работаю");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else Log.d("service", "no internet");
                }
            }
        }).start();
        return START_STICKY;
    }
    void NotifySend(int id,String new_serie,String new_anno){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        // до версии Android 8.0 API 26
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.play)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(new_serie)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(new_anno) // Текст уведомления
                // необязательные настройки
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo1)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[] { 200, 100 ,200})
                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
