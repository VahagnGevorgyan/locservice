package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetOrderTrackData {

    @SerializedName("track")
    @Expose
    private List<List<String>> track;

    public List<List<String>> getTrack() {
        return track;
    }

    public void setTrack(List<List<String>> track) {
        this.track = track;
    }
}
