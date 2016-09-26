package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 25 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleDistance {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private int value;

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The value
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(int value) {
        this.value = value;
    }
}
