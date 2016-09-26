package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 14 September 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleAutocompleteData {

    @SerializedName("predictions")
    @Expose
    private List<GooglePrediction> predictions = new ArrayList<GooglePrediction>();
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("error_message")
    @Expose
    private String error_message;

    /**
     *
     * @return
     * The predictions
     */
    public List<GooglePrediction> getPredictions() {
        return predictions;
    }

    /**
     *
     * @param predictions
     * The predictions
     */
    public void setPredictions(List<GooglePrediction> predictions) {
        this.predictions = predictions;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The error_message
     */
    public String getError_message() {
        return error_message;
    }

    /**
     *
     * @param error_message
     * The error_message
     */
    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
