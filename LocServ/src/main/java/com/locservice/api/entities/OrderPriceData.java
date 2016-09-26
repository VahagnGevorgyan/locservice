package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 6 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderPriceData {
    @SerializedName("price")
    @Expose
    private List<Integer> price = new ArrayList<Integer>();

    /**
     *
     * @return
     * The price
     */
    public List<Integer> getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(List<Integer> price) {
        this.price = price;
    }
}
