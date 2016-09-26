package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.google.GoogleAutocompleteData;
import com.locservice.api.entities.google.GoogleDirectionsData;
import com.locservice.api.entities.google.GoogleDistanceMatrixData;
import com.locservice.api.entities.google.GoogleGeocode;
import com.locservice.api.entities.google.GooglePlaceData;
import com.locservice.api.service.GoogleService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 25 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GoogleModel {

    private static final String TAG = GoogleModel.class.getSimpleName();

    private GoogleService googleService;

    public GoogleModel() {
        googleService = ServiceGenerator.createService(GoogleService.class, ApiHostType.API_GOOGLE_MAPS, "");
    }

    /**
     * Method for getting directions form google api
     * @param origin - origin
     * @param destination - destination address
     * @param units - units
     * @param mode - mode
     * @param sensor - sensor
     * @param cb - response callback
     */
    public void GetDirections(String origin,
                              String destination,
                              String units,
                              String mode,
                              boolean sensor,
                              String key,
                              Callback<GoogleDirectionsData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleModel.GetDirections origin : " + origin
                + " destination : " + destination
                + " units : " + units
                + " mode : " + mode
                + " sensor : " + sensor
                + " cb : " + cb);

        Call<GoogleDirectionsData> googleDirectionsDataCall = googleService.GetDirections(origin,
                                                                                    destination,
                                                                                    units,
                                                                                    mode,
                                                                                    sensor,
                                                                                    key,
                                                                                    "ru");
        googleDirectionsDataCall.enqueue(cb);

    } // end method getDirections

    /**
     * Method for getting distance matrix form google api
     * @param origins - origins
     * @param destinations - destination addresses
     * @param units - units
     * @param mode - mode
     * @param language - request language
     * @param departure_time - time of departure
     * @param key - api key
     * @param cb - response callback
     */
    public void GetDistanceMatrix(String origins,
                                  String destinations,
                                  String units,
                                  String mode,
                                  String language,
                                  String departure_time,
                                  String key,
                                  Callback<GoogleDistanceMatrixData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleModel.GetDistanceMatrix origin : " + origins
                + " : destinations : " + destinations
                + " : units : " + units
                + " : mode : " + mode
                + " : language : " + language
                + " : departure_time : " + departure_time
                + " : key : " + key
                + " : cb : " + cb);

        Call<GoogleDistanceMatrixData> googleDistanceMatrixDataCall = googleService.GetDistanceMatrix(origins,
                destinations,
                units,
                mode,
                language,
                departure_time,
                key);
        googleDistanceMatrixDataCall.enqueue(cb);

    } // end method GetDistanceMatrix

    /**
     * Method for getting geocode data form google api
     * @param latlng - request coordinates
     * @param language - request language
     * @param key - api key
     * @param cb - response callback
     */
    public void GetGeocode(String latlng,
                           String language,
                           String key,
                           Callback<GoogleGeocode> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleModel.GetGeocode latlng : " + latlng
                + " : language : " + language
                + " : key : " + key
                + " : cb : " + cb);

        Call<GoogleGeocode> googleGeocodeCall = googleService.GetGeocode(latlng,
                language,
                key);
        googleGeocodeCall.enqueue(cb);

    } // end method GetGeocode

    /**
     * Method for getting google autocomplete data
     * @param input - Input text
     * @param language - Language(ru)
     * @param components - Components(country:ru)
     * @param types - Type(address or geocode)
     * @param location - Location (35.4646,46.464418)
     * @param radius - Radius(km)
     * @param key - API key
     */
    public void GetAutocomplete(String input ,
                                String language,
                                String components,
                                String types,
                                String location,
                                String radius,
                                String key,
                                Callback<GoogleAutocompleteData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleModel.GetAutocomplete input : " + input
                + " : language : " + language
                + " : components : " + components
                + " : types : " + types
                + " : location : " + location
                + " : types : " + types
                + " : radius : " + radius
                + " : cb : " + cb);

        Call<GoogleAutocompleteData> googleGeocodeCall = googleService.GetAutocomplete(input,
                language,
                components,
                types,
                location,
                radius,
                key);
        googleGeocodeCall.enqueue(cb);

    } // end method GetAutocomplete

    /**
     * Method for getting google place by place id
     * @param placeid - Google place id
     * @param key - Google API key
     * @param cb - Callback
     */
    public void GetPlace(String placeid,
                         String key,
                         Callback<GooglePlaceData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: GoogleModel.GetAutocomplete placeid : " + placeid
                + " : key : " + key
                + " : cb : " + cb);

        Call<GooglePlaceData> googleGeocodeCall = googleService.GetPlace(placeid, key);
        googleGeocodeCall.enqueue(cb);

    } // end method GetPlace

}
