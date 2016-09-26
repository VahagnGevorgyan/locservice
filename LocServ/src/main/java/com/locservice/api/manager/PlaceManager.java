package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.CheckfilesData;
import com.locservice.api.entities.CountryData;
import com.locservice.api.request.CheckfilesChangesRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PlaceManager extends WebManager {

    private static final String TAG = PlaceManager.class.getSimpleName();

    public PlaceManager(ICallBack context) { super(context); }


    /**
     * Method for checking files from server
     * @param id_locality - city id
     * @param tariff_date - tariff file last updated date
     * @param landmark_date - landmarks file last updated date
     * @param cities_date - cities file last updated date
     * @param locale - language locale
     */
    public void CheckFilesChanges(int id_locality, String tariff_date, String landmark_date, String cities_date, String locale) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: PlaceManager.CheckFilesChanges : locale : " + locale +
                                                " : id_locality : " + id_locality +
                                                " : tariff_date : " + tariff_date +
                                                " : landmark_date : " + landmark_date +
                                                " : cities_date : " + cities_date);
        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : checkFilesChanges : {\n"
                            + " locale : " + locale
                            + ",\n id_locality : " + id_locality
                            + ",\n tariff_date : " + tariff_date
                            + ",\n landmark_date : " + landmark_date
                            + ",\n cities_date : " + cities_date
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getPlaceModel().CheckFilesChanges(new CheckfilesChangesRequest(id_locality, locale, tariff_date, landmark_date, cities_date),
                            new RequestCallback<CheckfilesData>(mContext, RequestTypes.CHECK_FILES_CHANGES));

    } // end method CheckFilesChanges


    /**
     * Method for getting countries list by link
     *
     * @param link - countries file link
     */
    public void GetCountriesList(String link) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: PlaceManager.GetCountriesList ։ country_link : " + link);
        if(link != null && !link.equals("")) {
            ServiceLocator.getPlaceModel().GetCountriesList(link, new RequestCallback<CountryData>(mContext, RequestTypes.GET_COUNTRIES));
        }

    } // end method GetCountriesList

}
