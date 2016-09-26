package com.locservice.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderTrackRequest extends WebRequest {

    @SerializedName("IdOrder")
    @Expose
    private String mOrderId;

    public OrderTrackRequest(String method, String orderId) {
        super(method);
        mOrderId = orderId;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }
}
