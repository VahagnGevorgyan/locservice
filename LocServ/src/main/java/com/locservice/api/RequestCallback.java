package com.locservice.api;

import com.locservice.CMAppGlobals;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class RequestCallback<T> implements Callback<T> {

    private static final String TAG = RequestCallback.class.getSimpleName();

    protected RequestListener<T> listener;
    private ICallBack mContext;

    public RequestCallback(ICallBack context, final int requestTypes) {
        this.mContext = context;

        this.listener = new RequestListener<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {
                if(CMAppGlobals.DEBUG) Logger.i(TAG, "::: RequestCallback.onSuccess : " + requestTypes + " : RESPONSE : " + response);

                if (mContext != null)
                    mContext.onSuccess(response.body(), requestTypes);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if(CMAppGlobals.DEBUG)Logger.e(TAG, "::: RequestCallback.onFailure : " + requestTypes + " : " + t.getMessage());

                if (mContext != null)
                    mContext.onFailure(t, requestTypes);
            }

        };
    }

    public RequestCallback(RequestListener<T> listener){
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            this.listener.onSuccess(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        this.listener.onFailure(call, t);
    }


}