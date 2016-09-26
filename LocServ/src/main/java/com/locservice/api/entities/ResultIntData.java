package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 27 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ResultIntData extends APIError {

    @SerializedName("result")
    @Expose
    private int result;

    /**
     *
     * @param result
     * The result
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The result
     */
    public int getResult() {
        return result;
    }

}
