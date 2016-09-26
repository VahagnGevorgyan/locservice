package com.locservice.api.request;

import com.locservice.application.LocServicePreferences;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetFavoriteRequest extends WebRequest {

    private String IdLocality;

    public GetFavoriteRequest() {
        super("getfavorite");
        this.IdLocality =  LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
    }

    public String getIdLocality() {
        return IdLocality;
    }

    public void setIdLocality(String idLocality) {
        IdLocality = idLocality;
    }
}
