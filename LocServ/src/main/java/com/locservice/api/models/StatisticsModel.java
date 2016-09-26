package com.locservice.api.models;

import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.Statistics;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.api.service.StatisticsService;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class StatisticsModel {

    private StatisticsService statisticsService;

    public StatisticsModel() {
        statisticsService = ServiceGenerator.createService(StatisticsService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting user statistics
     * @param body - request body
     * @param cb - response callback
     */
    public void GetStatistics(WebRequest body, Callback<Statistics> cb) {
        Call<Statistics> statisticsCall = statisticsService.GetStatistics(body);
        statisticsCall.enqueue(cb);

    } // and method AddNewClient

}
