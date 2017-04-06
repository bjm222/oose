package com.example.yuanweizhao.announcment.GeofenceAPI;


import android.util.Log;

import com.example.yuanweizhao.announcment.AnnouncementApplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * The controller for all geofence event including add geofence events and start/stop googleapi
 * And trigger geofence event
 */
public class GeofenceController {

    private static GeofenceController instance;
    private GoogleApiClient googleApiClient;
    private Geo geo;
    private Geofence geofence;

    // Get an instance of geofencecontroller, if doesn't exist create an new instance
    public static GeofenceController getInstance() {
        if (instance == null) {
            instance = new GeofenceController();
            Log.i("DEBUG","instance is Null");
        }
        return instance;
    }
    // Start google apiclient and will be handled by StartConnectionCallBack if successfully
    // start and ConnectionFailedCallBack if we fail to open the client
    public void startGoogleApiClient(GoogleApiClient.ConnectionCallbacks callback, ConnectionFailedCallBack connectionFailedListener) {
        googleApiClient = new GoogleApiClient.Builder(AnnouncementApplication.CONTEXT)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(callback)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        googleApiClient.connect();
    }

    // Stop googleApiClient
    public void stopGoogleApiClient() {
        googleApiClient.disconnect();
    }

    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }

    // The function to add a new geofence
    public void addGeofence(Geo geo) {
        this.geo = geo;
        this.geofence = geo.geofence();
        startGoogleApiClient(new AddGeoConnectionCallBack(), new ConnectionFailedCallBack());
    }

    // specifies the list of geofence to be monitored.
    public GeofencingRequest getAddGeofencingRequest() {
        List<Geofence> geofencesToAdd = AnnouncementApplication.getGeofences();
        Log.i("DEBUG","Reach1");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesToAdd);
        Log.i("DEBUG","Reach2");

        return builder.build();
    }
}
