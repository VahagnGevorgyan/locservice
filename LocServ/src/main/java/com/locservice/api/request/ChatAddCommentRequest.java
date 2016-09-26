package com.locservice.api.request;

import android.os.Build;

import com.locservice.gcm.GCMUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vahagn Gevorgyan
 * 19 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatAddCommentRequest extends WebRequest {
    private String comment;
    private String device_info; // json {system: Android 2.4}
    private String token;

    public ChatAddCommentRequest(String comment) {
        super("addcomment");
        this.comment = comment;
        this.device_info = getAndroidDeviceInfo();
        this.token = GCMUtils.getRegistrationId();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAndroidDeviceInfo() {
        JSONObject jsonDeviceInfo = new JSONObject();
        try {
            jsonDeviceInfo.put("system", "Android " + Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonDeviceInfo.toString();
    }

}
