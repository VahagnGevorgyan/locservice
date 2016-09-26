package com.locservice.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Parcelable;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.api.entities.OrderStatusData;
import com.locservice.application.LocServicePreferences;
import com.locservice.messages.Message;
import com.locservice.utils.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GCMUtils {

    /**
     * Base URL of the Server
     */
    public static final String SERVER_URL = null;

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = CMAppGlobals.GOOGLE_APIS_PROJECT_NUMBER;

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCM";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.locservice.GCM_ACTION";

    public static final String UPDATE_ORDER_STATUS = "com.locservice.UPDATE_ORDER_STATUS";

    /**
     * Intent's extra to send to UI
     */

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MESSAGE_TYPE = "message_type";
    public static final String EXTRA_MESSAGE_ORDER = "order_id";
    public static final String EXTRA_DATA1 = "data1";
    public static final String EXTRA_DATA2 = "data2";
    public static final String EXTRA_DATA3 = "data3";

    public static final String EXTRA_ORDER_STATE = "extra_order_state";
    public static final String EXTRA_ORDER_ID = "extra_order_status";
    public static final String EXTRA_ORDER_TIME = "extra_order_time";
    public static final String EXTRA_ORDER_PRICE = "extra_order_price";
    public static final String EXTRA_ORDER_BONUS = "extra_order_bonus";
    public static final String EXTRA_ORDER_MESSAGE = "extra_order_message";
    public static final String EXTRA_ORDER_LIST = "extra_order_list";

    public static final String EXTRA_WEB_VIEW_MESSAGE = "extra_web_view_message";
    public static final String EXTRA_TITLE = "extra_title";

    public static final String EXTRA_IS_GCM = "extra_is_gcm";


    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId() {

        String registrationId = LocServicePreferences.getAppSettings()
                .getString(LocServicePreferences.Settings.GCM_TOKEN.key(), "");
        if (registrationId.isEmpty()) {
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMUtils.getRegistrationId : Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = LocServicePreferences.getAppSettings().getInt(
                LocServicePreferences.Settings.PROPERTY_APP_VERSION.key(), Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: GCMUtils.getRegistrationId : App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion() {
        Context context = CMApplication.getAppContext();
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId
     *            registration ID
     */
    private static void storeRegistrationId(String regId) {
        int appVersion = getAppVersion();
        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.GCM_TOKEN,
                regId);
        LocServicePreferences.getAppSettings().setSetting(
                LocServicePreferences.Settings.PROPERTY_APP_VERSION, appVersion);

    }

    public static void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String regid = "";
                GoogleCloudMessaging gcm = null;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(CMApplication.getAppContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    // msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    // sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the
                    // device will send
                    // upstream messages to a server that echo back the message
                    // using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regid);
                } catch (IOException ex) {
                    if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.registerInBackground : Error :" + ex.getMessage());
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                //
            }
        }.execute(null, null, null);
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                try {
                    GooglePlayServicesUtil.getErrorDialog(resultCode,
                            (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST)
                            .show();
                } catch (Exception e) {
                    if(CMAppGlobals.DEBUG) Logger.e(TAG, ":: GCMUtils.checkPlayServices : Error!!! ");
                }
            } else {
                if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.checkPlayServices : This device is not supported.");
            }
            return false;
        }
        return true;
    }


    /**
     * Notifies UI
     * @param context
     *          application's context
     * @param message
     *          message to be displayed
     * @param state
     *          order state
     * @param orderId
     *          order id
     */
    public static void GCMNotifyUI(Context context, String message, int state,
                                   String orderId) {
        if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.GCMNotifyUI : message : " + message + " : state : " + state);
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE_TYPE, state);
        intent.putExtra(EXTRA_MESSAGE_ORDER, orderId);
        if(CMAppGlobals.DEBUG) Logger.w(TAG, ":: GCMUtils.GCMNotifyUI : Intent : " + intent.hashCode());
        context.sendBroadcast(intent);

    }

    public static void GCMNotifyWebViewNews(Context context, String title, String sutTitle, int state, Message webMessage) {
        if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.GCMNotifyWebViewNews : sutTitle : " + sutTitle
                + " : title : " + title
                + " : state : " + state
                + " : webMessage : " + webMessage );
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_MESSAGE, sutTitle);
        intent.putExtra(EXTRA_MESSAGE_TYPE, state);
        intent.putExtra(EXTRA_WEB_VIEW_MESSAGE, (Parcelable) webMessage);

        if(CMAppGlobals.DEBUG) Logger.w(TAG, ":: GCMUtils.GCMNotifyWebViewNews : Intent : " + intent.hashCode());
        context.sendBroadcast(intent);
    }

    /**
     * Notifies UI
     * @param context
     *          application's context.
     * @param message
     *          message to be displayed.
     * @param messageType
     *          type of the message
     * @param data1
     * @param data2
     * @param data3
     */
    public static void GCMNotifyUIEx(Context context, String message,
                                     int messageType, String data1, String data2, String data3) {
        if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.GCMNotifyUIEx : message : " + message + " : messageType : " + messageType);
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, String.valueOf(message));
        intent.putExtra(EXTRA_MESSAGE_TYPE, messageType);
        intent.putExtra(EXTRA_DATA1, data1);
        intent.putExtra(EXTRA_DATA2, data2);
        intent.putExtra(EXTRA_DATA3, data3);
        if(CMAppGlobals.DEBUG) Logger.w(TAG, ":: GCMUtils.GCMNotifyUIEx : intent : " + intent.hashCode());

        context.sendBroadcast(intent);
    }

    /**
     * Notifies UI
     * @param context
     *          application's context
     * @param message
     *          message to be displayed
     * @param state
     *          order state
     *//*
    public static void GCMNotifyUI(Context context, String message, int state) {
        if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.GCMNotifyUI : message : " + message + " : state : " + state);

        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE_TYPE, state);
        if(CMAppGlobals.DEBUG) Logger.w(TAG, ":: GCMUtils.GCMNotifyUI : intent : " + intent.hashCode());

        context.sendBroadcast(intent);

    }
    */

    /**
     * Notifies UI
     * @param context
     *          application's context.
     * @param message
     *          message to be displayed.
     * @param messageType
     *          type of the message
     */
    public static void GCMNotifyUIEx(Context context, String message,
                                     int messageType, String orderId, String price, String time,
                                     String bonus) {
        if(CMAppGlobals.DEBUG) Logger.d(TAG, ":: GCMUtils.GCMNotifyUIEx : message : " + message + " : message type : " + messageType);

        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_MESSAGE_TYPE, messageType);
        intent.putExtra(EXTRA_MESSAGE_ORDER, orderId);
        intent.putExtra(EXTRA_DATA1, price);
        intent.putExtra(EXTRA_DATA2, time);
        intent.putExtra(EXTRA_DATA3, bonus);
        if(CMAppGlobals.DEBUG) Logger.w(TAG, ":: GCMUtils.GCMNotifyUIEx : intent : " + intent.hashCode());

        context.sendBroadcast(intent);
    }


    /**
     * Method for updating order UI by orderId and status
     * @param context
     * @param orderId
     * @param status
     */
    public static void updateOrderUI(Context context, String orderId, int status) {
        updateOrderUI(context, orderId, status, null, null, null, null, false);
    }

    /**
     * Method for updating order UI by orderId,status, add. data
     * @param context
     * @param orderId
     * @param status
     * @param price
     * @param bonus
     * @param trip_time
     * @param isGCM
     */
    public static void updateOrderUI(final Context context,
                                     final String orderId, final int status, String price, String bonus,
                                     String trip_time, String message, boolean isGCM) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMUtils.updateOrderUI : context : " + context
                        + " status : " + status
                        + " price : " + price
                        + " bonus : " + bonus
                        + " trip_time : " + trip_time
                        + " message : " + message
                        + " isGCM : " + isGCM);

        if (context != null) {
            Intent intent = new Intent(GCMUtils.UPDATE_ORDER_STATUS);
            intent.putExtra(EXTRA_ORDER_STATE, status);
            intent.putExtra(EXTRA_ORDER_ID, orderId);
            if(trip_time != null)
                intent.putExtra(EXTRA_ORDER_TIME, trip_time);
            if(price != null)
                intent.putExtra(EXTRA_ORDER_PRICE, price);
            if(bonus != null)
                intent.putExtra(EXTRA_ORDER_BONUS, bonus);
            if(message != null)
                intent.putExtra(EXTRA_ORDER_MESSAGE, message);
            intent.putExtra(EXTRA_IS_GCM, isGCM);

            context.sendBroadcast(intent);
        }
    }

    /**
     * Method for updating order UI by orderId,status, add. data
     * @param context
     * @param orders
     */
    public static void updateOrderUI(final Context context,
                                     final List<OrderStatusData> orders) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: GCMUtils.updateOrderUI : orders : " + orders);

        if (context != null && orders != null) {
            Intent intent = new Intent(GCMUtils.UPDATE_ORDER_STATUS);
            intent.putParcelableArrayListExtra(EXTRA_ORDER_LIST, (ArrayList<? extends Parcelable>) orders);
            context.sendBroadcast(intent);
        }
    }

}
