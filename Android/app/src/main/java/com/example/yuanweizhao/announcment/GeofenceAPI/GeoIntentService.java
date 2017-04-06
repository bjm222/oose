package com.example.yuanweizhao.announcment.GeofenceAPI;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.AnnouncementApplication;
import com.example.yuanweizhao.announcment.MainActivity;
import com.example.yuanweizhao.announcment.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.app.Notification.PRIORITY_HIGH;

/**
 * This is the intenetservice that run geofence
 * In this intent service we will detect if user has been enter/stayin/exit an geofence region
 * And do corresponding operation based on user's location
 */
public class GeoIntentService extends IntentService {

    private String LOG_TAG = "DEBUG";
    public GeoIntentService() {
        super("GeoIntentService");
    }

    /**
     * Handle geofence event when this itent is triggered
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event == null) {
            return;
        }

        if (event.hasError()) {
            Log.i(LOG_TAG, "ERROR");
            return;
        }

        int transition = event.getGeofenceTransition();
        /**
         *  When user enter geofence area
         */
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.i(LOG_TAG, "ENTER");
            int counter = 0;
            Map<Integer, Announcement> Announcements = AnnouncementApplication.getGeofencesHash();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> s = new HashSet<String>(prefs.getStringSet("set", new HashSet<String>()));
            // An announcement is only valid for notification only if it has not been pushed yet
            // And the eventtime is within 30 min
            for(Geofence g: event.getTriggeringGeofences()) {
                Announcement tmp = Announcements.get(Integer.valueOf(g.getRequestId()));
                Log.i(LOG_TAG, tmp.getAnnouncement_eventTime());
                Log.i(LOG_TAG, tmp.getAnnouncement_title());
                if(!s.contains(String.valueOf(tmp.getAnnouncement_id()))) {
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    try {
                        Date date1 = df.parse(tmp.getAnnouncement_eventTime());
                        Date date2 = df.parse(formattedDate);
                        Long difference = date2.getTime()-date1.getTime();
                        if(difference <= 120*60*1000) {
                            s.add(String.valueOf(tmp.getAnnouncement_id()));
                            counter++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putStringSet("set",s);
            editor.commit();
            Intent resultIntent = new Intent(this, MainActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            if(counter > 0) {
                NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify = new Notification.Builder
                        (getApplicationContext()).setContentTitle("Announcement+")
                        .setContentText("There are " + counter + " events near you! Click to check")
                        .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                        .setPriority(PRIORITY_HIGH)
                        .setContentIntent(resultPendingIntent)
                        .build();
                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notify.defaults |= Notification.DEFAULT_VIBRATE;
                notify.visibility |= Notification.VISIBILITY_PUBLIC;
                notify.defaults = Notification.DEFAULT_ALL;
                notif.notify(0, notify);
                Log.i(LOG_TAG, String.valueOf(counter));
            }
         } else if(transition == Geofence.GEOFENCE_TRANSITION_DWELL ){
            Log.i(LOG_TAG, "DWELL");
        }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(LOG_TAG, "EXIT");
        }

    }
}
