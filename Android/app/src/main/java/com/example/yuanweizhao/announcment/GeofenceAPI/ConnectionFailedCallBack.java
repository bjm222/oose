package com.example.yuanweizhao.announcment.GeofenceAPI;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * If connection to googleapi client is failed print a failure message in Log
 */
public class ConnectionFailedCallBack implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("DEBUG", "CONNECTION FAILED");
    }
}