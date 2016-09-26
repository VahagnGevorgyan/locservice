package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vahagn Gevorgyan
 * 26 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CountryData extends APIError {

    @SerializedName("data")
    @Expose
    private List<Country> data = new ArrayList<Country>();
    @SerializedName("locale")
    @Expose
    private String locale;

    /**
     *
     * @return
     * The data
     */
    public List<Country> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<Country> data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     * The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

}