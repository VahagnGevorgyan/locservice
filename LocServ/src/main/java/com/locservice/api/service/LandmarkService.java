package com.locservice.api.service;

import com.locservice.api.entities.LandmarkData;
import com.locservice.api.entities.PolygonData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface LandmarkService {

    @GET
    Call<LandmarkData> GetLandmarkList(@Url String fileUrl);

    @GET
    Call<PolygonData> GetAirportPolygon(@Url String fileUrl);

}

