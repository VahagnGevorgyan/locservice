package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.DriverCoordinates;
import com.locservice.api.entities.DriverData;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.request.DriverCoordRequest;
import com.locservice.api.request.DriverInfoRequest;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.api.service.DriverService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverModel {

    private static final String TAG = DriverModel.class.getSimpleName();

    private DriverService driverService;

    public DriverModel() {
        driverService = ServiceGenerator.createService(DriverService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting drivers list
     * @param body - request body
     * @param cb - response callback
     */
    public void GetDrivers(GetDriversRequest body, Callback<DriverData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverModel.GetDrivers : body : " + body + " cb : " + cb);

        Call<DriverData> driverDataCall = driverService.GetDrivers(body);
        driverDataCall.enqueue(cb);

    } //end method GetDrivers

    /**
    * Method for getting driver info
    * @param body - request body
    * @param cb - response callback
    */
    public void GetDriverInfo(DriverInfoRequest body, Callback<DriverInfo> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverModel.GetDriverInfo : body : " + body + " cb : " + cb);

        Call<DriverInfo> driverInfoCall = driverService.GetDriverInfo(body);
        driverInfoCall.enqueue(cb);

    } //end method GetDriverInfo

    /**
     * Method for getting driver coordinates
     * @param body - request body
     * @param cb - response callback
     */
    public void GetDriverCoordinates(DriverCoordRequest body, Callback<DriverCoordinates> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverModel.GetDriverCoordinates : body : " + body + " cb : " + cb);

        Call<DriverCoordinates> driverCoordinatesCall = driverService.GetDriverCoordinates(body);
        driverCoordinatesCall.enqueue(cb);

    } //end method GetDriverCoordinates
}
