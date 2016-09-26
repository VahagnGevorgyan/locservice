package com.locservice.api.service;

import com.locservice.api.entities.TariffData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Vahagn Gevorgyan
 * 17 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public interface TariffService {

	@GET
	Call<TariffData> GetTariffList(@Url String fileUrl);

}
