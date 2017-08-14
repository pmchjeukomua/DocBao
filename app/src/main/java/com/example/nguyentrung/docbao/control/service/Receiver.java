package com.example.nguyentrung.docbao.control.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.nguyentrung.docbao.control.AsyncTaskParseXML;
import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.control.NetworkUtil;
import com.example.nguyentrung.docbao.model.News;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class Receiver extends WakefulBroadcastReceiver {
    private static final String TAG = "Receiver";
    public static final String KEY_NEWS_NOTIFICATION = "key news notification";
    private ArrayList<News> arrNews = new ArrayList<>();
    private Context context;
    //    private int typeNewspaper;
    private String LINK_DEFAULT = "http://www.24h.com.vn/upload/rss/tintuctrongngay.rss";
    private Intent newIntent = new Intent();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        this.context = context;
        if (NetworkUtil.getConnectyType(context) != 0) {
            AsyncTaskParseXML asyncTaskParseXML = new AsyncTaskParseXML(context, handler, Constant.TYPE_24H);
            asyncTaskParseXML.execute(LINK_DEFAULT);
        }
        setResultCode(Activity.RESULT_OK);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.WHAT_ARR) {
                arrNews.addAll((Collection<? extends News>) msg.obj);
                if (arrNews.size() > 0) {
                    newIntent.putExtra(KEY_NEWS_NOTIFICATION, arrNews.get(0));
                    ComponentName comp = new ComponentName(context.getPackageName(),
                            AlarmService.class.getName());
                    startWakefulService(context, (newIntent.setComponent(comp)));
                }
            }
        }
    };

}
