package com.locservice.api.manager;


import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.LastAddressData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.request.LastAdressesRequest;
import com.locservice.api.request.RemoveAddressRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 18 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddressManager extends WebManager {

    private static final String TAG = AddressManager.class.getSimpleName();

    public AddressManager(ICallBack context) {
        super(context);
    }


    /**
    * Method for getting last addresses
    * @param mContext - current context
    * @param id - last address id
    */
    public void GetLastAddresses(FragmentActivity mContext, String id, int IdLocality) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddressManager.GetLastAddresses : id : " + id
                + " : IdLocality : " + IdLocality );

        if(CMAppGlobals.API_TOAST)
            Toast.makeText(mContext, "Request : getlastaddresses : {\n"
                            + " id : " + id
                            + ",\n IdLocality : " + IdLocality + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getAddressModel().GetLastAddresses(new LastAdressesRequest(id, IdLocality),
                new RequestCallback<LastAddressData>(this.mContext, RequestTypes.REQUEST_GET_LAST_ADDRESSES));

    } // end method GetLastAddresses

    /**
     * Method for getting last addresses
     * @param id - Address id
     */
    public void RemoveAddress(int id) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddressManager.RemoveAddress : id : " + id);

        ServiceLocator.getAddressModel().RemoveAddress(new RemoveAddressRequest(id),
                new RequestCallback<ResultData>(this.mContext, RequestTypes.REQUEST_REMOVE_ADDRESS));

    } // end method RemoveAddress


}
