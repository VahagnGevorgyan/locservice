package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.BonusInfo;
import com.locservice.api.entities.RepostSocialData;
import com.locservice.api.request.RepostSocialRequest;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class BonusManager extends WebManager {

    private static final String TAG = BonusManager.class.getSimpleName();

    public BonusManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for getting bonuses information
     */
    public void GetBonusesInfo() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BonusManager.GetBonusesInfo ");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getBonusesInfo : {\n"
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getBonusModel().GetBonusesInfo(new WebRequest("getBonusesInfo"),
                new RequestCallback<BonusInfo>(mContext, RequestTypes.REQUEST_GET_BONUS_INFO));

    } // end method GetBonusInfo

    /**
     * Method for repost social network posting
     * @param social_id - social id
     */
    public void RepostSocial(int social_id) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BonusManager.RepostSocial : social_id : " + social_id);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : RepostSocial : {\n"
                    + " social_id : " + social_id
                    + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getBonusModel().RepostSocial(new RepostSocialRequest(social_id),
                new RequestCallback<RepostSocialData>(mContext, RequestTypes.REQUEST_REPOST_SOCIAL));

    } // end method RepostSocial

}
