package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 06 July 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PayOrderRequest extends WebRequest {

    private String IdOrder; //Required
    private int id_card;    //Required

    public PayOrderRequest(String IdOrder, int id_card) {
        super("payorder");
        this.IdOrder = IdOrder;
        this.id_card = id_card;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }

    public int getId_card() {
        return id_card;
    }

    public void setId_card(int id_card) {
        this.id_card = id_card;
    }
}
