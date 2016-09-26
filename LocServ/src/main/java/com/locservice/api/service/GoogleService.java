package com.locservice.api.service;


import com.locservice.api.entities.google.GoogleAutocompleteData;
import com.locservice.api.entities.google.GoogleDirectionsData;
import com.locservice.api.entities.google.GoogleDistanceMatrixData;
import com.locservice.api.entities.google.GoogleGeocode;
import com.locservice.api.entities.google.GooglePlaceData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vahagn Gevorgyan
 * 25 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface GoogleService {

    @GET("/maps/api/directions/json")
    Call<GoogleDirectionsData> GetDirections(@Query("origin") String origin,
                                             @Query("destination") String destination,
                                             @Query("units") String units,
                                             @Query("mode") String mode,
                                             @Query("sensor") boolean sensor,
                                             @Query("key") String key,
                                             @Query("language") String language);

    @GET("/maps/api/distancematrix/json")
    Call<GoogleDistanceMatrixData> GetDistanceMatrix(@Query("origins") String origins,
                                                     @Query("destinations") String destinations,
                                                     @Query("units") String units,
                                                     @Query("mode") String mode,
                                                     @Query("language") String language,
                                                     @Query("departure_time") String departure_time,
                                                     @Query("key") String key);

    @GET("/maps/api/geocode/json")
    Call<GoogleGeocode> GetGeocode(@Query("latlng") String latlng ,
                                   @Query("language") String language,
                                   @Query("key") String key);

    @GET("/maps/api/place/autocomplete/json")
    Call<GoogleAutocompleteData> GetAutocomplete(@Query("input") String input ,
                                                 @Query("language") String language,
                                                 @Query("components") String components,
                                                 @Query("types") String types,
                                                 @Query("location") String location,
                                                 @Query("radius") String radius,
                                                 @Query("key") String key);

    @GET("/maps/api/geocode/json")
    Call<GooglePlaceData> GetPlace(@Query("placeid") String placeid,
                                     @Query("key") String key);
}
