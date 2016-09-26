package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 18 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class NewOrderData extends APIError {

    @SerializedName("IdOrder")
    @Expose
    private String IdOrder;
    @SerializedName("order_exist")
    @Expose
    private int orderExist;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String sub_title;


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
     * The orderExist
     */
    public int getOrderExist() {
        return orderExist;
    }

    /**
     *
     * @param orderExist
     * The order_exist
     */
    public void setOrderExist(int orderExist) {
        this.orderExist = orderExist;
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

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The sub_title
     */
    public String getSub_title() {
        return sub_title;
    }

    /**
     *
     * @param sub_title
     * The sub_title
     */
    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

}
