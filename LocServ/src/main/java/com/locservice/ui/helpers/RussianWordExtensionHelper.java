package com.locservice.ui.helpers;

import android.content.Context;

import com.locservice.R;

/**
 * Created by Vahagn Gevorgyan
 * 15 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RussianWordExtensionHelper {

    /**
     * Method for getting russian trip word whit right extension
     * @param context
     * @param ordersCount
     * @return
     */
    public static String getTrip(Context context,int ordersCount) {
        String mTrip = "";
        if (ordersCount == 1) {
            mTrip = context.getString(R.string.str_trip_prefix) + context.getString(R.string.str_a_extension);
        } else if (ordersCount >= 2 && ordersCount <= 4){
            mTrip = context.getString(R.string.str_trip_prefix) + context.getString(R.string.str_i_extension);
        } else {
            mTrip = context.getString(R.string.str_trip_by_up_5);
        }

        return mTrip;

    } // end method getTrip;

    /**
     * Method for getting russian kilometer word whit right extension
     * @param context
     * @param distance
     * @return kilometer
     */
    public static String getKilometer(Context context, int distance) {
        String kilometer = "";
        if (distance == 1) {
            kilometer = context.getString(R.string.str_km_prifix);
        } else if (distance >= 2 && distance <= 4){
            kilometer = context.getString(R.string.str_km_prifix) + context.getString(R.string.str_a_extension);
        } else {
            kilometer = context.getString(R.string.str_km_prifix) + context.getString(R.string.str_ov_extension);
        }

        return kilometer;

    } // end method getKilometer;

    /**
     * Method for getting russian minute word whit right extension
     * @param context
     * @param minute
     * @return minuteString
     */
    public static String getMinute(Context context, int minute) {
        String minuteString = "";
        if (minute == 1) {
            minuteString = context.getString(R.string.str_minute_prefix)  + context.getString(R.string.str_a_extension);
        } else if (minute >= 2 && minute <= 4){
            minuteString = context.getString(R.string.str_minute_prefix) + context.getString(R.string.str_y_extension);
        } else {
            minuteString = context.getString(R.string.str_minute_prefix);
        }

        return minuteString;

    } // end method getKilometer;
}
