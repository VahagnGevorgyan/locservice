package com.locservice.api.entities;

import android.graphics.Bitmap;

/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Chat {
	
	private String message; //comment
	private String time; // date
	private boolean isUser; // byuser
	private String operatorName;
	private String serviceImagePath;
	private String userImagePath;
	private String doSomethingText;
	private Bitmap serviceImageBitmap;
	private Bitmap userImageBitmap;

	
	public String getDoSomethingText() {
		return doSomethingText;
	}
	public void setDoSomethingText(String doSomethingText) {
		this.doSomethingText = doSomethingText;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getServiceImagePath() {
		return serviceImagePath;
	}
	public void setServiceImagePath(String serviceImagePath) {
		this.serviceImagePath = serviceImagePath;
	}
	public String getUserImagePath() {
		return userImagePath;
	}
	public void setUserImagePath(String userImagePath) {
		this.userImagePath = userImagePath;
	}
	public Bitmap getServiceImageBitmap() {
		return serviceImageBitmap;
	}
	public void setServiceImageBitmap(Bitmap serviceImageBitmap) {
		this.serviceImageBitmap = serviceImageBitmap;
	}
	public Bitmap getUserImageBitmap() {
		return userImageBitmap;
	}
	public void setUserImageBitmap(Bitmap userImageBitmap) {
		this.userImageBitmap = userImageBitmap;
	}
	
	
	

}
