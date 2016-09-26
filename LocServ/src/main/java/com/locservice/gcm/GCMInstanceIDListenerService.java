package com.locservice.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Vahagn Gevorgyan
 * 27 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GCMInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);

    } // end method onTokenRefresh


}
