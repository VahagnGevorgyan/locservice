package com.locservice.api.request;

import com.locservice.application.LocServicePreferences;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RemoveFavoriteRequest extends WebRequest {

    private String phone;
    private String id;

    public RemoveFavoriteRequest(String id) {
        super("removefavorite");
        this.phone = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PHONE_NUMBER.key(), "");
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
