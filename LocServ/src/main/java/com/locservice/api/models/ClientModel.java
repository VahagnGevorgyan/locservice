package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.RequestUpdateClientInfo;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ClientService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by Vahagn Gevorgyan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ClientModel {

    private static final String TAG = ClientModel.class.getSimpleName();

    private ClientService clientService;

    public ClientModel() {
        clientService = ServiceGenerator.createService(ClientService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting client info
     * @param body - request body
     * @param cb - response callback
     */
    public void GetClientInfo (WebRequest body, Callback<ClientInfo> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverModel.GetClientInfo : body : " + body + " cb : " + cb);

        Call<ClientInfo> clientInfoCall = clientService.GetClientInfo(body);
        clientInfoCall.enqueue(cb);

    } // end method GetClientInfo

    /**
     * Method for updating client info
     * @param body - request body
     * @param cb - response callback
     */
    public void UpdateClientInfo (RequestUpdateClientInfo body, Callback<ResultData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverModel.UpdateClientInfo : body : " + body + " cb : " + cb);

        Call<ResultData> clientInfoCall = clientService.UpdateClientInfo(body);
        clientInfoCall.enqueue(cb);

    } // end method UpdateClientInfo

}
