package com.locservice.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.DriverInfo;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.DriverFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.utils.ActivityTypes;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;


public class CommonActivity extends BaseActivity implements ICallBack {
	
	private static final String TAG = ProfileActivity.class.getSimpleName();
	private Fragment currentFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_common);
		
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonActivity.onCreate ");

		// check network status
		if (Utils.checkNetworkStatus(this)) {
			setActivityByType();

			//change status bar color
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.setStatusBarColor(Color.parseColor("#00ffffff"));
			}
		}
	}

	/**
	 * Method for setting activity by type
	 */
	public void setActivityByType() {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			if(extras.containsKey(CMAppGlobals.ACTIVITY_TYPE)) {
				ActivityTypes activityType = (ActivityTypes) extras.get(CMAppGlobals.ACTIVITY_TYPE);
				switch (activityType) {
				case PROFILE:
					// set profile fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.PROFILE, null);
					break;
				case REWARDS:
					// set rewards fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.REWARDS, null);
					break;
				case ORDER_HISTORY:
					// set order history fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.ORDER_HISTORY, null);
					break;
				case DRIVER:
					// set driver fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.DRIVER_INFO, null);
					break;
				case SOCIAL_NETWORK:
					// set social network fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.SOCIAL_NETWORK, null);
					break;
				case FAVORITE_ADDRESS:
					// set favorite address fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.FAVORITE_ADDRESSES, null);
					break;
				case SEARCH:
					// set search fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.SEARCH, null);
					break;
				case CHAT:
					// set search fragment
					currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.CHAT, null);
					break;
				default:
					break;
				}
			}
		}
		
	} // end method setActivityByType

	public Fragment getCurrentFragment() {
		return currentFragment;
	}

	@Override
	public void onFailure(Throwable error, int requestType) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonActivity.OnFailure : requestType : " + requestType + " : error : " + error);

	}

	@Override
	public void onSuccess(Object response, int requestType) {
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonActivity.OnSuccess : requestType : " + requestType + " : object : " + response);
		if (response != null) {
			switch (requestType) {
				case RequestTypes.REQUEST_GET_DRIVER_INFO:
					if (response instanceof DriverInfo) {

						DriverInfo driverInfo = (DriverInfo) response;
						if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CommonActivity.OnSuccess : REQUEST_GET_DRIVER_INFO : driverInfo : " + driverInfo);
						if (currentFragment != null && currentFragment instanceof DriverFragment) {
							((DriverFragment)currentFragment).setmDriverInfo(driverInfo);
							((DriverFragment)currentFragment).setSlidePanelParameters();
						}
					}
					break;
			}
		}
	}
}
