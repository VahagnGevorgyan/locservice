package com.locservice.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderStatusResultReceiver extends BroadcastReceiver {

    private static final String TAG = OrderStatusResultReceiver.class.getSimpleName();

    public OrderStatusResultReceiver() {
        super();
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStatusResultReceiver : Constructor ");
    }

    public interface OnOrderStatusResultListener {

        void onReceiveOrderStatusResult(Context context, Intent intent);
    }

    /** Listener of this receiver */
    private OnOrderStatusResultListener listener = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderStatusResultReceiver.onReceive : intent : " + intent);

        if (null != this.listener) {
            this.listener.onReceiveOrderStatusResult(context, intent);
        } else {
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStatusResultReceiver.onReceiveResult : listener = null");
        }
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void setListener(final OnOrderStatusResultListener listener) {
        this.listener = listener;
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStatusResultReceiver.setListener : Listener : " + listener.hashCode());
    }

    /**
     * Clear listener
     */
    public void clearListener() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderStatusResultReceiver.clearListener : Clear Listener : " + listener.hashCode());
        this.listener = null;
    }

}
