package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 17 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ResultData extends APIError {

    @SerializedName("result")
    @Expose
    private String result;

    /**
     *
     * @param result
     * The result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The result
     */
    public String getResult() {
        return result;
    }
}
