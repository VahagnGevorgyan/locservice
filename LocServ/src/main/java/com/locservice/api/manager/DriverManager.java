package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.DriverCoordinates;
import com.locservice.api.entities.DriverData;
import com.locservice.api.entities.DriverInfo;
import com.locservice.api.request.DriverCoordRequest;
import com.locservice.api.request.DriverInfoRequest;
import com.locservice.api.request.GetDriversRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverManager extends WebManager {

    private static final String TAG = DriverManager.class.getSimpleName();

    public DriverManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting drivers list
     *
     * @param getDriversRequest - drivers request
     */
    public void GetDrivers (GetDriversRequest getDriversRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.GetDrivers : getDriversRequest : " + getDriversRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getdrivers : {\n"
                            + " latitude : "            + getDriversRequest.getLatitude()
                            + ",\n longitude : "        + getDriversRequest.getLongitude()
                            + ",\n radius : "           + getDriversRequest.getRadius()
                            + ",\n IdLocality : "       + getDriversRequest.getIdLocality()
                            + ",\n tariff : "           + getDriversRequest.getTariff()
                            + ",\n NoSmoking : "        + getDriversRequest.getNoSmoking()
                            + ",\n g_width : "          + getDriversRequest.getG_width()
                            + ",\n conditioner : "      + getDriversRequest.getConditioner()
                            + ",\n animal : "           + getDriversRequest.getAnimal()
                            + ",\n need_check : "       + getDriversRequest.getNeed_check()
                            + ",\n need_wifi : "        + getDriversRequest.getNeed_wifi()
                            + ",\n need_card : "        + getDriversRequest.getNeed_card()
                            + ",\n e_type : "           + getDriversRequest.getE_type()
                            + ",\n get_only_count : "   + getDriversRequest.getGet_only_count()
                            + ",\n baby_seat : "        + getDriversRequest.getBaby_seat()
                            + ",\n limit : "            + getDriversRequest.getLimit()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getDriverModel().GetDrivers(getDriversRequest,
                new RequestCallback<DriverData>(mContext, RequestTypes.REQUEST_GET_DRIVERS));

    } //end method GetDrivers

    /**
     * Method for getting drivers list from map
     *
     * @param getDriversRequest - drivers request
     */
    public void GetDriversFromMap (GetDriversRequest getDriversRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.GetDriversFromMap : getDriversRequest : " + getDriversRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getdrivers : {\n"
                            + " latitude : "            + getDriversRequest.getLatitude()
                            + ",\n longitude : "        + getDriversRequest.getLongitude()
                            + ",\n radius : "           + getDriversRequest.getRadius()
                            + ",\n IdLocality : "       + getDriversRequest.getIdLocality()
                            + ",\n tariff : "           + getDriversRequest.getTariff()
                            + ",\n NoSmoking : "        + getDriversRequest.getNoSmoking()
                            + ",\n g_width : "          + getDriversRequest.getG_width()
                            + ",\n conditioner : "      + getDriversRequest.getConditioner()
                            + ",\n animal : "           + getDriversRequest.getAnimal()
                            + ",\n need_check : "       + getDriversRequest.getNeed_check()
                            + ",\n need_wifi : "        + getDriversRequest.getNeed_wifi()
                            + ",\n need_card : "        + getDriversRequest.getNeed_card()
                            + ",\n e_type : "           + getDriversRequest.getE_type()
                            + ",\n get_only_count : "   + getDriversRequest.getGet_only_count()
                            + ",\n baby_seat : "        + getDriversRequest.getBaby_seat()
                            + ",\n limit : "            + getDriversRequest.getLimit()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getDriverModel().GetDrivers(getDriversRequest,
                new RequestCallback<DriverData>(mContext, RequestTypes.REQUEST_GET_DRIVERS_FROM_MAP));

    } //end method GetDriversFromMap

    /**
     * Method for getting driver info
     *
     * @param IdOrder - order id
     * @param driverId - driver id
     */
    public void GetDriverInfo (String IdOrder, String driverId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.GetDriverInfo : IdOrder : " + IdOrder + " : id : " + driverId);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getdriverinfo : {\n"
                            + " IdOrder : "            + IdOrder
                            + ",\n driverId : "        + driverId
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getDriverModel().GetDriverInfo(new DriverInfoRequest(IdOrder, driverId),
                new RequestCallback<DriverInfo>(mContext, RequestTypes.REQUEST_GET_DRIVER_INFO));

    } //end method GetDriverInfo

    /**
     * Method for getting driver coordinates
     */
    public void GetDriverCoordinates () {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.GetDriverCoordinates : orderId : " + CMApplication.getTrackingOrderId());

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getdrivercoordinates : {\n"
                            + " IdOrder : "            + CMApplication.getTrackingOrderId()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getDriverModel().GetDriverCoordinates(new DriverCoordRequest(CMApplication.getTrackingOrderId()),
                new RequestCallback<DriverCoordinates>(mContext, RequestTypes.REQUEST_GET_DRIVER_COORDINATES));

    } //end method GetDriverCoordinates


}
