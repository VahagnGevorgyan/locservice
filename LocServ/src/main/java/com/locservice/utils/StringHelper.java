package com.locservice.utils;

import com.locservice.CMAppGlobals;

/**
 * Created by Vahagn Gevorgyan
 * 12 September 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class StringHelper {

    private static final String TAG = StringHelper.class.getSimpleName();

    /**
     * Method for combining String arrays to String object
     * @param strings - String array
     * @return - combined String
     */
    public static String combineStrings(String... strings) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: StringHelper.combineStrings ");

        StringBuilder stringBuilder = new StringBuilder();
        if(strings != null && strings.length > 0) {
            for (String itemString :
                    strings) {
                stringBuilder.append(itemString);
            }
        }

        return stringBuilder.toString();

    } // end method combineStrings
}
