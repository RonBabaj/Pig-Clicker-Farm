package com.example.hazirclicker2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class WakeUpAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            //restarts alarm when the device is rebooted

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);

            Intent myIntent = new Intent(context, DailyReceiver.class);
            int ALARM1_ID = 100;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}