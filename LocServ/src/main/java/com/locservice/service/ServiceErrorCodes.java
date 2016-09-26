package com.locservice.service;

public class ServiceErrorCodes {
	
	public static final int CM_REQUEST_SUCCESS = 0;
	public static final int CM_REQUEST_FAILED = CM_REQUEST_SUCCESS + 1;
	public static final int CM_REQUEST_FAILED_TIMEOUT = CM_REQUEST_SUCCESS + 2;
	public static final int CM_REQUEST_FAILED_NO_NETWORK = CM_REQUEST_SUCCESS + 3;
	public static final int CM_REQUEST_INVALID_UID = CM_REQUEST_SUCCESS + 4;
	public static final int CM_REQUEST_PARSING_ERROR = CM_REQUEST_SUCCESS + 5;

}
