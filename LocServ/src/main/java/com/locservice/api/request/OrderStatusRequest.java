package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderStatusRequest extends WebRequest {

    private String IdOrdersList;

    public OrderStatusRequest() {
        super("getliststatus");
    }

    public OrderStatusRequest(String method, String IdOrdersList) {
        super(method);
        this.IdOrdersList = IdOrdersList;
    }

    public String getIdOrdersList() {
        return IdOrdersList;
    }

    public void setIdOrdersList(String idOrdersList) {
        IdOrdersList = idOrdersList;
    }
}
