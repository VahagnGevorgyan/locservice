package com.locservice.api.models;

import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.CheckfilesData;
import com.locservice.api.entities.CountryData;
import com.locservice.api.request.CheckfilesChangesRequest;
import com.locservice.api.service.PlaceService;
import com.locservice.api.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PlaceModel {

    private PlaceService placeBaseUrlService;

    public PlaceModel() {
        placeBaseUrlService = ServiceGenerator.createService(PlaceService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting countries/tariffs/landmarks
     *      links from server
     * @param body - request body
     * @param cb - response callback
     */
    public void CheckFilesChanges(CheckfilesChangesRequest body, Callback<CheckfilesData> cb) {
        Call<CheckfilesData> checkFilesDataCall = placeBaseUrlService.CheckFilesChanges(body);
        checkFilesDataCall.enqueue(cb);
    }

    /**
     * Method for getting countries list by file link
     * @param link - countries file link
     * @param cb - response callback
     */
    public void GetCountriesList(String link, Callback<CountryData> cb) {
        PlaceService placeNoUrlService = ServiceGenerator.createService(PlaceService.class, ApiHostType.API_NO_URL, link);
        Call<CountryData> countryDataCall = placeNoUrlService.GetCountriesList(link);
        countryDataCall.enqueue(cb);
    }
}
