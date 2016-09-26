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
public class GoogleDistanceMatrixData {

    @SerializedName("destination_addresses")
    @Expose
    private List<String> destinationAddresses = new ArrayList<String>();
    @SerializedName("origin_addresses")
    @Expose
    private List<String> originAddresses = new ArrayList<String>();
    @SerializedName("rows")
    @Expose
    private List<GoogleRow> rows = new ArrayList<GoogleRow>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The destinationAddresses
     */
    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    /**
     *
     * @param destinationAddresses
     * The destination_addresses
     */
    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    /**
     *
     * @return
     * The originAddresses
     */
    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    /**
     *
     * @param originAddresses
     * The origin_addresses
     */
    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    /**
     *
     * @return
     * The rows
     */
    public List<GoogleRow> getRows() {
        return rows;
    }

    /**
     *
     * @param rows
     * The rows
     */
    public void setRows(List<GoogleRow> rows) {
        this.rows = rows;
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
