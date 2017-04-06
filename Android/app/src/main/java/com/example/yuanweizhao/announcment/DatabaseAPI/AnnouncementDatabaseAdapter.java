package com.example.yuanweizhao.announcment.DatabaseAPI;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yuanweizhao.announcment.DataModel.Announcement;

import java.util.ArrayList;

/**
 * database API to store data locally by SQLite
 */

public class AnnouncementDatabaseAdapter {

    private static final String DATABASE_NAME = "announcement.db";
    private static final int DATABASE_VERSION = 1;

    public static final String ANNOUNCEMENT_TABLE = "announcement";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ANNOUNCEMENT_ID = "announcement_id";
    public static final String COLUMN_ANNOUNCEMENT_TITLE = "announcement_title";
    public static final String COLUMN_ANNOUNCEMENT_SUMMARY = "announcement_summary";
    public static final String COLUMN_ANNOUNCEMENT_EVENTTIME = "announcement_eventTime";
    public static final String COLUMN_ANNOUNCEMENT_LOCATIONNAME = "announcement_locationName";
    public static final String COLUMN_ANNOUNCEMENT_LOCATIONDESC = "announcement_locationDesc";
    public static final String COLUMN_ANNOUNCEMENT_FOODPROVIDED = "announcement_foodProvided";
    public static final String COLUMN_ANNOUNCEMENT_HOSTORGANIZATION = "announcement_hostOrganization";
    public static final String COLUMN_ANNOUNCEMENT_HOSTEMAIL = "announcement_hostEmail";

    private String[] allColumns = {COLUMN_ID, COLUMN_ANNOUNCEMENT_ID, COLUMN_ANNOUNCEMENT_TITLE,
            COLUMN_ANNOUNCEMENT_SUMMARY, COLUMN_ANNOUNCEMENT_EVENTTIME, COLUMN_ANNOUNCEMENT_LOCATIONNAME,
            COLUMN_ANNOUNCEMENT_LOCATIONDESC, COLUMN_ANNOUNCEMENT_FOODPROVIDED, COLUMN_ANNOUNCEMENT_HOSTORGANIZATION,
            COLUMN_ANNOUNCEMENT_HOSTEMAIL};

    public static final String CREATE_TABLE_ANNOUNCEMENT = "create table " + ANNOUNCEMENT_TABLE + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ANNOUNCEMENT_ID + " integer, "
            + COLUMN_ANNOUNCEMENT_TITLE + " text not null, "
            + COLUMN_ANNOUNCEMENT_SUMMARY + " text not null, "
            + COLUMN_ANNOUNCEMENT_EVENTTIME + " text not null, "
            + COLUMN_ANNOUNCEMENT_LOCATIONNAME + " text not null, "
            + COLUMN_ANNOUNCEMENT_LOCATIONDESC + " text not null, "
            + COLUMN_ANNOUNCEMENT_FOODPROVIDED + " integer, "
            + COLUMN_ANNOUNCEMENT_HOSTORGANIZATION + " text not null, "
            + COLUMN_ANNOUNCEMENT_HOSTEMAIL + " text not null "
            + ")";
    private SQLiteDatabase sqlDB;
    private Context context;

    private AnnouncementDatabaseHelper announcementDatabaseHelper;

