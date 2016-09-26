package com.locservice.api.manager;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.TariffData;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 19 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class TariffManager extends WebManager {
	
	private static final String TAG = TariffManager.class.getSimpleName();

    public TariffManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting tariff list
     *
     * @param link - tariff list link
     */
    public void GetTariffList(String link) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: TariffManager.GetTariffList ։ tariff_link : " + link);
		if(link != null && !link.equals("")) {
			ServiceLocator.getTariffModel().GetTariffList(link, new RequestCallback<TariffData>(mContext, RequestTypes.GET_TARIFFS));
		}
    } // end method GetTariffList


}
