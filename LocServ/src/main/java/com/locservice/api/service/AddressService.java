package com.locservice.api.service;


import com.locservice.api.entities.LastAddressData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.LastAdressesRequest;
import com.locservice.api.request.RemoveAddressRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 18 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface AddressService {

    @POST("/")
    Call<LastAddressData> GetLastAddresses(@Body LastAdressesRequest body);

    @POST("/")
    Call<ResultData> RemoveAddress(@Body RemoveAddressRequest body);

}
