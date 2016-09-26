package com.locservice.gcm;

import com.locservice.CMApplication;
import com.locservice.ui.helpers.OrderState;

/**
 * Created by Vahagn Gevorgyan
 * 01 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public enum GCMMessages {
    NONE(com.locservice.R.string.os_u),
    REGISTERED(com.locservice.R.string.gcm_reg),
    REGISTERED_ERROR(com.locservice.R.string.gcm_err_reg),
    UNREGISTERED(com.locservice.R.string.gcm_unreg),
    UNREGISTERED_ERROR(com.locservice.R.string.gcm_err_unreg),
    GCM_DELETED(com.locservice.R.string.gcm_del),
    R(com.locservice.R.string.os_r),
    A(com.locservice.R.string.os_a),
    RC(com.locservice.R.string.os_rc),
    BR(com.locservice.R.string.os_br),
    OW(com.locservice.R.string.os_ow),
    AR(com.locservice.R.string.os_ar),
    CC(com.locservice.R.string.os_cc),
    CP(com.locservice.R.string.os_cp),
    NC(com.locservice.R.string.os_nc),
    SC(com.locservice.R.string.os_sc),
    AM(com.locservice.R.string.gcm_am),
    AUTHENTICATION_FAILED(com.locservice.R.string.gcm_au),
    NEWS(com.locservice.R.string.os_news),
    WEB_VIEW_NEWS(com.locservice.R.string.os_web_view_news);

    private int type;

    GCMMessages(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }

    @Override
    public String toString() {
        return CMApplication.getAppContext().getString(this.type);
    }

    public static OrderState convertToOrderState(int typeGCM) {
        OrderState state = OrderState.UNKNOWN;
        if (typeGCM == GCMMessages.R.ordinal()) {
            state = OrderState.R;
        } else if (typeGCM == GCMMessages.A.ordinal()) {
            state = OrderState.A;
        } else if (typeGCM == GCMMessages.BR.ordinal()) {
            state = OrderState.BR;
        } else if (typeGCM == GCMMessages.RC.ordinal()) {
            state = OrderState.RC;
        } else if (typeGCM == GCMMessages.OW.ordinal()) {
            state = OrderState.OW;
        } else if (typeGCM == GCMMessages.AR.ordinal()) {
            state = OrderState.AR;
        } else if (typeGCM == GCMMessages.CC.ordinal()) {
            state = OrderState.CC;
        } else if (typeGCM == GCMMessages.SC.ordinal()) {
            state = OrderState.SC;
        } else if (typeGCM == GCMMessages.CP.ordinal()) {
            state = OrderState.CP;
        } else if (typeGCM == GCMMessages.NC.ordinal()) {
            state = OrderState.NC;
        }

        return state;
    }

    public static GCMMessages convertFromOrdinal(int typeGCM) {
        GCMMessages state = GCMMessages.NONE;
        if (typeGCM == GCMMessages.R.ordinal()) {
            state = GCMMessages.R;
        } else if (typeGCM == GCMMessages.A.ordinal()) {
            state = GCMMessages.A;
        } else if (typeGCM == GCMMessages.BR.ordinal()) {
            state = GCMMessages.BR;
        } else if (typeGCM == GCMMessages.RC.ordinal()) {
            state = GCMMessages.RC;
        } else if (typeGCM == GCMMessages.OW.ordinal()) {
            state = GCMMessages.OW;
        } else if (typeGCM == GCMMessages.AR.ordinal()) {
            state = GCMMessages.AR;
        } else if (typeGCM == GCMMessages.SC.ordinal()) {
            state = GCMMessages.SC;
        } else if (typeGCM == GCMMessages.CC.ordinal()) {
            state = GCMMessages.CC;
        } else if (typeGCM == GCMMessages.CP.ordinal()) {
            state = GCMMessages.CP;
        }
        return state;
    }

}

