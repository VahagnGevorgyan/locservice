package com.locservice.api.entities.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 30 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleElement {

    @SerializedName("distance")
    @Expose
    private GoogleDistance distance;
    @SerializedName("duration")
    @Expose
    private GoogleDuration duration;
    @SerializedName("duration_in_traffic")
    @Expose
    private GoogleDurationInTraffic durationInTraffic;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The distance
     */
    public GoogleDistance getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    public void setDistance(GoogleDistance distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The duration
     */
    public GoogleDuration getDuration() {
        return duration;
    }

    /**
     *
     * @param duration
     * The duration
     */
    public void setDuration(GoogleDuration duration) {
        this.duration = duration;
    }

    /**
     *
     * @return
     * The durationInTraffic
     */
    public GoogleDurationInTraffic getDurationInTraffic() {
        return durationInTraffic;
    }

    /**
     *
     * @param durationInTraffic
     * The duration_in_traffic
     */
    public void setDurationInTraffic(GoogleDurationInTraffic durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
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

}
