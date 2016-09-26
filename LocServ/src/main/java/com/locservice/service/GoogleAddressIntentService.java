package com.locservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.locservice.CMAppGlobals;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vahagn Gevorgyan
 * 26 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleAddressIntentService extends IntentService {

    private static final String TAG = GoogleAddressIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *.
     */
    public GoogleAddressIntentService() {
        super("GoogleAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, new Locale(LocaleManager.getLocaleLang()));
        String errorMessage = "";
        if (intent != null) {
            mReceiver = intent.getParcelableExtra(CMAppGlobals.RECEIVER);
            // Get the location passed to this service through an extra.
            LatLng latLng = intent.getParcelableExtra(CMAppGlobals.LAT_LNG_DATA_EXTRA);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                errorMessage = "Service not available";
                if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: GoogleAddressIntentService " + errorMessage + " IOException " + e.getMessage());
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid lat long used";
                if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: GoogleAddressIntentService" + errorMessage +  " IllegalArgumentException : " + e.getMessage());
            } catch (NullPointerException e) {
                errorMessage = "NullPointerException";
                if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: GoogleAddressIntentService" + errorMessage +  " NullPointerException : " + e.getMessage());
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = ":: GoogleAddressIntentService No address found";
                    if (CMAppGlobals.DEBUG) Logger.e(TAG, errorMessage);
                }
                deliverResultToReceiver(CMAppGlobals.FAILURE_RESULT, null, errorMessage);
            } else {
                Address address = addresses.get(0);
                if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: GoogleAddressIntentService address found : " + address.getCountryName());
                deliverResultToReceiver(CMAppGlobals.SUCCESS_RESULT, address, "Address found");
            }

        }
    } // end method onHandleIntent

    /**
     * Method for delivering to result receiver
     * @param resultCode
     * @param address
     * @param message
     */
    private void deliverResultToReceiver(int resultCode, Address address, String message) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CMAppGlobals.RESULT_DATA_KEY, address);
        bundle.putString(CMAppGlobals.RESULT_MESSAGE_KEY, message);
        mReceiver.send(resultCode, bundle);
    } // end method onHandleIntent deliverResultToReceiver

}
