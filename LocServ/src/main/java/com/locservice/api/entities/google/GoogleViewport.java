package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleViewport {

    @SerializedName("northeast")
    @Expose
    private GoogleNortheast northeast;
    @SerializedName("southwest")
    @Expose
    private GoogleSouthwest southwest;

    /**
     *
     * @return
     * The northeast
     */
    public GoogleNortheast getNortheast() {
        return northeast;
    }

    /**
     *
     * @param northeast
     * The northeast
     */
    public void setNortheast(GoogleNortheast northeast) {
        this.northeast = northeast;
    }

    /**
     *
     * @return
     * The southwest
     */
    public GoogleSouthwest getSouthwest() {
        return southwest;
    }

    /**
     *
     * @param southwest
     * The southwest
     */
    public void setSouthwest(GoogleSouthwest southwest) {
        this.southwest = southwest;
    }
}
