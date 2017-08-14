package com.example.nguyentrung.docbao.control.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class NotificationService  extends Service {

    private static final int NOTIFICATION_ID = 6789;
    private static final String TAG = "NotificationService";
    private static final String NOTIFY = "notify";
    private static final String IS_NOTIFID = "is notified";
    private boolean isNotified;
    private SimpleDateFormat simpleDateFormat;
    private Calendar calendar;
    private String time;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        calendar = Calendar.getInstance();

        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        time = simpleDateFormat.format(calendar.getTime());
        Log.e(TAG, "Time: " + time);
        SharedPreferences sharedPreferences = getSharedPreferences(NOTIFY, MODE_PRIVATE);
        isNotified = sharedPreferences.getBoolean(IS_NOTIFID, false);

        Log.e(TAG, "onStartCommand");
//        setTimeGetNotification();
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("readingnews")
                .setContentText("hahahahahah")
                .setSmallIcon(R.drawable.ic_logo)
                .setAutoCancel(true);
        Intent intentForeground = new Intent(getApplicationContext(), MainActivity.class);
        intentForeground.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intentForeground.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intentForeground, 0);
        builder.setContentIntent(pendingIntent);
        startForeground(NOTIFICATION_ID, builder.build());*/
        return START_STICKY;
    }

    private void setTimeGetNotification() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 56);
        Intent intent = new Intent(this, Receiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HALF_DAY, 0, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_HALF_DAY, 0, pendingIntent);
    }
}
