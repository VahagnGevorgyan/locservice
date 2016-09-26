package com.locservice.protocol;


public interface ICallBack {

	void onFailure(Throwable t, int requestType);
	
	void onSuccess(Object response, int requestType);
	
}
