package com.locservice.api.request;

import com.locservice.application.LocServicePreferences;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class SetFavoriteRequest extends WebRequest {

    private String IdLocality;
    private String address;
    private String latitude;
    private String longitude;
    private String entrance;
    private String comment;
    private String name;
    private String id;

    public SetFavoriteRequest(String address, String latitude, String longitude, String entrance, String comment, String name, String id) {
        super("setfavorite");
        this.IdLocality = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.entrance = entrance;
        this.comment = comment;
        this.name = name;
        this.id = id;
    }

    public String getIdLocality() {
        return IdLocality;
    }

    public void setIdLocality(String idLocality) {
        IdLocality = idLocality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
