package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 15 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PriceFrom {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private Object name;

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
    public Object getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(Object name) {
        this.name = name;
    }

}
