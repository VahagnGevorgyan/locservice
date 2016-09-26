package com.locservice.api;


import retrofit2.Call;
import retrofit2.Response;

interface RequestListener<T> {

    void onSuccess(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable t);
}
