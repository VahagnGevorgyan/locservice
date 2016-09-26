package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Driver extends APIError {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lt")
    @Expose
    private String lt;
    @SerializedName("ln")
    @Expose
    private String ln;
    @SerializedName("direction")
    @Expose
    private int direction;
    @SerializedName("CarColorCode")
    @Expose
    private String CarColorCode;
    @SerializedName("car_type")
    @Expose
    private String carType;

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
     * The lt
     */
    public String getLt() {
        return lt;
    }

    /**
     *
     * @param lt
     * The lt
     */
    public void setLt(String lt) {
        this.lt = lt;
    }

    /**
     *
     * @return
     * The ln
     */
    public String getLn() {
        return ln;
    }

    /**
     *
     * @param ln
     * The ln
     */
    public void setLn(String ln) {
        this.ln = ln;
    }

    /**
     *
     * @return
     * The direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     *
     * @param direction
     * The direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     *
     * @return
     * The CarColorCode
     */
    public String getCarColorCode() {
        return CarColorCode;
    }

    /**
     *
     * @param CarColorCode
     * The CarColorCode
     */
    public void setCarColorCode(String CarColorCode) {
        this.CarColorCode = CarColorCode;
    }

    /**
     *
     * @return
     * The carType
     */
    public String getCarType() {
        return carType;
    }

    /**
     *
     * @param carType
     * The car_type
     */
    public void setCarType(String carType) {
        this.carType = carType;
    }
}
