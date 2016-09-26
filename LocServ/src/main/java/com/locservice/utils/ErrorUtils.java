package com.locservice.utils;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.APIError;
import com.google.gson.Gson;

/**
 * Created by Vahagn Gevorgyan
 * 21 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ErrorUtils {

    private static final String TAG = ErrorUtils.class.getSimpleName();

    public static <T> boolean hasError(Context context, T response, String requestType) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : requestType : " + requestType);

        if(response instanceof APIError) {
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : true ");

            APIError error = (APIError) response;
//            if(error.getCode() != null && error.getErrors() != null && error.getErrors().size() > 0) {
            if(error.getMessage() != null && !error.getMessage().equals("")) {
//                String errorMessage = error.getErrors().get(0);
//                if(error.getAdd_info() != null && error.getAdd_info().size() > 0) {
//                    errorMessage += ":\n\n";
//                    errorMessage += error.getAdd_info().get(0);
//                }
                String errorMessage = error.getMessage();
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : errorMessage : " + errorMessage);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : code : " + error.getCode());
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();

                return true;
            } else {
                Gson gson = new Gson();
                String objJson = gson.toJson(response);
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : objJson : " + objJson);
                // print response toast
                if(CMAppGlobals.API_TOAST)
                    Toast.makeText(context, "Response : " + requestType + " : {\n"
                                    + objJson
                                    + "}"
                            , Toast.LENGTH_SHORT).show();
            }
        } else {
            Gson gson = new Gson();
            String objJson = gson.toJson(response);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ErrorUtils.hasError : objJson : " + objJson);
            // print response toast
            if(CMAppGlobals.API_TOAST)
                Toast.makeText(context, "Response : " + requestType + " : {\n"
                                + objJson
                                + "}"
                        , Toast.LENGTH_SHORT).show();
        }
        return false;

    } // end method hasError

}
