package com.locservice.api.service;

import com.locservice.api.entities.BindCardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.request.BindCardRequest;
import com.locservice.api.request.CheckCardBindRequest;
import com.locservice.api.request.UnBindCardRequest;
import com.locservice.api.request.WebRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface CreditCardService {

    @POST("/")
    Call<BindCardInfo> BindCard(@Body BindCardRequest body);

    @POST("/")
    Call<ResultIntData> UnbindCard(@Body UnBindCardRequest body);

    @POST("/")
    Call<CardsData> GetCreditCards(@Body WebRequest body);

    @POST("/")
    Call<ResultIntData> CheckCardBind(@Body CheckCardBindRequest body);

    @FormUrlEncoded
    @POST("/{link}")
    Call<String> CardAuthStart(@Path("link") String link, @Field("MD") String MD, @Field("PaReq") String PaReq, @Field("TermUrl") String TermUrl);

}
