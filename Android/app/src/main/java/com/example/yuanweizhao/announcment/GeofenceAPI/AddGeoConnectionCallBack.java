package com.example.yuanweizhao.announcment.GeofenceAPI;


import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.yuanweizhao.announcment.AnnouncementApplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

/**
 *  Amplify the GeofenceController with a new method addGeofence().
 *  With this method we create a new geofence and start the GoogleApiClient with our callbacks.
 *  So we create the new callback AddGeoConnectionCallBack where i add a new service to trigger
 *  the events.
 */
public class AddGeoConnectionCallBack implements  GoogleApiClient.ConnectionCallbacks{

    private String LOG_TAG = "DEBUG";

    /**
     * If the geofence connection is established deligate all the logic to geointentservice
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(AnnouncementApplication.CONTEXT, GeoIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(AnnouncementApplication.CONTEXT, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // The error has already be handled in map view so no need to double check permission here
        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(GeofenceController.getInstance().getGoogleApiClient(), GeofenceController.getInstance().getAddGeofencingRequest(), pendingIntent);
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(LOG_TAG, "SUCCESS" + status);
                } else {
                    Log.i(LOG_TAG, "FAILED: " + status.getStatusMessage() + " : " + status.getStatusCode());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
