package com.locservice.api.service;

import com.locservice.api.entities.Statistics;
import com.locservice.api.request.WebRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface StatisticsService {

    @POST("/")
    Call<Statistics> GetStatistics(@Body WebRequest body);
}
