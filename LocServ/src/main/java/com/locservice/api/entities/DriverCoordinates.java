package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 11 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverCoordinates extends APIError {
    @SerializedName("DriverID")
    @Expose
    private String DriverID;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("direction")
    @Expose
    private int direction;

    /**
     *
     * @return
     * The DriverID
     */
    public String getDriverID() {
        return DriverID;
    }

    /**
     *
     * @param DriverID
     * The DriverID
     */
    public void setDriverID(String DriverID) {
        this.DriverID = DriverID;
    }

    /**
     *
     * @return
     * The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     *
     * @param direction
     * The direction
     */

    public void setDirection(int direction) {
        this.direction = direction;
    }
}