package com.locservice.ws.manager;

import com.locservice.CMAppGlobals;
import com.locservice.protocol.IWsCallBack;
import com.locservice.utils.Logger;
import com.locservice.ws.WSManager;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Vahagn Gevorgyan
 * 03 December 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AuthWsManager {

    private static final String TAG = AuthWsManager.class.getSimpleName();

    public IWsCallBack mContext;

    public AuthWsManager(){}

    public AuthWsManager(IWsCallBack context) {
        mContext = context;
    }


    /**
     * Method for ws authentication
     */
    public void authenticate() {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("action", "authenticate");
            jsonRequest.put("label", "label");
            JSONObject dataObject = new JSONObject();
            dataObject.put("device_token", CMAppGlobals.TOKEN);
            jsonRequest.put("data", dataObject);

            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AuthWsManager.authenticate : jsonRequest : " + jsonRequest);

            // send JSON data
            WSManager.getInstance().con(mContext).sendJSONData(jsonRequest);


        } catch (JSONException e) {

            mContext.onWsFailure(e);
        }

    } // end method authenticate

}
