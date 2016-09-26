package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverNearest {
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("duration")
    @Expose
    private int duration;

    /**
     *
     * @return
     * The distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

}