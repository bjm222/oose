package com.example.yuanweizhao.announcment.ServerAPI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;

/**
 * This is a helper function to check whether the App has access to the network
 */

public class OnlineHelper {
    /**
     * check whether the network is available or not
     *
     * @param fa
     * @return boolean
     */
    public boolean isOnline(FragmentActivity fa) {
        ConnectivityManager cm = (ConnectivityManager) fa.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
