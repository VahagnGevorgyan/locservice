package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleMatchedSubstring {

    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("offset")
    @Expose
    private int offset;

    /**
     *
     * @return
     * The length
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param length
     * The length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     *
     * @return
     * The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     *
     * @param offset
     * The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
