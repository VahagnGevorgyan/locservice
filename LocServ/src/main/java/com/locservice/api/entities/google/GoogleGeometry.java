package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleGeometry {

    @SerializedName("location")
    @Expose
    private GoogleLocation location;
    @SerializedName("location_type")
    @Expose
    private String locationType;
    @SerializedName("viewport")
    @Expose
    private GoogleViewport viewport;

    /**
     *
     * @return
     * The location
     */
    public GoogleLocation getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(GoogleLocation location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The locationType
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     *
     * @param locationType
     * The location_type
     */
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     *
     * @return
     * The viewport
     */
    public GoogleViewport getViewport() {
        return viewport;
    }

    /**
     *
     * @param viewport
     * The viewport
     */
    public void setViewport(GoogleViewport viewport) {
        this.viewport = viewport;
    }
}
