package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Country {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("phone_prefix")
    @Expose
    private String phonePrefix;
    @SerializedName("phone_length")
    @Expose
    private String phoneLength;
    @SerializedName("id_currency")
    @Expose
    private String idCurrency;
    @SerializedName("map_type")
    @Expose
    private String mapType;
    @SerializedName("is_default")
    @Expose
    private String isDefault;
    @SerializedName("currency_name")
    @Expose
    private String currencyName;
    @SerializedName("currency_short_name")
    @Expose
    private String currencyShortName;
    @SerializedName("currency_name_iso")
    @Expose
    private String currencyNameIso;
    @SerializedName("cities")
    @Expose
    private List<City> cities = new ArrayList<City>();

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
     * The shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     *
     * @param shortName
     * The short_name
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     *
     * @return
     * The longName
     */
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @param longName
     * The long_name
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The phonePrefix
     */
    public String getPhonePrefix() {
        return phonePrefix;
    }

    /**
     *
     * @param phonePrefix
     * The phone_prefix
     */
    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    /**
     *
     * @return
     * The phoneLength
     */
    public String getPhoneLength() {
        return phoneLength;
    }

    /**
     *
     * @param phoneLength
     * The phone_length
     */
    public void setPhoneLength(String phoneLength) {
        this.phoneLength = phoneLength;
    }

    /**
     *
     * @return
     * The idCurrency
     */
    public String getIdCurrency() {
        return idCurrency;
    }

    /**
     *
     * @param idCurrency
     * The id_currency
     */
    public void setIdCurrency(String idCurrency) {
        this.idCurrency = idCurrency;
    }

    /**
     *
     * @return
     * The mapType
     */
    public String getMapType() {
        return mapType;
    }

    /**
     *
     * @param mapType
     * The map_type
     */
    public void setMapType(String mapType) {
        this.mapType = mapType;
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

    /**
     *
     * @return
     * The currencyName
     */
    public String getCurrencyName() {
        return currencyName;
    }

    /**
     *
     * @param currencyName
     * The currency_name
     */
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    /**
     *
     * @return
     * The currencyShortName
     */
    public String getCurrencyShortName() {
        return currencyShortName;
    }

    /**
     *
     * @param currencyShortName
     * The currency_short_name
     */
    public void setCurrencyShortName(String currencyShortName) {
        this.currencyShortName = currencyShortName;
    }

    /**
     *
     * @return
     * The currencyNameIso
     */
    public String getCurrencyNameIso() {
        return currencyNameIso;
    }

    /**
     *
     * @param currencyNameIso
     * The currency_name_iso
     */
    public void setCurrencyNameIso(String currencyNameIso) {
        this.currencyNameIso = currencyNameIso;
    }

    /**
     *
     * @return
     * The cities
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     *
     * @param cities
     * The cities
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

}