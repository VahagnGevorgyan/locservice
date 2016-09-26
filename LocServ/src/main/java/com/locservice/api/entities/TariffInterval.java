package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffInterval {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type_by_date")
    @Expose
    private String type_by_date;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc_static")
    @Expose
    private String descStatic;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("prepaid_time")
    @Expose
    private String prepaid_time;



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
     * The type_by_date
     */
    public String getType_by_date() {
        return type_by_date;
    }

    /**
     *
     * @param type_by_date
     * The is_night
     */
    public void setType_by_date(String type_by_date) {
        this.type_by_date = type_by_date;
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
     * The descStatic
     */
    public String getDescStatic() {
        return descStatic;
    }

    /**
     *
     * @param descStatic
     * The desc_static
     */
    public void setDescStatic(String descStatic) {
        this.descStatic = descStatic;
    }

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
     * The prepaid_time
     */
    public String getPrepaid_time() {
        return prepaid_time;
    }

    /**
     *
     * @param prepaid_time
     * The prepaid_time
     */
    public void setPrepaid_time(String prepaid_time) {
        this.prepaid_time = prepaid_time;
    }

}
