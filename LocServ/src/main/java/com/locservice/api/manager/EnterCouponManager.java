package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.EnterCouponData;
import com.locservice.api.request.EnterCouponRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 19 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class EnterCouponManager extends WebManager {

    private static final String TAG = EnterCouponManager.class.getSimpleName();

    public EnterCouponManager(ICallBack context) {
        super(context);
    }

    /**
     * Method for entering coupon for getting bonus
     */
    public void EnterCouponRequest(int id_coupon, String code) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: EnterCouponManager.EnterCouponRequest : id_coupon : " + id_coupon + " : code : " + code);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : enterCoupon : {\n"
                            + " id_coupon : "   + id_coupon
                            + ",\n code : "     + code
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getEnterCouponModel().EnterCouponRequest(new EnterCouponRequest(id_coupon, code),
                new RequestCallback<EnterCouponData>(mContext, RequestTypes.REQUEST_ENTER_COUPON));

    } // end method EnterCouponRequest
}
