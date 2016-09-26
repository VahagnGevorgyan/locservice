package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 04 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetOrdersData extends APIError {

    @SerializedName("orders")
    @Expose
    private List<OrderIdItem> orders = new ArrayList<OrderIdItem>();

    /**
     *
     * @return
     * The orders
     */
    public List<OrderIdItem> getOrders() {
        return orders;
    }

    /**
     *
     * @param orders
     * The orders
     */
    public void setOrders(List<OrderIdItem> orders) {
        this.orders = orders;
    }
}
