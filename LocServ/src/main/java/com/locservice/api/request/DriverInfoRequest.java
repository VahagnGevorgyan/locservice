package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverInfoRequest extends WebRequest {

    private String IdOrder;
    private String id; //Driver id
    

    public DriverInfoRequest(String IdOrder, String id) {
        super("getdriverinfo");
        this.IdOrder = IdOrder;
        this.id = id;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
