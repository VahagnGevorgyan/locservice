package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleStep {

    @SerializedName("distance")
    @Expose
    private GoogleDistance distance;
    @SerializedName("duration")
    @Expose
    private GoogleDuration duration;
    @SerializedName("end_location")
    @Expose
    private GoogleEndLocation endLocation;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("polyline")
    @Expose
    private GooglePolyline polyline;
    @SerializedName("start_location")
    @Expose
    private GoogleStartLocation startLocation;
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;

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
     * The htmlInstructions
     */
    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    /**
     *
     * @param htmlInstructions
     * The html_instructions
     */
    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    /**
     *
     * @return
     * The polyline
     */
    public GooglePolyline getPolyline() {
        return polyline;
    }

    /**
     *
     * @param polyline
     * The polyline
     */
    public void setPolyline(GooglePolyline polyline) {
        this.polyline = polyline;
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
     * The travelMode
     */
    public String getTravelMode() {
        return travelMode;
    }

    /**
     *
     * @param travelMode
     * The travel_mode
     */
    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    /**
     *
     * @return
     * The maneuver
     */
    public String getManeuver() {
        return maneuver;
    }

    /**
     *
     * @param maneuver
     * The maneuver
     */
    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

}
