package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LandmarkData {

    @SerializedName("data")
    @Expose
    private LandmarkInfo data;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("IdLocality")
    @Expose
    private int IdLocality;

    /**
     *
     * @return
     * The data
     */
    public LandmarkInfo getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(LandmarkInfo data) {
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

    /**
     *
     * @return
     * The IdLocality
     */
    public int getIdLocality() {
        return IdLocality;
    }

    /**
     *
     * @param IdLocality
     * The IdLocality
     */
    public void setIdLocality(int IdLocality) {
        this.IdLocality = IdLocality;
    }

}