package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.GetFavoriteData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.entities.SetFavoriteData;
import com.locservice.api.request.GetFavoriteRequest;
import com.locservice.api.request.RemoveFavoriteRequest;
import com.locservice.api.request.SetFavoriteRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FavoriteAddressManager extends WebManager {

    private static final String TAG = FavoriteAddressManager.class.getSimpleName();

    public FavoriteAddressManager(ICallBack context) {
        super(context);
    }

    /**
     *  Method for setting favorite address
     * @param address - address for setting
     * @param latitude - latitude of address
     * @param longitude - longitude of address
     * @param entrance - entrance of address
     * @param comment - comment of address
     * @param name - name of address
     * @param id - address id
     */
    public void SetFavorite(String address, String latitude, String longitude, String entrance, String comment, String name, String id) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressManager.GetFavorite : address : " + address
                + " : latitude : " + latitude
                + " : longitude : " + longitude
                + " : entrance : " + entrance
                + " : comment : " + comment
                + " : name : " + name
                + " : id : " + id
                + " : IdLocality : " + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "")
        );

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : setfavorite : {\n"
                            + " address : "   + address
                            + ",\n latitude : "     + latitude
                            + ",\n longitude : "     + longitude
                            + ",\n entrance : "     + entrance
                            + ",\n comment : "     + comment
                            + ",\n name : "     + name
                            + ",\n id : "     + id
                            + ",\n IdLocality : "     + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "")
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();


        ServiceLocator.getFavoriteAddressModel().SetFavorite(new SetFavoriteRequest(address, latitude, longitude, entrance, comment, name, id),
                new RequestCallback<SetFavoriteData>(mContext, RequestTypes.REQUEST_SET_FAVORITE));

    } // end method SetFavorite

    /**
     * Method for getting favorite address
     */
    public void GetFavorite(int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressManager.GetFavorite");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getfavorite : {\n"
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getFavoriteAddressModel().GetFavorite(new GetFavoriteRequest(),
                new RequestCallback<List<GetFavoriteData>>(mContext, requestType));

    } // end method GetFavorite

    /**
     * Method for removing favorite address
     * @param id - favorite address id
     */
    public void RemoveFavorite(String id) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: FavoriteAddressManager.RemoveFavorite : id : " + id);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : removefavorite : {\n"
                            + " id : "   + id
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getFavoriteAddressModel().RemoveFavorite(new RemoveFavoriteRequest(id),
                new RequestCallback<ResultData>(mContext, RequestTypes.REQUEST_REMOVE_FAVORITE));

    } // end method RemoveFavorite
}
