package com.example.yuanweizhao.announcment.AnnouncementUI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.R;

import java.util.ArrayList;

/**
 * an adapter for announcement data stucture
 */

public class AnnouncementAdapter extends ArrayAdapter<Announcement>{

    /**
     * constructor
     *
     * @param context
     * @param announcements
     */
    public AnnouncementAdapter(Context context, ArrayList<Announcement> announcements){
        super(context, 0, announcements);
    }

    /**
     * get data from a single announcement and set the view for it
     *
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Announcement announcement = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.listItemTitle);
        TextView summary = (TextView) convertView.findViewById(R.id.listItemContent);
        ImageView icon = (ImageView) convertView.findViewById(R.id.listItemIcon);

        // Populate the data into the template view using the data object
        title.setText(announcement.getAnnouncement_title());
        summary.setText(announcement.getAnnouncement_summary());
        if (announcement.isAnnouncement_foodProvided()) {
            icon.setImageResource(R.drawable.food_icon);
            Log.d("announcadapter", "VISIBLE VISIBLE VISIBLE VISIBLE");
        } else {
            Log.d("announcadapter", "GONE GONE GONE GONE");
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
