package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 19 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatAddCommentData extends APIError {

    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("msg")
    @Expose
    private String msg;

    /**
     *
     * @return
     * The result
     */
    public int getResult() {
        return result;
    }

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
     * The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param msg
     * The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
