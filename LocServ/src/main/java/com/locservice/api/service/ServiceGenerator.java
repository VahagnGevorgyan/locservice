package com.locservice.api.service;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.factory.GsonConverter;
import com.locservice.api.factory.HeaderHelper;
import com.locservice.api.factory.HeaderType;
import com.locservice.api.factory.StringConverterFactory;
import com.locservice.utils.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;



/**
 * Created by Vahagn Gevorgyan
 * 18 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ServiceGenerator {

    private static final String TAG = ServiceGenerator.class.getSimpleName();

    public static final String API_BASE_URL = "BASE_URL";
    public static final String API_FILE_URL = "FILE_URL";
    public static final String API_GOOGLE_MAPS_URL = "https://maps.googleapis.com";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverter.buildGsonConverter())
                    .baseUrl(API_BASE_URL);

    // No need to instantiate this class.
    private ServiceGenerator() {
    }

//    private static Authenticator getBasicAuthenticator(final String userName, final String password) {
//        return new Authenticator() {
//            @Override
//            public Request authenticate(Route route, Response response) throws IOException {
//                String credential = Credentials.basic(userName, password);
//                return response.request().newBuilder().header("Authorization", credential).build();
//            }
//        };
//    }

    public static <S> S createService(Class<S> serviceClass) {

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHtppClient = okHttpBuilder.addInterceptor(
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .headers(HeaderHelper.getJsonHeader(HeaderType.APPLICATION_JSON, ""))
                                .build();
//                .authenticator(getBasicAuthenticator(userName, password))
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        builder.client(okHtppClient);

        Retrofit adapter = builder.build();

        return adapter.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, ApiHostType apiUrlTypes, String url) {

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        okHttpBuilder.addInterceptor(httpLoggingInterceptor);
        okHttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .headers(HeaderHelper.getJsonHeader(HeaderType.APPLICATION_JSON, ""))
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient okHttpClient = okHttpBuilder
                .retryOnConnectionFailure(true)
                .build();

        builder.client(okHttpClient);


        switch (apiUrlTypes) {
            case API_FILE_URL:
            {
                builder.baseUrl(API_FILE_URL);
            }
                break;
            case API_BASE_URL:
            {
                builder.baseUrl(API_BASE_URL);
            }
                break;
            case API_GOOGLE_MAPS:
            {
                builder.baseUrl(API_GOOGLE_MAPS_URL);
            }
                break;
            case API_NO_URL:
            {
                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: ServiceGenerator.API_NO_URL : url : " + url);
            }
            case API_CUSTOM_URL:
            {
                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: ServiceGenerator.API_CUSTOM_URL : url : " + url);

                builder.addConverterFactory(new StringConverterFactory());

                okHttpBuilder.addInterceptor(httpLoggingInterceptor);
                okHttpBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .headers(HeaderHelper.getJsonHeader(HeaderType.APPLICATION_JSON, ""))
                                .build();
                        return chain.proceed(request);
                    }
                });
                OkHttpClient httpClient = okHttpBuilder.retryOnConnectionFailure(true).build();
                builder.client(httpClient);
            }
        }

        Retrofit adapter = builder.build();

        return adapter.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, final String token) {

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        okHttpBuilder.addInterceptor(httpLoggingInterceptor);
        okHttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .headers(HeaderHelper.getJsonHeader(HeaderType.APPLICATION_JSON, ""))
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient okHttpClient = okHttpBuilder.build();
        builder.client(okHttpClient);

        Retrofit adapter = builder.build();

        return adapter.create(serviceClass);
    }

}
