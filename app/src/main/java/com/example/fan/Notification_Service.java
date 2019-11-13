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
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.fan.App.channelId;

/**
 * Created by Star on 27.01.2018.
 */

public class Notification_Service extends Service {
    final String SAVED_TEXT = "saved_text";
    SharedPreferences lastSeries;
    SharedPreferences sPref;

    NotificationManager Notify_Man;
    int i = 0;
    private ArrayList<Seria> new_series;
    private ArrayList<Seria> series;

    @Override
    public void onCreate() {
        Notify_Man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sPref = getSharedPreferences("URL", MODE_PRIVATE);

        super.onCreate();
    }

    public boolean internet() {
        return isNetworkOnline(this);
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

                String queryUrl = sPref.getString(SAVED_TEXT, "");

                if (intent.getSerializableExtra("Series") != null) {
                    series = (ArrayList<Seria>) intent.getSerializableExtra("Series");
                    //series= (ArrayList<Seria>) series.subList(0,20);
                } else
                    series = new ArrayList<Seria>();

                queryUrl += "/api/v1/episodes?limit=30&offset=";
                Log.d("service", "episodeUrl: " + queryUrl);
                while (true) {
                    try {
                        if (internet()) {
                            Document doc;
                            String agent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Mobile Safari/537.36";
                            String s, img = "";
                            if (quick_help.CheckResponceCode(queryUrl)) {
//                            doc = Jsoup.parse(quick_help.GiveDocFromUrl(queryUrl+"/new/"));
                                doc = Jsoup.connect(queryUrl).ignoreContentType(true).get();

                                new_series = new ArrayList<Seria>();
                                new_series.addAll(getNewSeries(doc));

//                                Elements src = doc.select("li div div.item-newSerial");
//
//
//                                new_series = new ArrayList<Seria>();
//                                for (Element ss : src) {
//                                    String episodeName = ss.select("div.newSerial-bottom div.field-serialTitle a").text();
//                                    String anno = ss.select("div.newSerial-bottom div.field-serialDescription a").text();
//                                    //Log.d("not_main", "an= "+anno);
//                                    Log.d("tnp", ss.select("div.newSerial-top div.field-img a").attr("serialHref"));
//                                    String uri = ss.select("div.newSerial-top div.field-img a").attr("serialHref");
//                                    s = ss.select("div.newSerial-top div.field-img").attr("style");
//                                    img = (img = s.substring(0, s.indexOf(");"))).substring(img.indexOf("episodeUrl(") + 4);
//                                    Seria seria = new Seria(episodeName, uri, img, anno);
//                                    new_series.add(seria);
//
//                                }
                                if (series.size() == 0) {
                                    series.addAll(new_series);
                                    Log.d("service", "last episodeName null");
                                } else {
                                    Log.d("service", "last episodeName not null");

                                    ArrayList<String> series_names = new ArrayList<>();
                                    for (Seria seria : series)
                                        series_names.add(seria.getName());


                                    for (Seria new_seria : new_series) {
//                                        Log.d("service", "есть новая серия" + new_seria.getName());
                                        if (!series_names.contains(new_seria.getName())) {
                                            Log.d("service", "есть новая серия " + new_seria.getName());
                                            i++;
                                            NotifySend(i, new_seria.getName(), new_seria.getDescription());
                                        }
                                    }

//                                    if (!series.containsAll(new_series)) {
//                                        Log.d("service", "!series.equals(new_series)");
//                                        for (Seria new_seria : new_series) {
//                                            if (!series.contains(new_seria)) {
//                                                i++;
//                                                Log.d("service", "!series.contains(new_seria)");
//                                                NotifySend(i, new_seria.getName(), new_seria.getDescription());
//                                            }
//                                        }
//                                    }
                                    series.addAll(new_series);
                                    new_series.clear();
                                }
                            } else {
                                doc = Jsoup.parse(quick_help.GiveDocFromUrl("https://mrstarostinvlad.000webhostapp.com/actual_adres.php"));
                                queryUrl = doc.select("h1").text();

                                sPref = getSharedPreferences("URL", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString(SAVED_TEXT, "http://" + queryUrl);
                                ed.commit();
                            }
                            try {
                                Thread.sleep(10000);
                                Log.d("notification", "alive but wait");
//                                NotifySend(i, "Пока ничего", "но я работаю");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else Log.d("service", "no internet");
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
        return START_STICKY;
    }


    ArrayList<Seria> getNewSeries(Document doc) {
//        Log.d("MainFragment", doc.body().html());
        ArrayList<Seria> Series = new ArrayList<>();
        FanserJsonApi fanserJsonApi = null;
        try {
            fanserJsonApi = LoganSquare.parse(doc.body().html(), FanserJsonApi.class);
            for (FanserJsonApi.DataOfNewSer item : fanserJsonApi.dataOfSerials) {
//                Log.d("Name ", item.newSerial.episodeName + " : " + item.serialEpisode.episodeName + " : " + item.serialEpisode.episodeUrl + " : " + item.serialEpisode.episodeImages.smallImage);
                Seria seria = new Seria(item.newSerial.serialName, item.serialEpisode.episodeUrl,
                        item.serialEpisode.episodeImages.smallImage, item.serialEpisode.episodeName);
                Series.add(seria);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Series;
    }

    void NotifySend(int id, String new_serie, String new_anno) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();


//        Notification notification;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//            notification = new NotificationCompat.Builder(getApplicationContext(),App.channelId)
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentIntent(contentIntent)
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
//                    .setWhen(System.currentTimeMillis())
//                    .setVibrate(new long[]{200, 100, 200})

                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
                    .build();
            managerCompat.notify(id, notification);
        }
        // до версии Android 8.0 API 26
        else {
            Notification notification = new Notification.Builder(this)
                    .setContentIntent(contentIntent)
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
                    .setVibrate(new long[]{200, 100, 200})
                    .setAutoCancel(true)
                    .build(); // автоматически закрыть уведомление после нажатия
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Альтернативный вариант
            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (notificationManager != null) {
                notificationManager.notify(id, notification);
            }
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
