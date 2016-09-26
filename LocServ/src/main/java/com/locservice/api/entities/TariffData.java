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
public class TariffData extends APIError {

    @SerializedName("data")
    @Expose
    private List<Tariff> data = new ArrayList<Tariff>();
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("IdLocality")
    @Expose
    private String id_locality;



    /**
     *
     * @return
     * The data
     */
    public List<Tariff> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<Tariff> data) {
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
     * The id_locality
     */
    public String getId_locality() {
        return id_locality;
    }

    /**
     *
     * @param id_locality
     * The id_locality
     */
    public void setId_locality(String id_locality) {
        this.id_locality = id_locality;
    }

}
