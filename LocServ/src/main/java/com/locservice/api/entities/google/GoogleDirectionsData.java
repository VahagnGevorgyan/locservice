package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 25 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleDirectionsData {

    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GoogleGeocodedWaypoint> geocodedWaypoints = new ArrayList<GoogleGeocodedWaypoint>();
    @SerializedName("routes")
    @Expose
    private List<GoogleRoute> routes = new ArrayList<GoogleRoute>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The geocodedWaypoints
     */
    public List<GoogleGeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    /**
     *
     * @param geocodedWaypoints
     * The geocoded_waypoints
     */
    public void setGeocodedWaypoints(List<GoogleGeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    /**
     *
     * @return
     * The routes
     */
    public List<GoogleRoute> getRoutes() {
        return routes;
    }

    /**
     *
     * @param routes
     * The routes
     */
    public void setRoutes(List<GoogleRoute> routes) {
        this.routes = routes;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
