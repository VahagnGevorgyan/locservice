package com.locservice.api.request;

/**
 * Created by Hayk Yegoryan
 * 17 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CameOutRequest extends WebRequest {
    private String IdOrder;

    public CameOutRequest(String IdOrder) {
        super("cameout");
        this.IdOrder = IdOrder;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }
}
