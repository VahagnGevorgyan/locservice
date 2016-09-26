package com.locservice.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.locservice.CMAppGlobals;
import com.locservice.utils.Logger;

public class DetachableResultsReceiver extends ResultReceiver {

	private static final String TAG = DetachableResultsReceiver.class.getSimpleName();
	
	public DetachableResultsReceiver(Handler handler) {
		super(handler);
	}
	
	public interface OnReceiveResultListener {
		
		void onReceiveResult(int resultCode, Bundle resultData);
	}
	
	/** Listener of this receiver */
	private OnReceiveResultListener listener = null;

	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.onReceiveResult : Received : " + resultCode);

		if (null != this.listener) {
			this.listener.onReceiveResult(resultCode, resultData);
		} else {
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.onReceiveResult : Message dropped - no listener available for code : " 
						+ resultCode + ": " + resultData.toString());
		}
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(final OnReceiveResultListener listener) {
		this.listener = listener;
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.setListener : Listener : " + listener.hashCode());
	}

	public void clearListener() {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: DetachableResultsReceiver.clearListener : Clear Listener : " + listener.hashCode());
		this.listener = null;
	}
	

}
