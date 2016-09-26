package com.locservice.api.models;

import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.LandmarkData;
import com.locservice.api.entities.PolygonData;
import com.locservice.api.service.LandmarkService;
import com.locservice.api.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LandmarkModel {

    private LandmarkService landmarkService;

    /**
     * Method for getting landmark file
     * version
     * @param link  - link of URL
     * @param cb    - callback method
     */
    public void GetLandmarkList(String link, Callback<LandmarkData> cb) {
        landmarkService = ServiceGenerator.createService(LandmarkService.class, ApiHostType.API_NO_URL, link);
        Call<LandmarkData> landmarkDataCall = landmarkService.GetLandmarkList(link);
        landmarkDataCall.enqueue(cb);
    }

    /**
     * Method for getting airport polygon file
     * version
     * @param link  - link of URL
     * @param cb    - callback method
     */
    public void GetAirportPolygon(String link, Callback<PolygonData> cb) {
        landmarkService = ServiceGenerator.createService(LandmarkService.class, ApiHostType.API_NO_URL, link);
        Call<PolygonData> polygonDataCall = landmarkService.GetAirportPolygon(link);
        polygonDataCall.enqueue(cb);
    }

}
