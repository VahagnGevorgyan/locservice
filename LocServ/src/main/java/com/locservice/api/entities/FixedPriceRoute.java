package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FixedPriceRoute {

    private String id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("from")
    @Expose
    private PriceFrom from;
    @SerializedName("to")
    @Expose
    private PriceTo to;

    public Tariff tariff;


    /**
     *
     * @return
     * The price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The from
     */
    public PriceFrom getFrom() {
        return from;
    }

    /**
     *
     * @param from
     * The from
     */
    public void setFrom(PriceFrom from) {
        this.from = from;
    }

    /**
     *
     * @return
     * The to
     */
    public PriceTo getTo() {
        return to;
    }

    /**
     *
     * @param to
     * The to
     */
    public void setTo(PriceTo to) {
        this.to = to;
    }

}
