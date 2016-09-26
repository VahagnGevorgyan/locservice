package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Vahagn Gevorgyan
 * 11 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffLink extends APIError {

    @SerializedName("cities_link")
    @Expose
    private String citiesLink;
    @SerializedName("cities_date")
    @Expose
    private int citiesDate;
    @SerializedName("landmarks_link")
    @Expose
    private String landmarksLink;
    @SerializedName("landmarks_date")
    @Expose
    private int landmarksDate;
    @SerializedName("tariffs_link")
    @Expose
    private String tariffsLink;
    @SerializedName("tariffs_date")
    @Expose
    private int tariffsDate;

    /**
     *
     * @return
     * The citiesLink
     */
    public String getCitiesLink() {
        return citiesLink;
    }

    /**
     *
     * @param citiesLink
     * The cities_link
     */
    public void setCitiesLink(String citiesLink) {
        this.citiesLink = citiesLink;
    }

    /**
     *
     * @return
     * The citiesDate
     */
    public int getCitiesDate() {
        return citiesDate;
    }

    /**
     *
     * @param citiesDate
     * The cities_date
     */
    public void setCitiesDate(int citiesDate) {
        this.citiesDate = citiesDate;
    }

    /**
     *
     * @return
     * The landmarksLink
     */
    public String getLandmarksLink() {
        return landmarksLink;
    }

    /**
     *
     * @param landmarksLink
     * The landmarks_link
     */
    public void setLandmarksLink(String landmarksLink) {
        this.landmarksLink = landmarksLink;
    }

    /**
     *
     * @return
     * The landmarksDate
     */
    public int getLandmarksDate() {
        return landmarksDate;
    }

    /**
     *
     * @param landmarksDate
     * The landmarks_date
     */
    public void setLandmarksDate(int landmarksDate) {
        this.landmarksDate = landmarksDate;
    }

    /**
     *
     * @return
     * The tariffsLink
     */
    public String getTariffsLink() {
        return tariffsLink;
    }

    /**
     *
     * @param tariffsLink
     * The tariffs_link
     */
    public void setTariffsLink(String tariffsLink) {
        this.tariffsLink = tariffsLink;
    }

    /**
     *
     * @return
     * The tariffsDate
     */
    public int getTariffsDate() {
        return tariffsDate;
    }

    /**
     *
     * @param tariffsDate
     * The tariffs_date
     */
    public void setTariffsDate(int tariffsDate) {
        this.tariffsDate = tariffsDate;
    }
}
