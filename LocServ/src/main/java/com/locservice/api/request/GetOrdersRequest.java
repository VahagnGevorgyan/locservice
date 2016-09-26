package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 04 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetOrdersRequest extends WebRequest {

    private String phone;
    private int only_active;

    public GetOrdersRequest(String phone, int only_active) {
        super("getorders");
        this.phone = phone;
        this.only_active = only_active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
