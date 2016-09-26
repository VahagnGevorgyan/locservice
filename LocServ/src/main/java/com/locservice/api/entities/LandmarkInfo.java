package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LandmarkInfo {

    @SerializedName("districts")
    @Expose
    private List<District> districts = new ArrayList<District>();
    @SerializedName("railstations")
    @Expose
    private List<Railstation> railstations = new ArrayList<Railstation>();
    @SerializedName("airports")
    @Expose
    private List<Airport> airports = new ArrayList<Airport>();

    /**
     *
     * @return
     * The districts
     */
    public List<District> getDistricts() {
        return districts;
    }

    /**
     *
     * @param districts
     * The districts
     */
    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    /**
     *
     * @return
     * The railstations
     */
    public List<Railstation> getRailstations() {
        return railstations;
    }

    /**
     *
     * @param railstations
     * The railstations
     */
    public void setRailstations(List<Railstation> railstations) {
        this.railstations = railstations;
    }

    /**
     *
     * @return
     * The airports
     */
    public List<Airport> getAirports() {
        return airports;
    }

    /**
     *
     * @param airports
     * The airports
     */
    public void setAirports(List<Airport> airports) {
        this.airports = airports;
    }

}
