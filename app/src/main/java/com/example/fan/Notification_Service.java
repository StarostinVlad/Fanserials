package com.example.fan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    boolean stop = false;
    private ArrayList<FanserJsonApi.DataOfNewSer> new_series;
    private ArrayList<FanserJsonApi.DataOfNewSer> series;

    @Override
    public void onCreate() {
        Notify_Man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sPref = getSharedPreferences("URL", MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (!stop) {
            Intent intent = new Intent(getApplicationContext(), this.getClass());
            intent.setPackage(getPackageName());
            startService(intent);
        }
        super.onTaskRemoved(rootIntent);
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
        asyncNotify asyncNotify = new asyncNotify();
        asyncNotify.execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotifySend(666,"Уведомления отключились","это плохо!","");
        //Log.d("service", "destoyed");
        stop = true;
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

    ArrayList<FanserJsonApi.DataOfNewSer> getNewSeriesJson(Document doc) {
//        Log.d("MainFragment", doc.body().html());
        ArrayList<Seria> Series = new ArrayList<>();
        FanserJsonApi fanserJsonApi = null;
        try {
            fanserJsonApi = LoganSquare.parse(doc.body().html(), FanserJsonApi.class);
            return fanserJsonApi.dataOfSerials;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    void NotifySend(int id, String new_serie, String new_anno, String imageUrl) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();


//        Notification notification;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//            notification = new NotificationCompat.Builder(getApplicationContext(),App.channelId)
                Notification notification = null;
                notification = new Notification.Builder(this, channelId)
                        .setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.play)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle(new_serie)
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText(new_anno) // Текст уведомления
                        // необязательные настройки
                        //                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo1)) // большая
                        .setLargeIcon(Picasso.with(getApplicationContext())
                                .load(imageUrl).error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image).get()) // большая
                        // картинка
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        //                    .setWhen(System.currentTimeMillis())
                        //                    .setVibrate(new long[]{200, 100, 200})

                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
                        .build();
                managerCompat.notify(id, notification);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // до версии Android 8.0 API 26
        else {
            try {
                Notification notification = null; // автоматически закрыть уведомление после нажатия
                notification = new Notification.Builder(this)
                        .setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.play)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle(new_serie)
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText(new_anno) // Текст уведомления
                        // необязательные настройки
                        .setLargeIcon(Picasso.with(getApplicationContext())
                                .load(imageUrl).error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image).get()) // большая
                        // картинка
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        .setWhen(System.currentTimeMillis())
                        .setVibrate(new long[]{200, 100, 200})
                        .setAutoCancel(true)
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // Альтернативный вариант
                // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (notificationManager != null) {
                    notificationManager.notify(id, notification);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class asyncNotify extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

         return null;
        }
    }
}
