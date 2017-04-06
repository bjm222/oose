package com.example.yuanweizhao.announcment.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuanweizhao.announcment.AnnouncementApplication;
import com.example.yuanweizhao.announcment.AnnouncementUI.AnnouncementDetailActivity;
import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.GeofenceAPI.ConnectionFailedCallBack;
import com.example.yuanweizhao.announcment.GeofenceAPI.GeofenceController;
import com.example.yuanweizhao.announcment.GeofenceAPI.StartConnectionCallBack;
import com.example.yuanweizhao.announcment.R;
import com.example.yuanweizhao.announcment.ServerAPI.HttpController;
import com.example.yuanweizhao.announcment.ServerAPI.HttpRequestPackage;
import com.example.yuanweizhao.announcment.ServerAPI.OnlineHelper;
import com.example.yuanweizhao.announcment.ServerAPI.ParseJSONHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The fragment shows when user click Events near me
 */

public class myMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private OnlineHelper onlineHelper = new OnlineHelper();

    /**
     * When open the map automatically zoom to current location
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .tilt(0)
                    .bearing(0)                // Sets the orientation of the camera to east
                    .build();                  // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Customiz info window for our map fragment
     */
    private class myInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        myInfoWindowAdapter() {
            this.myContentsView = getActivity().getLayoutInflater().inflate(R.layout.my_info_window,null);

        }
        @Override
        public View getInfoWindow(Marker marker) {
            Announcement announcement = announcements.get(Integer.parseInt(marker.getSnippet()));
            TextView Title = ((TextView)myContentsView.findViewById(R.id.windowTitle));
            Title.setText(announcement.getAnnouncement_title());
            TextView Summary = ((TextView)myContentsView.findViewById(R.id.windowSummary));
            Summary.setText(announcement.getAnnouncement_summary());
            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }

    MapView mMapView;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private LocationManager locationManager;
    private String provider;
    private GoogleApiClient mGoogleApiClient;

    /**
     * When the map fragment is first created get data from server and connect to googleAPI
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (onlineHelper.isOnline(getActivity())) {
            myMapFragment.MyTask myTask = new myMapFragment.MyTask();
            HttpRequestPackage httpRequestPackage = new HttpRequestPackage();
            httpRequestPackage.setUri(getString(R.string.api_url));
            httpRequestPackage.setMethod("GET");
            myTask.execute(httpRequestPackage);
        } else {
            Toast.makeText(getContext(), "Network is not available.", Toast.LENGTH_LONG).show();
        }
        View rootView = inflater.inflate(R.layout.fragment_my_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView1);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(),false);
        mMapView.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        return rootView;
    }

    /**
     * Detect the user's location's change
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location change", String.valueOf(location.getLatitude()));
        mGoogleApiClient.connect();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
        mGoogleApiClient.connect();
        locationManager.requestLocationUpdates(provider,400,1,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        checkPermission();
        mGoogleApiClient.disconnect();
        locationManager.removeUpdates(this);
    }

    /**
     * Initialize the map, setup makers on map, and add events as geofence
     * @param mMap
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        // Check App's permission
        checkPermission();
        googleMap.setMyLocationEnabled(true);

        googleMap.setInfoWindowAdapter(new myInfoWindowAdapter());
        googleMap.setOnInfoWindowClickListener(this);
        // For dropping markers at on the Map based on announcement's information
        final Handler handler = new Handler();

        boolean b = handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Geofence> geofencesToAdd = new ArrayList<>();
                Map<Integer, Announcement> geofencesHash = new HashMap<>();
                Log.i("DEBUG", "Start adding geo");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                int range = Integer.parseInt(prefs.getString("range", "100"));
                for (int i = 0; i < announcements.size(); i++) {
                    LatLng place = new LatLng(announcements.get(i).getAnnouncement_locationLat(), announcements.get(i).getAnnouncement_locationLng());
                    googleMap.addMarker(new MarkerOptions().position(place).snippet(String.valueOf(i)));
                    geofencesToAdd.add(new Geofence.Builder()
                            .setRequestId(Integer.toString(announcements.get(i).getAnnouncement_id()))
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setLoiteringDelay(1000)
                            .setCircularRegion(place.latitude, place.longitude, range)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .build());

                    geofencesHash.put(announcements.get(i).getAnnouncement_id(), announcements.get(i));
                }
                Log.i("DEBUG", "finish adding geo");
                if (!geofencesToAdd.isEmpty()) {
                    ((AnnouncementApplication) getActivity().getApplication()).setGetgeofences(geofencesToAdd);
                    ((AnnouncementApplication) getActivity().getApplication()).setGetgeofencesHash(geofencesHash);
                    if (prefs.getBoolean("notification", true)) {
                        Log.i("DEBUG", "startGEO");
                        GeofenceController.getInstance().startGoogleApiClient(new StartConnectionCallBack(),
                                new ConnectionFailedCallBack());
                    } else {
                        Log.i("DEBUG", "stopGEO");
                    }
                }
            }
        }, 1000);
    }

    /**
     * listener function when a marker's info window is pressed
     * Pass infomation need to detail activity and open detail activity
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Announcement announcement = announcements.get(Integer.parseInt(marker.getSnippet()));
        Intent intent = new Intent(getActivity(),AnnouncementDetailActivity.class);
        intent.putExtra("ANNOUNCEMENT_ID_EXTRA",announcement.getAnnouncement_id());
        intent.putExtra("ANNOUNCEMENT_TITLE_EXTRA",announcement.getAnnouncement_title());
        intent.putExtra("ANNOUNCEMENT_SUMMARY_EXTRA",announcement.getAnnouncement_summary());
        intent.putExtra("ANNOUNCEMENT_EVENTTIME_EXTRA",announcement.getAnnouncement_eventTime());
        intent.putExtra("ANNOUNCEMENT_LOCATIONNAME_EXTRA",announcement.getAnnouncement_locationName());
        intent.putExtra("ANNOUNCEMENT_LOCATIONDESC_EXTRA",announcement.getAnnouncement_locationDesc());
        intent.putExtra("ANNOUNCEMENT_FOODPROVIDED_EXTRA",announcement.isAnnouncement_foodProvided());
        intent.putExtra("ANNOUNCEMENT_HOSTORGANIZATION_EXTRA",announcement.getAnnouncement_hostOrganization());
        intent.putExtra("ANNOUNCEMENT_HOSTEMAIL_EXTRA",announcement.getAnnouncement_hostEmail());
        intent.putExtra("ANNOUNCEMENT_ISEVENT_EXTRA",String.valueOf(announcement.isAnnouncement_isEvent()));
        intent.putExtra("ANNOUNCEMENT_TYPE", "server");
        startActivity(intent);
    }

    /**
     * Check user's permission, if App doesn't have permission for GPS ask for it.
     */
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);

        }
    }

    /**
     * task of background thread to get data from server
     */
    private class MyTask extends AsyncTask<HttpRequestPackage, String, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(HttpRequestPackage... params) {
            String strJson = HttpController.getAllAnnouncements(params[0]);
            if (strJson == null) {
                publishProgress(strJson);
                Log.d("error", "can't open url");
            } else {
                Log.d("in the bg", strJson);
            }
            return strJson;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                ParseJSONHelper parseJSONHelper = new ParseJSONHelper();
                announcements = parseJSONHelper.parseJSON(result);
                Toast.makeText(getContext(), "Loading complete.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getContext(), "Server is not available.", Toast.LENGTH_LONG).show();
        }
    }
}
