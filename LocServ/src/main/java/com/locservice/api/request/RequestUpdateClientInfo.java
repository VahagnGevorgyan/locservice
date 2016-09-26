package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 09 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RequestUpdateClientInfo extends WebRequest {

    private String name;
    private String email;
    private int sms_notification;
    private String photo; // max 2M
    private int only_filled;

    public RequestUpdateClientInfo(String name, String email,
                                   int sms_notification, String photo, int only_filled) {
        super("updateclientinfo");
        this.name = name;
        this.email = email;
        this.sms_notification = sms_notification;
        this.photo = photo;
        this.only_filled = only_filled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSms_notification() {
        return sms_notification;
    }

    public void setSms_notification(int sms_notification) {
        this.sms_notification = sms_notification;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getOnly_filled() {
        return only_filled;
    }

    public void setOnly_filled(int only_filled) {
        this.only_filled = only_filled;
    }
}