    /**
     * set context
     *
     * @param context
     */
    public AnnouncementDatabaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * open the database
     *
     * @return AnnouncementDatabaseAdapter
     * @throws android.database.SQLException
     */
    public AnnouncementDatabaseAdapter open() throws android.database.SQLException {
        announcementDatabaseHelper = new AnnouncementDatabaseHelper(context);
        sqlDB = announcementDatabaseHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close the database
     */
    public void close() {
        announcementDatabaseHelper.close();
    }

    /**
     * create Announcement and insert it in the database
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
     * @return Announcement
     */
    public Announcement createAnnouncement(int announcement_id, String announcement_title, String announcement_summary,
                                           String announcement_eventTime, String announcement_locationName, String announcement_locationDesc,
                                           int announcement_foodProvided, String announcement_hostOrganization, String announcement_hostEmail) {
        deleteAnnouncement(announcement_id);
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANNOUNCEMENT_ID, announcement_id);
        values.put(COLUMN_ANNOUNCEMENT_TITLE, announcement_title);
        values.put(COLUMN_ANNOUNCEMENT_SUMMARY, announcement_summary);
        values.put(COLUMN_ANNOUNCEMENT_EVENTTIME, announcement_eventTime);
        values.put(COLUMN_ANNOUNCEMENT_LOCATIONNAME, announcement_locationName);
        values.put(COLUMN_ANNOUNCEMENT_LOCATIONDESC, announcement_locationDesc);
        values.put(COLUMN_ANNOUNCEMENT_FOODPROVIDED, announcement_foodProvided);
        values.put(COLUMN_ANNOUNCEMENT_HOSTORGANIZATION, announcement_hostOrganization);
        values.put(COLUMN_ANNOUNCEMENT_HOSTEMAIL, announcement_hostEmail);

        long insertID = sqlDB.insert(ANNOUNCEMENT_TABLE, null, values);
        System.out.println(insertID);
        Cursor cursor = sqlDB.query(ANNOUNCEMENT_TABLE, allColumns, COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        Announcement newAnnouncement = cursorToAnnouncement(cursor);
        cursor.close();
        return newAnnouncement;
    }

    /**
     * delete announcement in db
     *
     * @param announcementIdToDelete
     * @return
     */
    public long deleteAnnouncement(int announcementIdToDelete) {
        return sqlDB.delete(ANNOUNCEMENT_TABLE, COLUMN_ANNOUNCEMENT_ID + " = " + announcementIdToDelete, null);
    }

    /**
     * get upcoming registered events
     *
     * @return ArrayList<Announcement>
     */
    public ArrayList<Announcement> getUpcomingRegisteredEvents() {
        ArrayList<Announcement> announcements = new ArrayList<>();

        Cursor cursor = sqlDB.query(ANNOUNCEMENT_TABLE, allColumns, COLUMN_ANNOUNCEMENT_EVENTTIME + " >= " +  "(DATETIME('now', 'localtime'))", null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Announcement announcement = cursorToAnnouncement(cursor);
            announcements.add(announcement);
        }

        cursor.close();
        return announcements;
    }

    /**
     * get past registered events
     *
     * @return ArrayList<Announcement>
     */
    public ArrayList<Announcement> getPastRegisteredEvents() {
        ArrayList<Announcement> announcements = new ArrayList<>();

        Cursor cursor = sqlDB.query(ANNOUNCEMENT_TABLE, allColumns, COLUMN_ANNOUNCEMENT_EVENTTIME + " < " +  "(DATETIME('now', 'localtime'))", null, null, null, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Announcement announcement = cursorToAnnouncement(cursor);
            announcements.add(announcement);
        }

        cursor.close();
        return announcements;
    }

    /**
     * Helper function for cursor to get the information
     *
     * @param cursor
     * @return Announcement
     */
    private Announcement cursorToAnnouncement(Cursor cursor) {
        boolean announcement_foodProvided;
        if (cursor.getInt(7) == 1) {
            announcement_foodProvided = true;
        } else {
            announcement_foodProvided = false;
        }
        return new Announcement(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), announcement_foodProvided, cursor.getString(8), cursor.getString(9));
    }

    /**
     * AnnouncementDatabaseHelper class
     */
    private static class AnnouncementDatabaseHelper extends SQLiteOpenHelper {

        AnnouncementDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            System.out.print("Testing");
            db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCEMENT_TABLE);
            db.execSQL(CREATE_TABLE_ANNOUNCEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int ondVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ANNOUNCEMENT_TABLE);
            onCreate(db);
        }
    }
}
