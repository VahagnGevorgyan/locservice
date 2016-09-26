package com.locservice.api.entities;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 18 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LastAddressData {

    @SerializedName("addresses")
    @Expose
    private List<LastAddress> addresses = new ArrayList<LastAddress>();
    @SerializedName("is_last")
    @Expose
    private int isLast;

    /**
     *
     * @return
     * The addresses
     */
    public List<LastAddress> getAddresses() {
        return addresses;
    }

    /**
     *
     * @param addresses
     * The addresses
     */
    public void setAddresses(List<LastAddress> addresses) {
        this.addresses = addresses;
    }

    /**
     *
     * @return
     * The isLast
     */
    public int getIsLast() {
        return isLast;
    }

    /**
     *
     * @param isLast
     * The is_last
     */
    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

}
