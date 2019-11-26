package com.example.fan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.fan.App.channelId;


public class CloudMessage extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotifySend(remoteMessage);

        super.onMessageReceived(remoteMessage);
    }

    void NotifySend(RemoteMessage remoteMessage) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String imageUri = "";
        if(remoteMessage.getData().containsKey("largeIcon")) {
            //Log.d("message", "icon: " + remoteMessage.getData().get("largeIcon"));
            imageUri = remoteMessage.getData().get("largeIcon");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

//            notification = new NotificationCompat.Builder(getApplicationContext(),App.channelId)
                Notification customNotification = new Notification.Builder(this, channelId)
                        .setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.play)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle(notification.getTitle())
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText(notification.getBody()) // Текст уведомления
                        // необязательные настройки
                        //                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo1)) // большая
                        .setLargeIcon(Picasso.with(getApplicationContext())
                                .load(imageUri).error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image).get()) // большая
                        // картинка
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        //                    .setWhen(System.currentTimeMillis())
                        //                    .setVibrate(new long[]{200, 100, 200})

                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
                        .build();
                managerCompat.notify(0, customNotification);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // до версии Android 8.0 API 26
        else {
            try {
                Notification customNotification = new Notification.Builder(this)
                        .setContentIntent(contentIntent)
                        // обязательные настройки
                        .setSmallIcon(R.drawable.play)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                        .setContentTitle(notification.getTitle())
                        //.setContentText(res.getString(R.string.notifytext))
                        .setContentText(notification.getBody()) // Текст уведомления
                        // необязательные настройки
                        //                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo1)) // большая
                        .setLargeIcon(Picasso.with(getApplicationContext())
                                .load(imageUri).error(R.drawable.no_image)
                                .placeholder(R.drawable.no_image).get()) // большая
                        // картинка
                        //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                        //                    .setWhen(System.currentTimeMillis())
                        //                    .setVibrate(new long[]{200, 100, 200})

                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setAutoCancel(true) // автоматически закрыть уведомление после нажатия
                        .build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // Альтернативный вариант
                // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (notificationManager != null) {
                    notificationManager.notify(0,customNotification);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}