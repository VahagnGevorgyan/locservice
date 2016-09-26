package com.locservice.api.service;

import com.locservice.api.entities.BonusInfo;
import com.locservice.api.entities.RepostSocialData;
import com.locservice.api.request.RepostSocialRequest;
import com.locservice.api.request.WebRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface BonusService {

    @POST("/")
    Call<BonusInfo> GetBonusesInfo(@Body WebRequest body);

    @POST("/")
    Call<RepostSocialData> RepostSocial(@Body RepostSocialRequest body);
}
