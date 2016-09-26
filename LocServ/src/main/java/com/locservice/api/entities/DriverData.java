package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverData {

    @SerializedName("drivers")
    @Expose
    private List<Driver> drivers = new ArrayList<Driver>();
    @SerializedName("nearest")
    @Expose
    private DriverNearest nearest;

    /**
     *
     * @return
     * The drivers
     */
    public List<Driver> getDrivers() {
        return drivers;
    }

    /**
     *
     * @param drivers
     * The drivers
     */
    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    /**
     *
     * @return
     * The nearest
     */
    public DriverNearest getNearest() {
        return nearest;
    }

    /**
     *
     * @param nearest
     * The nearest
     */
    public void setNearest(DriverNearest nearest) {
        this.nearest = nearest;
    }

}
