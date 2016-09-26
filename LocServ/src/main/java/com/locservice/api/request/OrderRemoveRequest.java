package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 30 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderRemoveRequest extends WebRequest {

    private String IdOrder;

    public OrderRemoveRequest() {
        super("removeOrderFromHistory");
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }
}
