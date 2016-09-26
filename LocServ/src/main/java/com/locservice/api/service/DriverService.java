package com.locservice.api.service;

import com.locservice.api.entities.DriverCoordinates;
import com.locservice.api.entities.DriverData;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.request.DriverCoordRequest;
import com.locservice.api.request.DriverInfoRequest;
import com.locservice.api.request.GetDriversRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface DriverService {

    @POST("/")
    Call<DriverData> GetDrivers(@Body GetDriversRequest body);

    @POST("/")
    Call<DriverInfo> GetDriverInfo(@Body DriverInfoRequest body);

    @POST("/")
    Call<DriverCoordinates> GetDriverCoordinates(@Body DriverCoordRequest body);
}
