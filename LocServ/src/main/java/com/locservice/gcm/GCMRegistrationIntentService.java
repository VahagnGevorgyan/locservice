package com.locservice.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.Logger;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


/**
 * Created by Vahagn Gevorgyan
 * 27 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GCMRegistrationIntentService extends IntentService {

    private static final String TAG = GCMRegistrationIntentService.class.getSimpleName();

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMRegistrationIntentService.onHandleIntent : intent : " + intent);

        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_SenderId);

        String token = null;

        try {
            if (LocServicePreferences.getAppSettings().getBoolean(LocServicePreferences.Settings.SENT_TOKEN_TO_SERVER.key(), false)) {
                token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.GCM_TOKEN.key(), null);
            } else {
                // request token that will be used by the server to send push notifications
                token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                // save token
                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.GCM_TOKEN, token);
            }
            if (CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMRegistrationIntentService Token: " + token);


            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            if (CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMRegistrationIntentService IOException : " + e.getMessage());
            LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.SENT_TOKEN_TO_SERVER, false);
        }

    } // end method onHandleIntent

    /**
     * Method for sending registrarion to server
     * @param token
     */
    private void sendRegistrationToServer(String token) {
        // send network request

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.SENT_TOKEN_TO_SERVER, true);

    } // end method sendRegistrationToServer


}
