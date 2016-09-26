package com.locservice.protocol;

public interface IWsCallBack {

    void onWsFailure(Exception error);

    void onWsSuccess(Object response);

    void onWsOpen();

    void onWsClose(int code, String reason);

}
