package com.locservice.api.service;

import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.RequestUpdateClientInfo;
import com.locservice.api.request.WebRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface ClientService {

    @POST("/")
    Call<ClientInfo> GetClientInfo(@Body WebRequest body);

    @POST("/")
    Call<ResultData> UpdateClientInfo(@Body RequestUpdateClientInfo body);

}
