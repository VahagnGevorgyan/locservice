package com.locservice.api.models;

import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.AuthData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.AddNewClientRequest;
import com.locservice.api.request.CheckPhoneRequest;
import com.locservice.api.service.RegisterService;
import com.locservice.api.service.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 17 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RegisterModel {

    private RegisterService registerService;

    public RegisterModel() {
        registerService = ServiceGenerator.createService(RegisterService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for adding new client
     * @param body - request body
     * @param cb - response callback
     */
    public void AddNewClient(AddNewClientRequest body, Callback<ResultData> cb) {
        Call<ResultData> resultDataCall = registerService.AddNewClient(body);
        resultDataCall.enqueue(cb);

    } // and method AddNewClient

    /**
     * Method fo chacking phone
     * @param body - request body
     * @param cb - response callback
     */
    public void CheckPhone(CheckPhoneRequest body, Callback<AuthData> cb) {
        Call<AuthData> authDataCall = registerService.CheckPhone(body);
        authDataCall.enqueue(cb);

    } // and method CheckPhone
}
