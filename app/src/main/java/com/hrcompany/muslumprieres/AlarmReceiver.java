package com.hrcompany.muslumprieres;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String prayerName = intent.getStringExtra("PRAYER_NAME");
        String prayerTime = intent.getStringExtra("PRAYER_TIME");

        Intent prayersIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, prayersIntent , 0);

        long[] vibrationPattern = new long[]{0, 100, 200};
        Uri athan = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.azan1);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Time for prayer !")
                .setContentText(prayerName+" "+prayerTime)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.muslum_prieres_app_icon_bw)
                .setVibrate(vibrationPattern)
                .setSound(athan)
                //.setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }
}
