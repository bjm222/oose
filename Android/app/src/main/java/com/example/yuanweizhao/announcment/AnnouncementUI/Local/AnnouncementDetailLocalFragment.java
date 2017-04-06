package com.example.yuanweizhao.announcment.AnnouncementUI.Local;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * This is the local announcement detail fragment
 */
public class AnnouncementDetailLocalFragment extends Fragment {
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

    private TextView title;
    private TextView location;
    private TextView timeandlocation;
    private TextView hostInfo;
    private WebView  web;

    private Button deleteButton;
    private OnlineHelper onlineHelper = new OnlineHelper();

    /**
     * Constructor
     */
    public AnnouncementDetailLocalFragment() {
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
        View fragmentLayout = inflater.inflate(R.layout.fragment_announcement_detail_local, container, false);
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

        // make http request to the server to get announcement detail
        if (onlineHelper.isOnline(getActivity())) {
            AnnouncementDetailLocalFragment.MyTask myTask = new AnnouncementDetailLocalFragment.MyTask();
            Log.d("server111", intent.getExtras().getInt("ANNOUNCEMENT_ID_EXTRA") + "onCreateView: ");
            HttpRequestPackage httpRequestPackage = new HttpRequestPackage();
            httpRequestPackage.setUri(getString(R.string.api_url) + "/" + String.valueOf(announcement_id));
            httpRequestPackage.setMethod("GET");
            myTask.execute(httpRequestPackage);
        } else {
            Toast.makeText(getContext(), "Network is not available.", Toast.LENGTH_LONG).show();
        }

        // get the corresponding view
        title = (TextView) fragmentLayout.findViewById(R.id.viewLocalAnnouncementTitle);
        location = (TextView) fragmentLayout.findViewById(R.id.viewLocalAnnouncementLocation);
        timeandlocation = (TextView) fragmentLayout.findViewById(R.id.viewLocalAnnouncementTimeAndLocation);
        hostInfo = (TextView) fragmentLayout.findViewById(R.id.viewLocalAnnouncementHostInfo);
        web = (WebView) fragmentLayout.findViewById(R.id.localDetailWeb);
        deleteButton = (Button) fragmentLayout.findViewById(R.id.localDeleteButton);

        // set the content in the detail
        title.setText(announcement_title);
        timeandlocation.setText("Time: " + announcement_eventTime);
        hostInfo.setText("Organizer: " + announcement_hostOrganization);
        location.setText("Location: "+ announcement_locationName + " " + announcement_locationDesc);

        // confirm dialog
        buildConfirmDialog();
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmDialogObject.show();
            }
        });

        return fragmentLayout;
    }

    /**
     * Build a confirm dialog for delete. Positive is confirm and negative is cancel
     */
    private void buildConfirmDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Are you sure?");
        confirmBuilder.setMessage("Are you sure you want to delete this event?");
        confirmBuilder.setPositiveButton("confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i){
                AnnouncementDatabaseAdapter databaseAdapter = new AnnouncementDatabaseAdapter(getActivity().getBaseContext());
                databaseAdapter.open();
                databaseAdapter.deleteAnnouncement(announcement_id);
                databaseAdapter.close();

                getActivity().finish();
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
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
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
}
