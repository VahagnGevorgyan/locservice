package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 19 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class EnterCouponRequest extends WebRequest {

    private int id_coupon;
    private String code;

    public EnterCouponRequest(int id_coupon, String code) {
        super("enterCoupon");
        this.id_coupon = id_coupon;
        this.code = code;
    }

    public int getId_coupon() {
        return id_coupon;
    }

    public void setId_coupon(int id_coupon) {
        this.id_coupon = id_coupon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
