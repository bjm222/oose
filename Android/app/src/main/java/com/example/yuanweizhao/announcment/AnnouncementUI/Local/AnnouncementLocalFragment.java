package com.example.yuanweizhao.announcment.AnnouncementUI.Local;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.yuanweizhao.announcment.AnnouncementUI.AnnouncementAdapter;
import com.example.yuanweizhao.announcment.AnnouncementUI.AnnouncementDetailActivity;
import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.DatabaseAPI.AnnouncementDatabaseAdapter;
import com.example.yuanweizhao.announcment.MainActivity;

import java.util.ArrayList;

/**
 * This is the local announcement list fragment
 */
public class AnnouncementLocalFragment extends ListFragment {
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private AnnouncementAdapter announcementAdapter;

    /**
     * Get announcement data from local database and set the layout with these data when create
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // database manipulation when create
        getDataFromLocal();

        // apply the AnnouncementAdapter for layout format
        announcementAdapter = new AnnouncementAdapter(getActivity(),announcements);
        setListAdapter(announcementAdapter);

        // set the line between tow list item
        getListView().setDivider(ContextCompat.getDrawable(getActivity(),android.R.color.black));
        getListView().setDividerHeight(5);
    }

    /**
     * Get announcement data from local database and set the layout with these data when resume
     */
    @Override
    public void onResume() {
        super.onResume();
        // database manipulation when resume
        getDataFromLocal();
        announcementAdapter.notifyDataSetChanged();
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
        Log.d("Announcement ID", String.valueOf(announcement.getAnnouncement_id()));
        intent.putExtra("ANNOUNCEMENT_ID_EXTRA",announcement.getAnnouncement_id());
        intent.putExtra("ANNOUNCEMENT_TITLE_EXTRA",announcement.getAnnouncement_title());
        intent.putExtra("ANNOUNCEMENT_SUMMARY_EXTRA",announcement.getAnnouncement_summary());
        intent.putExtra("ANNOUNCEMENT_EVENTTIME_EXTRA",announcement.getAnnouncement_eventTime());
        intent.putExtra("ANNOUNCEMENT_LOCATIONNAME_EXTRA",announcement.getAnnouncement_locationName());
        intent.putExtra("ANNOUNCEMENT_LOCATIONDESC_EXTRA",announcement.getAnnouncement_locationDesc());
        intent.putExtra("ANNOUNCEMENT_FOODPROVIDED_EXTRA",announcement.isAnnouncement_foodProvided());
        intent.putExtra("ANNOUNCEMENT_HOSTORGANIZATION_EXTRA",announcement.getAnnouncement_hostOrganization());
        intent.putExtra("ANNOUNCEMENT_HOSTEMAIL_EXTRA",announcement.getAnnouncement_hostEmail());
        intent.putExtra("ANNOUNCEMENT_TYPE", "local");

        startActivity(intent);
    }

    /**
     * get data from the local database
     */
    private void getDataFromLocal() {
        // database manipulation
        AnnouncementDatabaseAdapter databaseAdapter = new AnnouncementDatabaseAdapter(getActivity().getBaseContext());
        databaseAdapter.open();
        announcements.clear();

        // to distinguish the up comings OR past events
        switch (MainActivity.getLocalAnnouncementType()) {
            case "Upcoming":
                // get upcoming registered events
                announcements.addAll(databaseAdapter.getUpcomingRegisteredEvents());
                break;
            case "Past":
                // get past registered events
                announcements.addAll(databaseAdapter.getPastRegisteredEvents());
                break;
        }
        databaseAdapter.close();
    }
}
