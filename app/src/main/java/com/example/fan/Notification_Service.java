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
    private ArrayList<String> uris,anno,names,last_names,new_series,new_anno,previousNewSeries;

    SharedPreferences lastSeries;

    NotificationManager Notify_Man;

    @Override
    public void onCreate() {
        Notify_Man=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }
    public boolean internet(){
        if (isNetworkOnline(this)) {
            return true;
        } else {
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
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                last_names = new ArrayList<String>();
                previousNewSeries = new ArrayList<String>();
                while(true) {
                    if (internet()) {
                        Document doc;
                        String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
                        String s, img = "";
                        if(quick_help.CheckResponceCode(getString(R.string.url)+"/new/")){
                            doc = Jsoup.parse(quick_help.GiveDocFromUrl(getString(R.string.url)+"/new/"));
                            Elements src = doc.select("li div div.item-serial");
                            uris = new ArrayList<String>();
                            anno = new ArrayList<String>();
                            names = new ArrayList<String>();
                            new_series = new ArrayList<String>();
                            new_anno = new ArrayList<String>();
                            for (Element ss : src) {
                                names.add(ss.select("div.serial-bottom div.field-title a").text());
                                anno.add(ss.select("div.serial-bottom div.field-description a").text());
                                Log.d("tnp", "from service " + ss.select("div.serial-top div.field-img a").attr("href"));
                                uris.add(ss.select("div.serial-top div.field-img a").attr("href"));
                            }
                            if (last_names.size() == 0) {
                                for (int i = 0; i < names.size(); i++) {
                                    last_names.add(names.get(i));
                                }
                                Log.d("service", "last name null");
                            } else {
                                Log.d("service", "last name not null");
                            }

                            boolean serieExist = false;
                            for (int i = 0; i < names.size(); i++) {
                                for (int j = 0; j < names.size(); j++) {
                                    if (names.get(j).equals(last_names.get(i))) {
                                        serieExist = true;
                                        break;
                                    }
                                }
                                if (!serieExist) {
                                    new_series.add(names.get(i));
                                    new_anno.add(anno.get(i));
                                }
                                serieExist = false;
                            }
                            for (int i = 0; i < new_series.size(); i++) {
                                if (previousNewSeries.size() != 0) {
                                    NotifySend(i, new_series.get(i), new_anno.get(i));
                                    if (!previousNewSeries.get(i).equals(new_series.get(i))) {
                                        NotifySend(i, new_series.get(i), new_anno.get(i));
                                        break;
                                    }
                                } else NotifySend(i, new_series.get(i), new_anno.get(i));
                            }
                            previousNewSeries.clear();
                            previousNewSeries.addAll(new_series);
                            new_anno.clear();
                            new_series.clear();
                            last_names.clear();
                            if (last_names.size() == 0) {
                                last_names.addAll(names);
                            }
                        }
                        try {
                            Thread.sleep(1000);
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
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.play)) // большая
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
