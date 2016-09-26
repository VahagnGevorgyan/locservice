package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.EnterCouponData;
import com.locservice.api.request.EnterCouponRequest;
import com.locservice.api.service.EnterCouponService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 19 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class EnterCouponModel {

    private static final String TAG = EnterCouponModel.class.getSimpleName();

    private EnterCouponService commentService;

    public EnterCouponModel() {
        this.commentService = ServiceGenerator.createService(EnterCouponService.class, ApiHostType.API_BASE_URL, "");
    }

    /**
     * Method for entering coupon for getting bonus
     * @param body - request body
     * @param cb - response callback
     */
    public void EnterCouponRequest(EnterCouponRequest body, Callback<EnterCouponData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: EnterCouponModel.EnterCouponRequest : body : " + body + " cb : " + cb);

        Call<EnterCouponData> enterCouponDataCall = commentService.EnterCouponRequest(body);
        enterCouponDataCall.enqueue(cb);

    } //end method EnterCouponRequest
}
