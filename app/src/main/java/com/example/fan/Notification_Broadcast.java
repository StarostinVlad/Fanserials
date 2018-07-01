package com.example.fan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Star on 27.01.2018.
 */

public class Notification_Broadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent NotifyService=new Intent(context,Notification_Service.class);
        context.startService(NotifyService);
    }
}
