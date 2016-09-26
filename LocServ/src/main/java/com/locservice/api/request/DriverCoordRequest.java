package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 10 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverCoordRequest extends WebRequest {

    private String IdOrder;

    public DriverCoordRequest(String IdOrder) {
        super("getdrivercoordinates");
        this.IdOrder = IdOrder;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }
}
