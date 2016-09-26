package com.locservice.api.service;

import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.entities.SetFavoriteData;
import com.locservice.api.request.GetFavoriteRequest;
import com.locservice.api.request.RemoveFavoriteRequest;
import com.locservice.api.request.SetFavoriteRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface FavoriteAddressService {

    @POST("/")
    Call<SetFavoriteData> SetFavorite(@Body SetFavoriteRequest body);

    @POST("/")
    Call<List<GetFavoriteData>> GetFavorite(@Body GetFavoriteRequest body);

    @POST("/")
    Call<ResultData> RemoveFavorite(@Body RemoveFavoriteRequest body);

}
