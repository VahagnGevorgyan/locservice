package com.locservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestTypes;
import com.locservice.utils.Logger;

public class CMIntentService extends IntentService {

	private static final String TAG = CMIntentService.class.getSimpleName();
	
	
	public CMIntentService() {
		super(TAG);
	}
	
	/**
	 * Service request statuses
	 */
	public interface ServiceStatus {
		/** Running Status returned to the caller */
		int RUNNING = 0;
		/** Error Status returned to the caller */
		int ERROR = 1;
		/** Finished Status returned to the caller */
		int FINISHED = 2;
		/** Result Status - result comes in bundle with it */
		int RESULT = 3;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		 
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CMIntentService.onHandleIntent()");
		
		final int requestType = intent.getIntExtra(CMAppGlobals.EXTRA_REQUEST_TYPE, RequestTypes.DEF_REQUEST);
		final ResultReceiver receiver = intent.getParcelableExtra(CMAppGlobals.EXTRA_STATUS_RECEIVER);
		
		if(receiver == null) {
			if(CMAppGlobals.DEBUG)Logger.w(TAG, ":: CMIntentService.onHandleIntent() : Abnormal situation request : " + requestType);
		}
		
		
		int serviceResultCode = ServiceStatus.ERROR;
		final Bundle bundle = new Bundle();
		Bundle rqExecuteResultBundle = null;
		bundle.putString(Intent.EXTRA_TEXT, String.valueOf(requestType));
		bundle.putInt(CMAppGlobals.EXTRA_REQUEST_TYPE, requestType);
		
		if (receiver != null) {
			receiver.send(ServiceStatus.RUNNING, bundle);
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CMIntentService.onHandleIntent() : RECEIVER : " + receiver.toString());
		}
		try {
//			ApiResponseHandler handler = null;
//			ApiRequest request = null;
			
//			switch (requestType) {
//			case RequestTypes.TARIFF_ZONES:
//				handler = new TariffZonesHandler();
//				request = new TariffZonesRequest();
//				break;
//			case RequestTypes.ACTUAL_VERSION:
//				handler = new ActualVersionHandler();
//				request = new ActualVersionRequest(intent.getStringExtra(CMAppGlobals.EXTRA_REQUEST_DATA));
//				break;
//			case RequestTypes.TARIFFS:
//				handler = new TariffsHandler();
//				request = new TariffsRequest(intent.getStringExtra(CMAppGlobals.EXTRA_REQUEST_DATA));
//				break;
//			case RequestTypes.TARIFF_SERVICE_OPTIONS:
//				handler = new TariffServiceOptionsHandler();
//				request = new TariffServiceOptionsRequest(intent.getStringExtra(CMAppGlobals.EXTRA_REQUEST_DATA));
//				break;
//			default:
//				break;
//			}
			
			/// CALLER!!!
//			ApiRequestCaller.getCaller();
//			rqExecuteResultBundle = ApiRequestCaller.executeRequest(request, handler);

			if (CMAppGlobals.DEBUG) {

				Logger.i(TAG, ":: CMIntentService.onHandleIntent() : request finished");
			}

			if (rqExecuteResultBundle != null) {
				
				int errorCode = rqExecuteResultBundle
						.getInt(CMAppGlobals.ERROR_CODE_EXTRA_BUNDLE);
				
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CMIntentService.onHandleIntent() : BUNDLE : " + rqExecuteResultBundle.toString());
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CMIntentService.onHandleIntent() : ERROR CODE : " + errorCode);

				if (errorCode == ServiceErrorCodes.CM_REQUEST_SUCCESS) {
					serviceResultCode = ServiceStatus.FINISHED;
				}
			}
			
		} catch(Exception e) {
			if(CMAppGlobals.DEBUG)Logger.e(TAG,":: ApiRequest : Error : Exception while request : " + e.toString());
		} finally {
			if (receiver != null) {
				if (rqExecuteResultBundle != null) {
					bundle.putAll(rqExecuteResultBundle);
				}

				receiver.send(serviceResultCode, bundle);
			}
		}

	}

}
