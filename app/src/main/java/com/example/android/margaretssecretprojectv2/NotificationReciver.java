package com.example.android.margaretssecretprojectv2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReciver extends BroadcastReceiver {

    public static String LOG = "NotificationReciver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d(LOG, "Notfication Triggered");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.heart)
                .setSound(alarmSound)
                .setContentTitle("Test Notification")
                .setContentText("Notification Content")
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());

    }
}