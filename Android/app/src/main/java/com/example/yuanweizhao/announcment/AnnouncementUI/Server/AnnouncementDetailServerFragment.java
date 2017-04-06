package com.example.yuanweizhao.announcment.AnnouncementUI.Server;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.DatabaseAPI.AnnouncementDatabaseAdapter;
import com.example.yuanweizhao.announcment.R;
import com.example.yuanweizhao.announcment.ServerAPI.HttpController;
import com.example.yuanweizhao.announcment.ServerAPI.HttpRequestPackage;
import com.example.yuanweizhao.announcment.ServerAPI.OnlineHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the server announcement detail fragment
 */
public class AnnouncementDetailServerFragment extends Fragment {
    private Announcement announcements;
    private AlertDialog confirmDialogObject;

    private int announcement_id;
    private String announcement_title;
    private String announcement_summary;
    private String announcement_detail;
    private String announcement_eventTime;
    private String announcement_locationName;
    private String announcement_locationDesc;
    boolean announcement_foodProvided;
    private String announcement_hostOrganization;
    private String announcement_hostEmail;
    private boolean announcement_isEvent;

    private TextView title;
    private TextView location;
    private TextView timeandlocation;
    private TextView hostInfo;
    private WebView  web;

    private Button registerButton;
    private OnlineHelper onlineHelper = new OnlineHelper();

    /**
     * Constructor
     */
    public AnnouncementDetailServerFragment() {
        // Required empty public constructor
    }

