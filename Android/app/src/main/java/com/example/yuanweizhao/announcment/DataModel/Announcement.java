package com.example.yuanweizhao.announcment.DataModel;

/**
 * Announcement class, This is the data model for our whole application
 */

public class Announcement {
    private int announcement_id;
    private String announcement_title;
    private String announcement_summary;
    private String announcement_detail;
    private String announcement_pushDate;
    private boolean announcement_isEvent;
    private String announcement_eventTime;
    private int announcement_location;
    private String announcement_locationName;
    private String announcement_locationDesc;
    private double announcement_locationLat;
    private double announcement_locationLng;
    private boolean announcement_foodProvided;
    private String announcement_hostFirstName;
    private String announcement_hostLastName;
    private String announcement_hostOrganization;
    private String announcement_hostEmail;
    private String announcement_hostPhone;

    /**
     * constructor for announcement without announcement_detail and location
     *
     * @param announcement_hostPhone
     * @param announcement_title
     * @param announcement_summary
     * @param announcement_pushDate
     * @param announcement_isEvent
     * @param announcement_eventTime
     * @param announcement_locationName
     * @param announcement_locationDesc
     * @param announcement_locationLat
     * @param announcement_locationLng
     * @param announcement_foodProvided
     * @param announcement_hostFirstName
     * @param announcement_hostLastName
     * @param announcement_hostOrganization
     * @param announcement_hostEmail
     * @param announcement_id
     */
    public Announcement(String announcement_hostPhone, String announcement_title, String announcement_summary,
                        String announcement_pushDate, boolean announcement_isEvent, String announcement_eventTime,
                        String announcement_locationName, String announcement_locationDesc, double announcement_locationLat,
                        double announcement_locationLng, boolean announcement_foodProvided, String announcement_hostFirstName,
                        String announcement_hostLastName, String announcement_hostOrganization, String announcement_hostEmail,
                        int announcement_id) {
        this.announcement_hostPhone = announcement_hostPhone;
        this.announcement_title = announcement_title;
        this.announcement_summary = announcement_summary;
        this.announcement_pushDate = announcement_pushDate;
        this.announcement_isEvent = announcement_isEvent;
        this.announcement_eventTime = announcement_eventTime;
        this.announcement_locationName = announcement_locationName;
        this.announcement_locationDesc = announcement_locationDesc;
        this.announcement_locationLat = announcement_locationLat;
        this.announcement_locationLng = announcement_locationLng;
        this.announcement_foodProvided = announcement_foodProvided;
        this.announcement_hostFirstName = announcement_hostFirstName;
        this.announcement_hostLastName = announcement_hostLastName;
        this.announcement_hostOrganization = announcement_hostOrganization;
        this.announcement_hostEmail = announcement_hostEmail;
        this.announcement_id = announcement_id;
    }
    /**
     * constructor for announcement in database
     *
     * @param announcement_id
     * @param announcement_title
     * @param announcement_summary
     * @param announcement_eventTime
     * @param announcement_locationName
     * @param announcement_locationDesc
     * @param announcement_foodProvided
     * @param announcement_hostOrganization
     * @param announcement_hostEmail
     */
    public Announcement(int announcement_id, String announcement_title, String announcement_summary,
                        String announcement_eventTime, String announcement_locationName, String announcement_locationDesc,
                        boolean announcement_foodProvided, String announcement_hostOrganization, String announcement_hostEmail) {
        this.announcement_id = announcement_id;
        this.announcement_title = announcement_title;
        this.announcement_summary = announcement_summary;
        this.announcement_eventTime = announcement_eventTime;
        this.announcement_locationName = announcement_locationName;
        this.announcement_locationDesc = announcement_locationDesc;
        this.announcement_foodProvided = announcement_foodProvided;
        this.announcement_hostOrganization = announcement_hostOrganization;
        this.announcement_hostEmail = announcement_hostEmail;
    }


    public int getAnnouncement_id() {
        return announcement_id;
    }

    public String getAnnouncement_title() {
        return announcement_title;
    }

    public String getAnnouncement_summary() {
        return announcement_summary;
    }

    public String getAnnouncement_detail() {
        return announcement_detail;
    }

    public String getAnnouncement_pushDate() {
        return announcement_pushDate;
    }

    public boolean isAnnouncement_isEvent() {
        return announcement_isEvent;
    }

    public String getAnnouncement_eventTime() {
        return announcement_eventTime;
    }

    public int getAnnouncement_location() {
        return announcement_location;
    }

    public String getAnnouncement_locationName() {
        return announcement_locationName;
    }

    public String getAnnouncement_locationDesc() {
        return announcement_locationDesc;
    }

    public double getAnnouncement_locationLat() {
        return announcement_locationLat;
    }

    public double getAnnouncement_locationLng() {
        return announcement_locationLng;
    }

    public boolean isAnnouncement_foodProvided() {
        return announcement_foodProvided;
    }

    public String getAnnouncement_hostFirstName() {
        return announcement_hostFirstName;
    }

    public String getAnnouncement_hostLastName() {
        return announcement_hostLastName;
    }

    public String getAnnouncement_hostOrganization() {
        return announcement_hostOrganization;
    }

    public String getAnnouncement_hostEmail() {
        return announcement_hostEmail;
    }

    public String getAnnouncement_hostPhone() {
        return announcement_hostPhone;
    }
}
