package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 29 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PolygonData {
    @SerializedName("data")
    @Expose
    private List<PolygonPoint> data = new ArrayList<PolygonPoint>();
    @SerializedName("airport")
    @Expose
    private int airport;
    @SerializedName("terminal")
    @Expose
    private int terminal;

    /**
     *
     * @return
     * The data
     */
    public List<PolygonPoint> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<PolygonPoint> data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The airport
     */
    public int getAirport() {
        return airport;
    }

    /**
     *
     * @param airport
     * The airport
     */
    public void setAirport(int airport) {
        this.airport = airport;
    }

    /**
     *
     * @return
     * The terminal
     */
    public int getTerminal() {
        return terminal;
    }

    /**
     *
     * @param terminal
     * The terminal
     */
    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

}