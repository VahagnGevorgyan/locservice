package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 15 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RequestGetOrdersInfo extends WebRequest {
    private String phone;
    private String IdOrdersList;

    public RequestGetOrdersInfo(String method, String phone, String IdOrdersList) {
        super(method);
        this.phone = phone;
        this.IdOrdersList = IdOrdersList;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdOrdersList() {
        return IdOrdersList;
    }

    public void setIdOrdersList(String idOrdersList) {
        IdOrdersList = idOrdersList;
    }
}
