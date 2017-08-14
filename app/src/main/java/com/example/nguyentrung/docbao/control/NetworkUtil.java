package com.example.nguyentrung.docbao.control;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectyType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static boolean hasInternet(Context context){
        if (getConnectyType(context) == TYPE_WIFI || getConnectyType(context) == TYPE_MOBILE){
            return true;
        }
        return false;
    }

    public static String getConnectStatus(Context context) {
        int connect = NetworkUtil.getConnectyType(context);
        String status = null;
        if (connect == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enable";
        } else if (connect == NetworkUtil.TYPE_MOBILE) {
            status = "Mobi enable";
        } else {
            status = "Not connected to Internet";
        }
        return status;
    }
}
