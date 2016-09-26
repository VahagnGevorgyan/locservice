package com.locservice.api.service;

import com.locservice.api.entities.EnterCouponData;
import com.locservice.api.request.EnterCouponRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 19 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface EnterCouponService {

    @POST("/")
    Call<EnterCouponData> EnterCouponRequest(@Body EnterCouponRequest body);
}
