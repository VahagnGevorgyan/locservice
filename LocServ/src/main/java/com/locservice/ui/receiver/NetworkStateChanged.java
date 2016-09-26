package com.locservice.ui.receiver;


/**
 * Created by Vahagn Gevorgyan
 * 11 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class NetworkStateChanged {

    private boolean mIsInternetConnected;

    public NetworkStateChanged(boolean isInternetConnected) {
        this.mIsInternetConnected = isInternetConnected;
    }

    public boolean isInternetConnected() {
        return this.mIsInternetConnected;
    }
}
