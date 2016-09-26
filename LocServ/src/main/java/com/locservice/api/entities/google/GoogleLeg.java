package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleLeg {

    @SerializedName("distance")
    @Expose
    private GoogleDistance distance;
    @SerializedName("duration")
    @Expose
    private GoogleDuration duration;
    @SerializedName("end_address")
    @Expose
    private String endAddress;
    @SerializedName("end_location")
    @Expose
    private GoogleEndLocation endLocation;
    @SerializedName("start_address")
    @Expose
    private String startAddress;
    @SerializedName("start_location")
    @Expose
    private GoogleStartLocation startLocation;
    @SerializedName("steps")
    @Expose
    private List<GoogleStep> steps = new ArrayList<GoogleStep>();
    @SerializedName("via_waypoint")
    @Expose
    private List<Object> viaWaypoint = new ArrayList<Object>();

    /**
     *
     * @return
     * The distance
     */
    public GoogleDistance getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(GoogleDistance distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The duration
     */
    public GoogleDuration getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(GoogleDuration duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The endAddress
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     *
     * @param endAddress
     * The end_address
     */
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    /**
     *
     * @return
     * The endLocation
     */
    public GoogleEndLocation getEndLocation() {
        return endLocation;
    }

    /**
     *
     * @param endLocation
     * The end_location
     */
    public void setEndLocation(GoogleEndLocation endLocation) {
        this.endLocation = endLocation;
    }

    /**
     *
     * @return
     * The startAddress
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     *
     * @param startAddress
     * The start_address
     */
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    /**
     *
     * @return
     * The startLocation
     */
    public GoogleStartLocation getStartLocation() {
        return startLocation;
    }

    /**
     *
     * @param startLocation
     * The start_location
     */
    public void setStartLocation(GoogleStartLocation startLocation) {
        this.startLocation = startLocation;
    }

    /**
     *
     * @return
     * The steps
     */
    public List<GoogleStep> getSteps() {
        return steps;
    }

    /**
     *
     * @param steps
     * The steps
     */
    public void setSteps(List<GoogleStep> steps) {
        this.steps = steps;
    }

    /**
     *
     * @return
     * The viaWaypoint
     */
    public List<Object> getViaWaypoint() {
        return viaWaypoint;
    }

    /**
     *
     * @param viaWaypoint
     * The via_waypoint
     */
    public void setViaWaypoint(List<Object> viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }

}
