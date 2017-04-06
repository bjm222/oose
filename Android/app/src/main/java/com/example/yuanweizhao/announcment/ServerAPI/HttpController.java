package com.example.yuanweizhao.announcment.ServerAPI;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is http API to make connection, get and send data to remote server
 */

public class HttpController {

    /**
     * get announcements from the server
     *
     * @param httpRequestPackage
     * @return String the JSON announcement data
     */
    public static String getAllAnnouncements(HttpRequestPackage httpRequestPackage) {
        BufferedReader reader = null;
        String uri = httpRequestPackage.getUri();
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // send request
            con.setRequestMethod(httpRequestPackage.getMethod());

            // get response
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * post attendant information to the server
     *
     * @param httpRequestPackage
     * @return String the post status
     */
    public static String postAttendantInfo(HttpRequestPackage httpRequestPackage) {
        BufferedReader reader = null;
        String uri = httpRequestPackage.getUri();
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // send request & data
            con.setRequestMethod(httpRequestPackage.getMethod());
            JSONObject json = new JSONObject(httpRequestPackage.getUserInfo());
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(json.toString());
            writer.flush();

            // response code, 201 for successful for POST method
            String response;
            if (con.getResponseCode() == 201) {
                response = "register successful";
            } else {
                response = "register fail";
            }
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
