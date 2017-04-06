package com.example.yuanweizhao.announcment.AnnouncementUI.Server;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yuanweizhao.announcment.AnnouncementUI.AnnouncementAdapter;
import com.example.yuanweizhao.announcment.AnnouncementUI.AnnouncementDetailActivity;
import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.R;
import com.example.yuanweizhao.announcment.ServerAPI.HttpController;
import com.example.yuanweizhao.announcment.ServerAPI.HttpRequestPackage;
import com.example.yuanweizhao.announcment.ServerAPI.OnlineHelper;
import com.example.yuanweizhao.announcment.ServerAPI.ParseJSONHelper;

import java.util.ArrayList;

/**
 * This is the server announcement list fragment
 */
public class AnnouncementServerFragment extends ListFragment {
    ArrayList<Announcement> announcements;
    private String start_year, start_month, start_day, end_year, end_month, end_day;
    private boolean isPast = false;
    private OnlineHelper onlineHelper = new OnlineHelper();

    /**
     * get the date data from date picker from the bundle
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            start_year = Integer.toString(bundle.getInt("start_year"));
            start_month = addZeroToDate(Integer.toString(bundle.getInt("start_month")));
            start_day = addZeroToDate(Integer.toString(bundle.getInt("start_day")));
            end_year = Integer.toString(bundle.getInt("end_year"));
            end_month = addZeroToDate(Integer.toString(bundle.getInt("end_month")));
            end_day = addZeroToDate(Integer.toString(bundle.getInt("end_day")));
            isPast = bundle.getBoolean("isPast");
        }
    }

    /**
     * Get announcement data from remote server and set the layout with these data when create
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (onlineHelper.isOnline(getActivity())) {
            MyTask myTask = new MyTask();
            HttpRequestPackage httpRequestPackage = new HttpRequestPackage();
            if (isPast) {
                httpRequestPackage.setUri(getString(R.string.api_url) + "/bydate/" + start_year + "-" + start_month + "-" + start_day + "/" + end_year + "-" + end_month + "-" + end_day);
            } else {
                httpRequestPackage.setUri(getString(R.string.api_url));
            }
            httpRequestPackage.setMethod("GET");
            myTask.execute(httpRequestPackage);
        } else {
            Toast.makeText(getContext(), "Network is not available.", Toast.LENGTH_LONG).show();
        }
        Log.d("on activity created", "test");
    }

    /**
     * put necessary data into intent for detail fragment to use
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l,v,position,id);
        // put information into the intent and start the corresponding activity
        Announcement announcement = (Announcement) getListAdapter().getItem(position);
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
     * helper function to add 0 for month and day date when they are less than 10
     *
     * @param s
     * @return String
     */
    private String addZeroToDate(String s) {
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
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

                AnnouncementAdapter announcementAdapter;
                announcementAdapter = new AnnouncementAdapter(getActivity(), announcements);
                setListAdapter(announcementAdapter);
                getListView().setDivider(ContextCompat.getDrawable(getActivity(), android.R.color.black));
                getListView().setDividerHeight(5);

                Toast.makeText(getContext(), "Loading complete.", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getContext(), "Server is not available.", Toast.LENGTH_LONG).show();
        }
    }
}