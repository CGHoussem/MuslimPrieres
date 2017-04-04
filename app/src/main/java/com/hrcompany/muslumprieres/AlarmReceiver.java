package com.hrcompany.muslumprieres;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String salat_name = intent.getStringExtra("SALAT_NAME");
        String salat_time = intent.getStringExtra("SALAT_TIME");
        String title = context.getString(R.string.temps_pour_priere_text) + salat_name + context.getString(R.string.a_text) + salat_time;
        String message = context.getString(R.string.consulter_les_prieres);

        long[] vibrate = {0, 100, 200, 300};
        Uri athanSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.athan_1);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);

        MediaPlayer mediaPlayer = MediaPlayer.create(context, athanSound);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.muslum_prieres_app_icon_bw)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                //.setSound(athanSound)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
