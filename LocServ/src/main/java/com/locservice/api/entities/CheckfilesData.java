package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CheckfilesData extends APIError {

    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("cities_link")
    @Expose
    private String cities_link;
    @SerializedName("cities_date")
    @Expose
    private int cities_date;
    @SerializedName("landmarks_link")
    @Expose
    private String landmarks_link;
    @SerializedName("landmarks_date")
    @Expose
    private int landmarks_date;
    @SerializedName("tariffs_link")
    @Expose
    private String tariffs_link;
    @SerializedName("tariffs_date")
    @Expose
    private int tariffs_date;


    /**
     *
     * @return
     * The citiesLink
     */
    public String getCitiesLink() {
        return cities_link;
    }

    /**
     *
     * @param citiesLink
     * The cities_link
     */
    public void setCitiesLink(String citiesLink) {
        this.cities_link = citiesLink;
    }

    /**
     *
     * @return
     * The citiesDate
     */
    public int getCitiesDate() {
        return cities_date;
    }

    /**
     *
     * @param citiesDate
     * The cities_date
     */
    public void setCitiesDate(int citiesDate) {
        this.cities_date = citiesDate;
    }

    /**
     *
     * @return
     * The landmarksLink
     */
    public String getLandmarksLink() {
        return landmarks_link;
    }

    /**
     *
     * @param landmarksLink
     * The landmarks_link
     */
    public void setLandmarksLink(String landmarksLink) {
        this.landmarks_link = landmarksLink;
    }

    /**
     *
     * @return
     * The landmarksDate
     */
    public int getLandmarksDate() {
        return landmarks_date;
    }

    /**
     *
     * @param landmarksDate
     * The landmarks_date
     */
    public void setLandmarksDate(int landmarksDate) {
        this.landmarks_date = landmarksDate;
    }

    /**
     *
     * @return
     * The tariffsLink
     */
    public String getTariffsLink() {
        return tariffs_link;
    }

    /**
     *
     * @param tariffsLink
     * The tariffs_link
     */
    public void setTariffsLink(String tariffsLink) {
        this.tariffs_link = tariffsLink;
    }

    /**
     *
     * @return
     * The tariffsDate
     */
    public int getTariffsDate() {
        return tariffs_date;
    }

    /**
     *
     * @param tariffsDate
     * The tariffs_date
     */
    public void setTariffsDate(int tariffsDate) {
        this.tariffs_date = tariffsDate;
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
     * The locale
     */
    public String getLocale() {
        return locale;
    }


}