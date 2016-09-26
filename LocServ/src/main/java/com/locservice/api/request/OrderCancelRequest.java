package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderCancelRequest extends WebRequest {

    private String IdOrder;

    public OrderCancelRequest(String IdOrder) {
        super("cancelorder");
        this.IdOrder = IdOrder;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }
}
