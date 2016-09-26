package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AuthData extends APIError {

    @SerializedName("result")
    @Expose
    private int result;

    @SerializedName("auth_token")
    @Expose
    private String auth_token;


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


    /**
     *
     * @param auth_token
     * The auth_token
     */
    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    /**
     *
     * @return
     * The auth_token
     */
    public String getAuth_token() {
        return auth_token;
    }

}


