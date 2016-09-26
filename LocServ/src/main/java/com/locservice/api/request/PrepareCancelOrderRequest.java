package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 29 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PrepareCancelOrderRequest extends WebRequest {

    private String IdOrder;

    public PrepareCancelOrderRequest(String IdOrder) {
        super("prepareCancelOrder");
        this.IdOrder = IdOrder;
    }

    /**
     * Method for getting order id
     * @return - order id
     */
    public String getIdOrder() {
        return IdOrder;

    } // end method getIdOrder

    /**
     * Method for setting order id
     * @param idOrder - order id
     */
    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;

    } // end method setIdOrder
}
