package com.starostinvlad.fan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Star on 27.01.2018.
 */

public class Notification_Broadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Intent NotifyService = new Intent(context, Notification_Service.class);
//            context.startService(NotifyService);
//            Toast.makeText(context,"started!",Toast.LENGTH_LONG).show();
//        }
    }
}
