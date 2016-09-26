package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class City extends APIError {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("search_radius")
    @Expose
    private String searchRadius;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("gmt")
    @Expose
    private String gmt;
    @SerializedName("nouns")
    @Expose
    private String nouns;
    @SerializedName("is_default")
    @Expose
    private String isDefault;


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

    /**
     *
     * @return
     * The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The searchRadius
     */
    public String getSearchRadius() {
        return searchRadius;
    }

    /**
     *
     * @param searchRadius
     * The search_radius
     */
    public void setSearchRadius(String searchRadius) {
        this.searchRadius = searchRadius;
    }

    /**
     *
     * @return
     * The area
     */
    public String getArea() {
        return area;
    }

    /**
     *
     * @param area
     * The area
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     *
     * @return
     * The gmt
     */
    public String getGmt() {
        return gmt;
    }

    /**
     *
     * @param gmt
     * The gmt
     */
    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    /**
     *
     * @return
     * The nouns
     */
    public String getNouns() {
        return nouns;
    }

    /**
     *
     * @param nouns
     * The nouns
     */
    public void setNouns(String nouns) {
        this.nouns = nouns;
    }

    /**
     *
     * @return
     * The isDefault
     */
    public String getIsDefault() {
        return isDefault;
    }

    /**
     *
     * @param isDefault
     * The is_default
     */
    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}