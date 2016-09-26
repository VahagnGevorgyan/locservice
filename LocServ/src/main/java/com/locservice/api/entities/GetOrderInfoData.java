package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetOrderInfoData extends APIError {

    @SerializedName("orders")
    @Expose
    private List<Order> orders = new ArrayList<Order>();

    /**
     *
     * @return
     * The orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     *
     * @param orders
     * The orders
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
