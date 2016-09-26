package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.google.GoogleAutocompleteData;
import com.locservice.api.entities.google.GoogleDirectionsData;
import com.locservice.api.entities.google.GoogleDistanceMatrixData;
import com.locservice.api.entities.google.GoogleGeocode;
import com.locservice.api.entities.google.GooglePlaceData;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 25 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleManager extends WebManager {

    private static final String TAG = DriverManager.class.getSimpleName();

    public GoogleManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting directions form google api
     *
     * @param origin - origin text
     * @param destination - destination text
     */
    public void GetDirections(String origin, String destination, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetDirections origin : " + origin
                + " : destination : " + destination
                + " : requestType : " + requestType);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : GetDirections : {\n"
                            + " origin : "          + origin
                            + ",\n destination : "  + destination
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getGoogleModel().GetDirections(origin, destination, "metric", "driving", true,
                "",
                new RequestCallback<GoogleDirectionsData>(mContext, requestType));

    } // end method GetDirections

    /**
     * Method for getting distance matrix form google api
     *
     * @param origins - origins
     * @param destinations - string destinations
     */
    public void GetDistanceMatrix(String origins, String destinations, String departure_time, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetDistanceMatrix origins : " + origins
                + " : destinations : " + destinations
                + " : departure_time : " + departure_time);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : GetDistanceMatrix : {\n"
                            + " origins : "          + origins
                            + ",\n destinations : "  + destinations
                            + ",\n departure_time : "  + departure_time
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getGoogleModel().GetDistanceMatrix(origins, destinations, "metric", "driving", LocaleManager.getLocaleLang(), departure_time,
//                CMAppGlobals.GOOGLE_ANDROID_DISTANCE_API_KEY,
                "",
                new RequestCallback<GoogleDistanceMatrixData>(mContext, requestType));

    } // end method GetDistanceMatrix

    /**
     * Method for getting geocode data
     * For Coll Address form google api
     *
     * @param latlng - coll address coordinates
     */
    public void GetGeocodeForCollAddress(String latlng) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetGeocodeForCollAddress : latlng : " + latlng);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : GetGeocodeForCollAddress : {\n"
                            + " latlng : "  + latlng
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getGoogleModel().GetGeocode(latlng, LocaleManager.getLocaleLang(),
                "",
                new RequestCallback<GoogleGeocode>(mContext, RequestTypes.REQUEST_GOOGLE_GEOCODE_FOR_COLL_ADDRESS));

    } // end method GetGeocodeForCollAddress

    /**
     * Method for getting geocode data
     * For Delivery Address form google api
     *
     * @param latlng - coll address coordinates
     */
    public void GetGeocodeForDeliveryAddress(String latlng) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetGeocodeForDeliveryAddress : latlng : " + latlng);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : GetGeocodeForDeliveryAddress : {\n"
                            + " latlng : "  + latlng
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getGoogleModel().GetGeocode(latlng, LocaleManager.getLocaleLang(),
                "",
                new RequestCallback<GoogleGeocode>(mContext, RequestTypes.REQUEST_GOOGLE_GEOCODE_FOR_DELIVERY_ADDRESS));

    } // end method GetGeocodeForDeliveryAddress

    /**
     * Method for getting google autocomplete data
     * @param input - Input text
     * @param language - Language(ru)
     * @param components - Components(country:ru)
     * @param types - Type(address,geocode)
     * @param location - Location (35.4646,46.464418)
     * @param radius - Radius(by km)
     */
    public void GetAutocomplete(String input ,
                                String language,
                                String components,
                                String types,
                                String location,
                                String radius) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetAutocomplete : input : " + input
                + " : language : " + language
                + " : components : " + components
                + " : types : " + types
                + " : location : " + location
                + " : radius : " + radius
        );

        ServiceLocator.getGoogleModel().GetAutocomplete(input,
                                                        language,
                                                        components,
                                                        types,
                                                        location,
                                                        radius,
                                                        CMAppGlobals.GOOGLE_ANDROID_PLACE_API_KEY,
                new RequestCallback<GoogleAutocompleteData>(mContext, RequestTypes.REQUEST_GOOGLE_AUTOCOMPLETE));

    } // end method GetAutocomplete

    /**
     * Method for getting google place by place id
     * @param placeid - Google place id
     */
    public void GetPlace(String placeid) {

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleManager.GetAutocomplete : placeid : " + placeid);

        ServiceLocator.getGoogleModel().GetPlace(placeid,
                CMAppGlobals.GOOGLE_ANDROID_PLACE_API_KEY,
                new RequestCallback<GooglePlaceData>(mContext, RequestTypes.REQUEST_GOOGLE_PLACE));

    } // end method GetPlace
}
