package com.locservice.api.manager;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.LandmarkData;
import com.locservice.api.entities.PolygonData;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 22 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LandmarkManager extends WebManager {

    private static final String TAG = LandmarkManager.class.getSimpleName();

    public LandmarkManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting landmark list
     *
     * @param link - landmark link
     */
    public void GetLandmarkList(String link) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LandmarkManager.GetLandmarkList ։ landmark_link : " + link);
        if(link != null && !link.equals("")) {
            ServiceLocator.getLandmarkModel().GetLandmarkList(link, new RequestCallback<LandmarkData>(mContext, RequestTypes.REQUEST_GET_LANDMARK_LIST));
        }

    } // end method GetLandmarkList

    /**
     * Method for getting airport polygon
     *
     * @param airportId - airport id
     */
    public void GetAirportPolygon(String airportId, String terminalId) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: LandmarkManager.GetAirportPolygon ։ airportId : " + airportId + " : terminalId : " + terminalId);
        if(airportId != null && !airportId.equals("")) {
            String url;
            url = "/landmark_poly/" + airportId;
            if(terminalId != null && !terminalId.equals("")) {
                url += "_" + terminalId;
            }
            url += ".json";
            ServiceLocator.getLandmarkModel().GetAirportPolygon(url, new RequestCallback<PolygonData>(mContext, RequestTypes.REQUEST_GET_AIRPORT_POLYGON));
        }

    } // end method GetAirportPolygon


}
