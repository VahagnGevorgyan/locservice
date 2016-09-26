package com.locservice.ui.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 11 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkStateReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: NetworkStateReceiver.onReceive : Network connectivity change ");

//        if(Utils.checkNetworkStatus(context))
//            // Internet connection, send network state changed
//            EventBus.getDefault().post(new NetworkStateChanged(true));
//        else
//            // no Internet connection, send network state changed
//            EventBus.getDefault().post(new NetworkStateChanged(false));

    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

}
