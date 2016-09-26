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
public class GoogleRoute {

    @SerializedName("bounds")
    @Expose
    private GoogleBounds bounds;
    @SerializedName("copyrights")
    @Expose
    private String copyrights;
    @SerializedName("legs")
    @Expose
    private List<GoogleLeg> legs = new ArrayList<GoogleLeg>();
    @SerializedName("overview_polyline")
    @Expose
    private GoogleOverviewPolyline overviewPolyline;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("warnings")
    @Expose
    private List<Object> warnings = new ArrayList<Object>();
    @SerializedName("waypoint_order")
    @Expose
    private List<Object> waypointOrder = new ArrayList<Object>();

    /**
     *
     * @return
     * The bounds
     */
    public GoogleBounds getBounds() {
        return bounds;
    }

    /**
     *
     * @param bounds
     * The bounds
     */
    public void setBounds(GoogleBounds bounds) {
        this.bounds = bounds;
    }

    /**
     *
     * @return
     * The copyrights
     */
    public String getCopyrights() {
        return copyrights;
    }

    /**
     *
     * @param copyrights
     * The copyrights
     */
    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    /**
     *
     * @return
     * The legs
     */
    public List<GoogleLeg> getLegs() {
        return legs;
    }

    /**
     *
     * @param legs
     * The legs
     */
    public void setLegs(List<GoogleLeg> legs) {
        this.legs = legs;
    }

    /**
     *
     * @return
     * The overviewPolyline
     */
    public GoogleOverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    /**
     *
     * @param overviewPolyline
     * The overview_polyline
     */
    public void setOverviewPolyline(GoogleOverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    /**
     *
     * @return
     * The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     *
     * @param summary
     * The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     *
     * @return
     * The warnings
     */
    public List<Object> getWarnings() {
        return warnings;
    }

    /**
     *
     * @param warnings
     * The warnings
     */
    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    /**
     *
     * @return
     * The waypointOrder
     */
    public List<Object> getWaypointOrder() {
        return waypointOrder;
    }

    /**
     *
     * @param waypointOrder
     * The waypoint_order
     */
    public void setWaypointOrder(List<Object> waypointOrder) {
        this.waypointOrder = waypointOrder;
    }

}
