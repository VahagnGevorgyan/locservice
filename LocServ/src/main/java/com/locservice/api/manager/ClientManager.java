package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.RequestUpdateClientInfo;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ClientManager extends WebManager {

    private static final String TAG = ClientManager.class.getSimpleName();

    public ClientManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting client info
     */
    public void GetClientInfo () {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.GetClientInfo");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "getclientinfo : {\n"
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getClientModel().GetClientInfo(new WebRequest("getclientinfo"),
                new RequestCallback<ClientInfo>(mContext, RequestTypes.REQUEST_GET_CLIENT_INFO));

    } // end method GetClientInfo

    /**
     * Method for updating client info
     * @param name - client name
     * @param email - client email address
     * @param sms_notification - sms notification
     * @param photo - client photo
     */
    public void UpdateClientInfo (String name, String email, int sms_notification, String photo, int only_filled) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: DriverManager.UpdateClientInfo : name : "
                + name + " : email : " + email + " : sms_notification : " + sms_notification + " : only_filled : " + only_filled);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "updateclientinfo : {\n"
                            + " id : " + name
                            + ",\n email : " + email
                            + ",\n sms_notification : " + sms_notification
                            + ",\n only_filled : " + only_filled
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getClientModel().UpdateClientInfo(new RequestUpdateClientInfo(name, email, sms_notification, photo, only_filled),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_UPDATE_CLIENT_INFO));

    } // end method UpdateClientInfo
}
