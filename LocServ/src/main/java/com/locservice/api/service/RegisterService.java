package com.locservice.api.service;

import com.locservice.api.entities.AuthData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.AddNewClientRequest;
import com.locservice.api.request.CheckPhoneRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface RegisterService {

    @POST("/")
    Call<ResultData> AddNewClient(@Body AddNewClientRequest body);

    @POST("/")
    Call<AuthData> CheckPhone(@Body CheckPhoneRequest body);
}
