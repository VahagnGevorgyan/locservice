package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 29 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PrepareCancelOrderData extends APIError {

    @SerializedName("is_paid")
    @Expose
    private int isPaid;
    @SerializedName("cancel_text")
    @Expose
    private String cancelText;

    /**
     *
     * @return
     * The isPaid
     */
    public int getIsPaid() {
        return isPaid;
    }

    /**
     *
     * @param isPaid
     * The is_paid
     */
    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    /**
     *
     * @return
     * The cancelText
     */
    public String getCancelText() {
        return cancelText;
    }

    /**
     *
     * @param cancelText
     * The cancel_text
     */
    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

}