    /**
     * Get the data from the server & intent and set the corresponding view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_announcement_detail_server, container, false);

        // get the intent
        Intent intent = getActivity().getIntent();
        announcement_id = intent.getExtras().getInt("ANNOUNCEMENT_ID_EXTRA");
        announcement_title = intent.getExtras().getString("ANNOUNCEMENT_TITLE_EXTRA");
        announcement_summary = intent.getExtras().getString("ANNOUNCEMENT_SUMMARY_EXTRA");
        announcement_eventTime = intent.getExtras().getString("ANNOUNCEMENT_EVENTTIME_EXTRA");
        announcement_locationName = intent.getExtras().getString("ANNOUNCEMENT_LOCATIONNAME_EXTRA");
        announcement_locationDesc = intent.getExtras().getString("ANNOUNCEMENT_LOCATIONDESC_EXTRA");
        announcement_foodProvided = intent.getExtras().getBoolean("ANNOUNCEMENT_FOODPROVIDED_EXTRA");
        announcement_hostOrganization = intent.getExtras().getString("ANNOUNCEMENT_HOSTORGANIZATION_EXTRA");
        announcement_hostEmail = intent.getExtras().getString("ANNOUNCEMENT_HOSTEMAIL_EXTRA");
        announcement_isEvent = Boolean.parseBoolean(intent.getExtras().getString("ANNOUNCEMENT_ISEVENT_EXTRA"));

        // make http request to the server to get announcement detail
        if (onlineHelper.isOnline(getActivity())) {
            AnnouncementDetailServerFragment.MyTask myTask = new AnnouncementDetailServerFragment.MyTask();
            Log.d("server111", announcement_id + "onCreateView: ");
            Log.d("url", getString(R.string.api_url) + String.valueOf(announcement_id));
            HttpRequestPackage httpRequestPackage = new HttpRequestPackage();
            httpRequestPackage.setUri(getString(R.string.api_url) + "/" + String.valueOf(announcement_id));
            httpRequestPackage.setMethod("GET");
            myTask.execute(httpRequestPackage);
        } else {
            Toast.makeText(getContext(), "Network is not available.", Toast.LENGTH_LONG).show();
        }

        // get the corresponding view
        title = (TextView) fragmentLayout.findViewById(R.id.viewServerAnnouncementTitle);
        location = (TextView) fragmentLayout.findViewById(R.id.viewServerAnnouncementLocation);
        timeandlocation = (TextView) fragmentLayout.findViewById(R.id.viewServerAnnouncementTimeAndLocation);
        hostInfo = (TextView) fragmentLayout.findViewById(R.id.viewServerAnnouncementHostInfo);
        web = (WebView) fragmentLayout.findViewById(R.id.serverDetailWeb);
        registerButton = (Button) fragmentLayout.findViewById(R.id.serverRegisterButton);

        // set the content in the detail
        title.setText(announcement_title);
        timeandlocation.setText("Time: " + announcement_eventTime);
        hostInfo.setText("Organizer: " + announcement_hostOrganization);
        location.setText("Location: "+ announcement_locationName + " " + announcement_locationDesc);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        // set different layout for different announcement type
        if(!announcement_isEvent) {
            timeandlocation.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
        }else {
            try {
                Date date1 = df.parse(announcement_eventTime);
                Date date2 = df.parse(formattedDate);
                if(date2.getTime() > date1.getTime()) {
                    registerButton.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // confirm dialog
        buildConfirmDialog();
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmDialogObject.show();
            }
        });

        return fragmentLayout;
    }

    /**
     * Build a confirm dialog for register. Positive is confirm and negative is cancel
     */
    private void buildConfirmDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Are you sure?");
        confirmBuilder.setMessage("Are you sure you want to register this event?");
        confirmBuilder.setPositiveButton("confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                if(prefs.getString("firstName", "").length() == 0) {
                    Toast.makeText(getContext(), "Please set your information in setting page. Otherwise you cannot register an event", Toast.LENGTH_LONG).show();
                }else {
                    // store information to local database
                    AnnouncementDatabaseAdapter databaseAdapter = new AnnouncementDatabaseAdapter(getActivity().getBaseContext());
                    databaseAdapter.open();
                    int foodProvided;
                    if (announcement_foodProvided) {
                        foodProvided = 1;
                    } else {
                        foodProvided = 0;
                    }
                    databaseAdapter.createAnnouncement(announcement_id, announcement_title, announcement_summary,
                            announcement_eventTime, announcement_locationName, announcement_locationDesc,
                            foodProvided, announcement_hostOrganization, announcement_hostEmail);
                    databaseAdapter.close();

                    // send user info to server
                    AnnouncementDetailServerFragment.MyRegister myRegister = new AnnouncementDetailServerFragment.MyRegister(getContext());
                    Log.d("server111", announcement_id + "onCreateView: ");
                    Log.d("url", getString(R.string.api_url) + "/" + String.valueOf(announcement_id) + "attend");
                    HttpRequestPackage httpRequestPackage = new HttpRequestPackage();
                    httpRequestPackage.setUri(getString(R.string.api_url) + "/" + String.valueOf(announcement_id) + "/attend");
                    httpRequestPackage.setMethod("POST");
                    httpRequestPackage.setFirstName(prefs.getString("firstName", ""));
                    Log.d("firstname", prefs.getString("firstName", ""));
                    httpRequestPackage.setLastName(prefs.getString("lastName", ""));
                    httpRequestPackage.setOrganization(prefs.getString("organization", ""));
                    httpRequestPackage.setEmail(prefs.getString("email", ""));
                    httpRequestPackage.setPhone(prefs.getString("phoneNumber", ""));
                    myRegister.execute(httpRequestPackage);
                    getActivity().finish();
                }
            }
        });

        confirmBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        confirmDialogObject = confirmBuilder.create();
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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    announcement_detail = jsonObject.getString("detail");

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                });
                WebSettings webSettings = web.getSettings();
                webSettings.setJavaScriptEnabled(true);
                web.setInitialScale(150);
                String mime = "text/html";
                String encoding = "utf-8";
                web.loadData(announcement_detail,mime,encoding);
                Toast.makeText(getContext(), "Loading complete.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getContext(), "Server is not available.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * task of background thread for register button to send data to server
     */
    private class MyRegister extends AsyncTask<HttpRequestPackage, String, String> {
        private Context mContext;

        public MyRegister(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(HttpRequestPackage... params) {
            String response = HttpController.postAttendantInfo(params[0]);
            if (response == null) {
                publishProgress(response);
                Log.d("has response", "can't open url");
            } else {
                Log.d("register status:", response);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(mContext, "Server is not available.", Toast.LENGTH_LONG).show();
        }

    }
}
