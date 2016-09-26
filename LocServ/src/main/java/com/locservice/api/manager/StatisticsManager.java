package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.Statistics;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;


/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class StatisticsManager extends WebManager {

    private static final String TAG = StatisticsManager.class.getSimpleName();

    public StatisticsManager(ICallBack iCallBack) {
        super(iCallBack);
    }

    /**
     * Method for getting user statistics
     */
    public void GetStatistics() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: StatisticsManager.GetStatistics ");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getstatistic : {\n"
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getStatisticsModel().GetStatistics(new WebRequest("getstatistic"),
                new RequestCallback<Statistics>(mContext, RequestTypes.REQUEST_GET_STATISTICS));

    } // end method GetStatistics

}
