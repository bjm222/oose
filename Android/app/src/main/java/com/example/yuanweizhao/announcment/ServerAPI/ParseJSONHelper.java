package com.example.yuanweizhao.announcment.ServerAPI;

import com.example.yuanweizhao.announcment.DataModel.Announcement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is a helper function to parse JSON string
 */

public class ParseJSONHelper {

    /**
     * A parser function that convert JSON object to List of Announcement
     *
     * @param strJson The Json object
     * @return return a list of Announcement contains all announcements' information
     */
    public ArrayList<Announcement> parseJSON(String strJson) {
        ArrayList<Announcement> allAnnouncements = new ArrayList<Announcement>();

        try {
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = new JSONArray(strJson);

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // get the  JSON content with out detail field
                int announcement_id = jsonObject.getInt("id");
                String announcement_title = jsonObject.getString("title");
                String announcement_summary = jsonObject.getString("summary");
                String announcement_pushDate = jsonObject.getString("pushDate");
                boolean announcement_isEvent = jsonObject.getBoolean("isEvent");
                String announcement_eventTime = jsonObject.getString("eventTime");
                //int announcement_location = jsonObject.getInt("location");
                String announcement_locationName = jsonObject.getString("locationName");
                String announcement_locationDesc = jsonObject.getString("locationDesc");
                double announcement_locationLat = jsonObject.getDouble("locationLat");
                double announcement_locationLng = jsonObject.getDouble("locationLng");
                boolean announcement_foodProvided = jsonObject.getBoolean("foodProvided");
                String announcement_hostFirstName = jsonObject.getString("hostFirstName");
                String announcement_hostLastName = jsonObject.getString("hostLastName");
                String announcement_hostOrganization = jsonObject.getString("hostOrganization");
                String announcement_hostEmail = jsonObject.getString("hostEmail");
                String announcement_hostPhone = jsonObject.getString("hostPhone");

                allAnnouncements.add(new Announcement(announcement_hostPhone, announcement_title, announcement_summary,
                        announcement_pushDate, announcement_isEvent, announcement_eventTime, announcement_locationName,
                        announcement_locationDesc, announcement_locationLat, announcement_locationLng, announcement_foodProvided,
                        announcement_hostFirstName, announcement_hostLastName, announcement_hostOrganization, announcement_hostEmail,
                        announcement_id));
            }
            return allAnnouncements;
        } catch (JSONException e) {
            e.printStackTrace();
            return allAnnouncements;
        }
    }
}
