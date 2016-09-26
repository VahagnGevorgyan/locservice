package com.locservice.api.factory;

import okhttp3.Headers;

/**
 * Created by Vahagn Gevorgyan
 * 17 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class HeaderHelper {

    /**
     * Method for getting headers by header type
     * @param headerType - header type
     * @param authToken - auth token
     * @return - headers list
     */
    public static Headers getJsonHeader(HeaderType headerType, String authToken) {
        Headers.Builder builder = new Headers.Builder();
        switch (headerType) {
            case APPLICATION_JSON:
                builder.add("Content-Type", "application/json");
                builder.add("Accept", "application/json");
                break;
            case APPLICATION_FORM:
                builder.add("Content-Type", "application/x-www-form-urlencoded");
                break;
        }
        if (authToken != null && !authToken.isEmpty()) {
            builder.add("X-MY-Auth", authToken);
        }
        return builder.build();

    } // end method getJsonHeader

}
