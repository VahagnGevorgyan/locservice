package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 21 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class APIError {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("errors")
    @Expose
    private List<String> errors;
    @SerializedName("add_info")
    @Expose
    private List<String> add_info;
    @SerializedName("message")
    @Expose
    private String message;


    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     *
     * @param errors
     * The types
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     *
     * @return
     * The add_info
     */
    public List<String> getAdd_info() {
        return add_info;
    }

    /**
     *
     * @param add_info
     * The add_info
     */
    public void setAdd_info(List<String> add_info) {
        this.add_info = add_info;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
