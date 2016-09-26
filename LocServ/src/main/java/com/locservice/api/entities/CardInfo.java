package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 27 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CardInfo {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("cardholder")
    @Expose
    private String cardholder;
    @SerializedName("expiration")
    @Expose
    private String expiration;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The pan
     */
    public String getPan() {
        return pan;
    }

    /**
     *
     * @param pan
     * The pan
     */
    public void setPan(String pan) {
        this.pan = pan;
    }

    /**
     *
     * @return
     * The cardholder
     */
    public String getCardholder() {
        return cardholder;
    }

    /**
     *
     * @param cardholder
     * The cardholder
     */
    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    /**
     *
     * @return
     * The expiration
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     *
     * @param expiration
     * The expiration
     */
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    /**
     *
     * @return
     * The bank
     */
    public String getBank() {
        return bank;
    }

    /**
     *
     * @param bank
     * The bank
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     *
     * @return
     * The color
     */
    public String getColor() {
        return color;
    }

    /**
     *
     * @param color
     * The color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }
}
