package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 06 July 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CheckOrderPay extends WebRequest {

    private String IdOrder;

    public CheckOrderPay(String IdOrder) {
        super("checkOrderPay");
        this.IdOrder = IdOrder;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }
}
