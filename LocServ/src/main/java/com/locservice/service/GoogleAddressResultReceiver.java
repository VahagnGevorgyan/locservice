package com.locservice.service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 26 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
@SuppressLint("ParcelCreator")
public class GoogleAddressResultReceiver extends ResultReceiver {
    private static final String TAG = GoogleAddressResultReceiver.class.getSimpleName();

    public interface OnReceiveResultListener {

        void onReceiveResult(int resultCode, Bundle resultData);
    }

    /** Listener of this receiver */
    private OnReceiveResultListener listener = null;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public GoogleAddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GoogleAddressResultReceiver.onReceiveResult : Received : " + resultCode);

        if (null != this.listener) {
            this.listener.onReceiveResult(resultCode, resultData);
        } else {
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GoogleAddressResultReceiver.onReceiveResult : Message dropped - no listener available for code : "
                    + resultCode + ": " + resultData.toString());
        }
    } // end method onReceiveResult

    /**
     * Mehtod for setting listener
     * @param listener
     */
    public void setListener(final OnReceiveResultListener listener) {
        this.listener = listener;
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.setListener : Listener : " + listener.hashCode());
    } // end method setListener

    /**
     * Method for clearing listener
     */
    public void clearListener() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.clearListener : Clear Listener : " + listener.hashCode());
        this.listener = null;
    } // end method clearListener
}
