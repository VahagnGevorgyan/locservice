package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 17 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderIdItem {
    @SerializedName("IdOrder")
    @Expose
    private String IdOrder;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The IdOrder
     */
    public String getIdOrder() {
        return IdOrder;
    }

    /**
     *
     * @param IdOrder
     * The IdOrder
     */
    public void setIdOrder(String IdOrder) {
        this.IdOrder = IdOrder;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}