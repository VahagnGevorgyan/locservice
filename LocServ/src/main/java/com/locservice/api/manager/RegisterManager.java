package com.locservice.api.manager;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.locservice.BuildConfig;
import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.AuthData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.AddNewClientRequest;
import com.locservice.api.request.CheckPhoneRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 17 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RegisterManager extends WebManager {

    private static final String TAG = RegisterManager.class.getSimpleName();

    public RegisterManager(ICallBack context) {
        super(context);
    }


    /**
     * Method for adding new client
     *
     * @param phone         - phone number
     * @param company_id    - company id
     * @param deviceId      - device id
     */
    public void AddNewClient(String phone, int company_id, String deviceId) {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String os_version = Build.VERSION.RELEASE;
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterManager.AddNewClient : phone : " + phone
                + " : company_id : " + company_id + " : device_id : " + deviceId + " : brand : " + brand + " : model : " + model
                + " : os_version : " + os_version + " : verson : " + BuildConfig.VERSION_NAME);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : addnewclient : {\n"
                            + " phone : " + phone
                            + ",\n company_id : " + company_id
                            + ",\n deviceId : " + deviceId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getRegisterModel().AddNewClient(new AddNewClientRequest(phone, company_id, deviceId, brand, model, os_version),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_ADD_NEW_CLIENT));

    } // end method AddNewClient

    /**
     * Method for checking phone
     * @param phone - phone number
     * @param code - code
     */
    public void CheckPhone(String phone, String code, String deviceId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterManager.CheckPhone : phone : " + phone + " : code : " + code + " : deviceId : " + deviceId);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : checkphone : {\n"
                            + " phone : " + phone
                            + ",\n code : " + code
                            + ",\n deviceId : " + deviceId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getRegisterModel().CheckPhone(new CheckPhoneRequest(phone, deviceId, code, BuildConfig.VERSION_NAME),
                new RequestCallback<AuthData>(mContext, RequestTypes.REQUEST_CHECK_PHONE));

    } // end method CheckPhone
}
