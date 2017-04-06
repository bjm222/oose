package com.example.yuanweizhao.announcment;

import android.app.Application;
import android.content.Context;

import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.google.android.gms.location.Geofence;

import java.util.List;
import java.util.Map;

/**
 *  This is an application level file that store global data for this application
 *  Because the list of geofence is added in mapview but used in geointent service
 *  We need to store this information as global
 */

public class AnnouncementApplication extends Application {
    public static Context CONTEXT;
    private static List<Geofence> geofencesToAdd;
    private static Map<Integer,Announcement> geofenceHash;


    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }

    public static List<Geofence> getGeofences() {
        return geofencesToAdd;
    }

    public void setGetgeofences(List<Geofence> geofencesToAdd) {
        this.geofencesToAdd = geofencesToAdd;
    }

    public static Map<Integer, Announcement> getGeofencesHash() {
        return geofenceHash;
    }

    public void setGetgeofencesHash(Map<Integer, Announcement> geofencesHash) {
        this.geofenceHash = geofencesHash;
    }
}
