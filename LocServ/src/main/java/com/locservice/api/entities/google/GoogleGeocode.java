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
public class GoogleGeocode {

    @SerializedName("results")
    @Expose
    private List<GoogleResult> results = new ArrayList<GoogleResult>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The results
     */
    public List<GoogleResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<GoogleResult> results) {
        this.results = results;
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
