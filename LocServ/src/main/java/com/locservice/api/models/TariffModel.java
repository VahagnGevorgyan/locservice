package com.locservice.api.models;

import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.TariffData;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.api.service.TariffService;

import retrofit2.Call;
import retrofit2.Callback;

public class TariffModel {

	/**
	 * Method for getting tariff file
	 * version
	 * @param link - tariff list link
	 * @param cb - response callback
	 */
	public void GetTariffList(String link, Callback<TariffData> cb) {
		TariffService tariffService = ServiceGenerator.createService(TariffService.class, ApiHostType.API_NO_URL, link);
		Call<TariffData> tariffDataCall = tariffService.GetTariffList(link);
		tariffDataCall.enqueue(cb);
	}

}
