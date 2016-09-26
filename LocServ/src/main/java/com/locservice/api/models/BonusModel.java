package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.BonusInfo;
import com.locservice.api.entities.RepostSocialData;
import com.locservice.api.request.RepostSocialRequest;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.BonusService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class BonusModel {
    private static final String TAG = BonusModel.class.getSimpleName();

    private BonusService bonusService;

    public BonusModel() {
        bonusService = ServiceGenerator.createService(BonusService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for getting bonuses info
     *
     * @param body - request body
     * @param cb   - response callback
     */
    public void GetBonusesInfo(WebRequest body, Callback<BonusInfo> cb) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: BonusModel.GetBonusesInfo : body : " + body + " cb : " + cb);

        Call<BonusInfo> bonusInfoCall = bonusService.GetBonusesInfo(body);
        bonusInfoCall.enqueue(cb);

    } // end method GetBonusesInfo

    /**
     * Method for re-posting social network
     *
     * @param body - request body
     * @param cb   - response callback
     */
    public void RepostSocial(RepostSocialRequest body, Callback<RepostSocialData> cb) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: BonusModel.RepostSocial : body : " + body + " cb : " + cb);

        Call<RepostSocialData> bonusInfoCall = bonusService.RepostSocial(body);
        bonusInfoCall.enqueue(cb);

    } // end method RepostSocial
}