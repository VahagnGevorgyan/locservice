package com.locservice.api.request;


import com.locservice.BuildConfig;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.LocaleManager;

/**
 * Created by Vahagn Gevorgyan
 * 19 November 2015
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class WebRequest {

	private String auth_token;
	private String phone_os;
	private String method;
	private String locale;
	private String ver;

	public WebRequest(String method) {
	    this.auth_token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
		this.phone_os = "android";
		this.locale = LocaleManager.getLocaleLang();
		this.method = method;
		this.ver = BuildConfig.VERSION_NAME;
	}

	public WebRequest(String method, String locale) {
		this.auth_token = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
		this.phone_os = "android";
		this.locale = locale;
		this.method = method;
		this.ver = BuildConfig.VERSION_NAME;
	}

}
