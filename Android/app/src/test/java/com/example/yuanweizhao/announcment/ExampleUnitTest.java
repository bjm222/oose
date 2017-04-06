package com.example.yuanweizhao.announcment;

import com.example.yuanweizhao.announcment.DataModel.Announcement;
import com.example.yuanweizhao.announcment.Map.myMapFragment;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parseJSON_isCorrect() throws Exception {
        String strJson=" { " +
                "\"Announcement\" :[" +
                "{" +
                "\"announcementID\":\"01\"," +
                "\"title\":\"Google\"," +
                "\"content\":\"YAY\"," +
                "\"icon\":\"10\","+
                "\"latitude\":\"39.326738\","+
                "\"longitude\":\"-76.620987\","+
                "\"locationName\":\"Hackerman\""+
                "}, " +
                "{" +
                "\"announcementID\":\"02\"," +
                "\"title\":\"FaceBook\"," +
                "\"content\":\"YAY1\"," +
                "\"icon\":\"11\","+
                "\"latitude\":\"39.327120\","+
                "\"longitude\":\"-76.620686\","+
                "\"locationName\":\"Barton\""+
                "} " +
                "]" +
                "}";
        myMapFragment mapFragment = new myMapFragment();
        AnnouncementUI.AnnouncementServerFragment announcementServerFrag = new AnnouncementUI.AnnouncementServerFragment();
        ArrayList<Announcement> result = mapFragment.parseJSON(strJson);
        assertNotNull(result);
        result = announcementServerFrag.parseJSON(strJson);
        assertNotNull(result);

        String strJson1=" { " +
                "\"Announcement\" :[" +
                "{" +
                "\"announcementID\":\"01\"," +
                "\"title\":\"Google\"," +
                "\"content\":\"YAY\"," +
                "\"icon\":\"10\""+
                "} ," +
                "{" +
                "\"announcementID\":\"02\"," +
                "\"title\":\"FaceBook\"," +
                "\"content\":\"YAY1\"," +
                "\"icon\":\"11\""+
                "} " +
                "]" +
                "}";
        result = mapFragment.parseJSON(strJson1);
        assertNull(result);
        result = announcementServerFrag.parseJSON(strJson1);
        assertNull(result);
    }




}