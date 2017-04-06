package com.example.yuanweizhao.announcment.ServerAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a customized http request package when making http connection
 */

public class HttpRequestPackage {
    private String uri;
    private String method;
    private Map<String, String> userInfo = new HashMap<>();

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public Map<String, String> getUserInfo() {
        return userInfo;
    }
    public void setFirstName(String firstName) {
        userInfo.put("firstName", firstName);
    }
    public void setLastName(String lastName) {
        userInfo.put("lastName", lastName);
    }
    public void setOrganization(String organization) {
        userInfo.put("organization", organization);
    }
    public void setEmail(String email) {
        userInfo.put("email", email);
    }
    public void setPhone(String phone) {
        userInfo.put("phone", phone);
    }
}
