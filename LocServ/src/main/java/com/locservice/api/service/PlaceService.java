package com.locservice.api.service;

import com.locservice.api.entities.CheckfilesData;
import com.locservice.api.entities.CountryData;
import com.locservice.api.request.CheckfilesChangesRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface PlaceService {

    @POST("/")
    Call<CheckfilesData> CheckFilesChanges(@Body CheckfilesChangesRequest body);

    @GET
    Call<CountryData> GetCountriesList(@Url String fileUrl);
}
