package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 19 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class EnterCouponData extends APIError {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("sum")
    @Expose
    private int sum;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("campain")
    @Expose
    private Campain campain;

    /**
     *
     * @return
     * The status
     */
    public int getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The sum
     */
    public int getSum() {
        return sum;
    }

    /**
     *
     * @param sum
     * The sum
     */
    public void setSum(int sum) {
        this.sum = sum;
    }

    /**
     *
     * @return
     * The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param msg
     * The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     *
     * @return
     * The campain
     */
    public Campain getCampain() {
        return campain;
    }

    /**
     *
     * @param campain
     * The campain
     */
    public void setCampain(Campain campain) {
        this.campain = campain;
    }

}
