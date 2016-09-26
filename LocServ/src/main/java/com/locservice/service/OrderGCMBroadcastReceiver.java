package com.locservice.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.locservice.CMAppGlobals;
import com.locservice.gcm.GCMMessages;
import com.locservice.gcm.GCMUtils;
import com.locservice.messages.Message;
import com.locservice.ui.helpers.OrderState;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderGCMBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = OrderGCMBroadcastReceiver.class
            .getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderGCMBroadcastReceiver.onReceive : intent : " + intent);

        if (intent.getExtras() == null)
            return;

//        String message = "";
//
//        Object receivedMessage = intent.getExtras().getString(
//                GCMUtils.EXTRA_MESSAGE);
//
//        if (receivedMessage instanceof String) {
//            message = intent.getExtras().getString(GCMUtils.EXTRA_MESSAGE);
//        }
        String message = intent.getExtras().getString(GCMUtils.EXTRA_MESSAGE);
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: OrderGCMBroadcastReceiver.onReceive : message : " + message);

        int type = intent.getExtras().getInt(GCMUtils.EXTRA_MESSAGE_TYPE);
        String orderId = intent.getExtras().getString(
                GCMUtils.EXTRA_MESSAGE_ORDER);

        if (GCMMessages.WEB_VIEW_NEWS.ordinal() == type) {
            Utils.generateWebViewNotification(context,
                    intent.getStringExtra(GCMUtils.EXTRA_TITLE),
                    message,
                    (Message) intent.getParcelableExtra(GCMUtils.EXTRA_WEB_VIEW_MESSAGE));
            return;
        }

        if (GCMMessages.AM.ordinal() == type) {
            Utils.generateNotification(context, message, type);
            return;
        }

        OrderState state = GCMMessages.convertToOrderState(type);
        if(orderId == null || orderId.equals(""))
            return;

        // GCM Messages
        int result = 0;
        if (GCMMessages.CP.ordinal() == type
                || GCMMessages.AR.ordinal() == type) {// NewOrderData done
            final String price = intent.getStringExtra(GCMUtils.EXTRA_DATA1);
            final String tripTime = intent.getStringExtra(GCMUtils.EXTRA_DATA2);
            final String bonus = intent.getStringExtra(GCMUtils.EXTRA_DATA3);

            GCMUtils.updateOrderUI(context, orderId, state.ordinal(), price, bonus, tripTime, message, true);
            if (result == 1) {
                GCMMessages msg = GCMMessages.convertFromOrdinal(type);
                Utils.generateNotification(context, msg, orderId, type);
                return;
            }

        }

        if (GCMMessages.R.ordinal() == type) {
            // order would be created
            result = 1;
        } else if (GCMMessages.SC.ordinal() == type) {
            // order would be created
            GCMMessages msg = GCMMessages.convertFromOrdinal(type);
            Utils.generateNotification(context, msg, orderId, type);
        }

        if (!TextUtils.isEmpty(orderId)) {
            GCMUtils.updateOrderUI(context, orderId, state.ordinal(), null, null, null, message, true);
            result = 1;
        }

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderGCMBroadcastReceiver : orderId : "
                + orderId + " : state : " + state.ordinal() + " : result : " + result);

        if (result == 1) {
            GCMMessages msg = GCMMessages.convertFromOrdinal(type);
            Utils.generateNotification(context, msg, orderId, type);
        } else if(GCMMessages.A.ordinal() == type) {
            GCMMessages msg = GCMMessages.convertFromOrdinal(type);
            Utils.generateNotification(context, msg, orderId, type);
        }
    }


}
