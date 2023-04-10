package com.example.hazirclicker2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentContainerView;

public class DailyReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "my_channel_01";
    private static final CharSequence CHANNEL_NAME = "My Channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        initnotif(context,"We Already Missed You!","Come And Claim Your Daily Reward Today!");
    }

    public static void initnotif(Context context, String title, String message) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Claim Your Daily Reward");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("message in a bottle","message in a bottle");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.haziricon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent).setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(100, notification);
    }
}
