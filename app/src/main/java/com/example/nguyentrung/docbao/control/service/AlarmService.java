package com.example.nguyentrung.docbao.control.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.view.activity.MainActivity;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        News news = (News) intent.getSerializableExtra(Receiver.KEY_NEWS_NOTIFICATION);
        Log.e(TAG, "KEY_NEWS_NOTIFICATION: " + news.toString());
        sendNotification(news);
//        Toast.makeText(getApplicationContext(), TAG + "onHandleIntent", Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(News news) {
        Log.d("AlarmService", "Preparing to send notification...: " + news.getTitle());
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Constant.KEY_ITEM_NEWS_NOTIFICATION, news);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle(getResources().getString(R.string.app_name)).setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(news.getTitle()))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(news.getTitle());

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.e("AlarmService", "Notification sent.");
    }


}