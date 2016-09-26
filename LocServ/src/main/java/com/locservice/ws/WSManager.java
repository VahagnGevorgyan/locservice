package com.locservice.ws;


import com.locservice.CMAppGlobals;
import com.locservice.protocol.IWsCallBack;
import com.locservice.utils.Logger;

import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Vahagn Gevorgyan
 * 02 December 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class WSManager {

    private static final String TAG = WSManager.class.getSimpleName();

    private static WebSocketConnection mConnection = new WebSocketConnection();

    final String wsURI = "WS_URL";

    private IWsCallBack mContext;

    private static WSManager wsManager;

    public static WSManager getInstance() {
        if(wsManager == null) wsManager = new WSManager();
        return wsManager;
    }


    /**
     * Method for checking connection
     * @return
     */
    public WSManager con(IWsCallBack context) {
        setmContext(context);
        if(!mConnection.isConnected()) {
            this.connectWS();
        }
        return wsManager;

    } // end method con


    /**
     * Method for connecting ws server
     */
    public void connectWS() {
        try {
            mConnection.connect(wsURI, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: WSManager.onOpen ");
                    getmContext().onWsOpen();

                }

                @Override
                public void onTextMessage(String payload) {
                    if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: WSManager.onTextMessage : payload : " + payload);
                    getmContext().onWsSuccess(payload);

                }

                @Override
                public void onClose(int code, String reason) {
                    if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: WSManager.onClose : " + reason);
                    getmContext().onWsClose(code, reason);

                }
            });
        } catch (WebSocketException e) {
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: WSManager.onWsFailure : " + e.getMessage());
            getmContext().onWsFailure(e);

        }

    } // end method connectWS


    /**
     * Method for sending JSON data to ws server
     * @param data
     */
    public void sendJSONData(JSONObject data) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: WSManager.sendJSONData : data : " + data + " : mConnection : " + mConnection);
        mConnection.sendTextMessage(data.toString());
    }


    public IWsCallBack getmContext() {
        return mContext;
    }

    public void setmContext(IWsCallBack mContext) {
        this.mContext = mContext;
    }
}
