package com.locservice.api.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vahagn Gevorgyan
 * 17 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GsonConverter {

    public static GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        return GsonConverterFactory.create(gson);
    }

}
