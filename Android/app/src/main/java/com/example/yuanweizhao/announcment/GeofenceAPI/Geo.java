package com.example.yuanweizhao.announcment.GeofenceAPI;


import com.google.android.gms.location.Geofence;

import java.util.UUID;

/**
 * The data model for geofence
 */
public class Geo {
    public String id;
    public String name;
    public double latitude;
    public double longitude;
    public int radius;

    /**
     * The constructor for geofence
     * @return
     */
    public Geofence geofence() {
        id = UUID.randomUUID().toString();
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1000)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }


}
