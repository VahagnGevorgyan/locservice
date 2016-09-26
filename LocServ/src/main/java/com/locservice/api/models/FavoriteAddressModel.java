package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.entities.SetFavoriteData;
import com.locservice.api.request.GetFavoriteRequest;
import com.locservice.api.request.RemoveFavoriteRequest;
import com.locservice.api.request.SetFavoriteRequest;
import com.locservice.api.service.FavoriteAddressService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FavoriteAddressModel {

    private static final String TAG = FavoriteAddressModel.class.getSimpleName();

    private FavoriteAddressService favoriteAddressService;

    public FavoriteAddressModel() {
        this.favoriteAddressService = ServiceGenerator.createService(FavoriteAddressService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for setting favorite address
     * @param body - request body
     * @param cb - response callback
     */
    public void SetFavorite(SetFavoriteRequest body, Callback<SetFavoriteData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressModel.SetFavorite : body : " + body + " cb : " + cb);

        Call<SetFavoriteData> setFavoriteDataCall = favoriteAddressService.SetFavorite(body);
        setFavoriteDataCall.enqueue(cb);

    } //end method SetFavorite

    /**
     * Method for getting favorite address
     * @param body - request body
     * @param cb - response callback
     */
    public void GetFavorite(GetFavoriteRequest body, Callback<List<GetFavoriteData>> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressModel.GetFavorite : body : " + body + " cb : " + cb);

        Call<List<GetFavoriteData>> listCall = favoriteAddressService.GetFavorite(body);
        listCall.enqueue(cb);

    } //end method SetFavorite

    /**
     * Method for removing favorite address
     * @param body - request body
     * @param cb - response callback
     */
    public void RemoveFavorite(RemoveFavoriteRequest body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressModel.RemoveFavorite : body : " + body + " cb : " + cb);

        Call<ResultData> resultDataCall = favoriteAddressService.RemoveFavorite(body);
        resultDataCall.enqueue(cb);

    } //end method RemoveFavorite
}
