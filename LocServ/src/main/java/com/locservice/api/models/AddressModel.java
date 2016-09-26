package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.LastAddressData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.LastAdressesRequest;
import com.locservice.api.request.RemoveAddressRequest;
import com.locservice.api.service.AddressService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 18 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddressModel {

    private static final String TAG = AddressModel.class.getSimpleName();

    private AddressService addressService;

    public AddressModel() {
        addressService = ServiceGenerator.createService(AddressService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting last addresses
     * @param body - request body
     * @param cb - response callback
     */
    public void GetLastAddresses (LastAdressesRequest body, Callback<LastAddressData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddressModel.GetLastAddresses : body : " + body + " cb : " + cb);

        Call<LastAddressData> lastAddressDataCall = addressService.GetLastAddresses(body);
        lastAddressDataCall.enqueue(cb);

    } // end method GetLastAddresses

    /**
     * Method for Removing last addresses
     * @param body - request body
     * @param cb - response callback
     */
    public void RemoveAddress (RemoveAddressRequest body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddressModel.RemoveAddress : body : " + body + " cb : " + cb);

        Call<ResultData> lastAddressDataCall = addressService.RemoveAddress(body);
        lastAddressDataCall.enqueue(cb);

    } // end method RemoveAddress

}
