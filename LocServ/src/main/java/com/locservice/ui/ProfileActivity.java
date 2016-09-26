package com.locservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.ResultData;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.ProfileFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.Utils;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.vk.sdk.VKSdk;

public class ProfileActivity extends BaseActivity implements ICallBack{
	
	private static final String TAG = ProfileActivity.class.getSimpleName();
	private Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FacebookSdk.sdkInitialize(getApplicationContext());
		FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
		
		setContentView(R.layout.activity_common);

		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileActivity.onCreate ");

		// check network status
		if (Utils.checkNetworkStatus(this)) {
			// set profile fragment
			currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.PROFILE, null);
		}

	}

	@Override
	public void onFailure(Throwable error, int requestType) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onFailure :  requestType " + requestType +   " : error : " + error);
	}

	@Override
	public void onSuccess(Object response, int requestType) {
		if (response != null) {
			switch (requestType) {
				case RequestTypes.REQUEST_GET_CLIENT_INFO:
					if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CLIENT_INFO")) {
						if(response instanceof ClientInfo) {
							ClientInfo clientInfo = (ClientInfo) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_GET_CLIENT_INFO : clientInfo : " + clientInfo);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_GET_CLIENT_INFO : name : " + clientInfo.getName());
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_GET_CLIENT_INFO : email : " + clientInfo.getEmail());
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_GET_CLIENT_INFO : link : " + clientInfo.getPhotoLink());

							((ProfileFragment)currentFragment).setClientInfo(clientInfo);
						}
					}
					break;
				case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
					if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_UPDATE_CLIENT_INFO")) {
						if(response instanceof ResultData) {
							ResultData resultData = (ResultData) response;
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_UPDATE_CLIENT_INFO : resultData : " + resultData);
							if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_UPDATE_CLIENT_INFO : result : " + resultData.getResult());

							if (resultData.getResult() != null && resultData.getResult().equals("1")) {
								ClientInfo currentClientInfo = ((ProfileFragment) currentFragment).getCurrentClientInfo();

								if (currentClientInfo != null) {
									if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess : REQUEST_GET_CLIENT_INFO " +
											": clientInfo : " + currentClientInfo +
											":  name : " + currentClientInfo.getName() +
											": email : " + currentClientInfo.getEmail() +
											": photoLink : " + currentClientInfo.getPhotoLink() +
											": sms_notification : " + currentClientInfo.getSmsNotification());

									((ProfileFragment) currentFragment).setClientInfo(currentClientInfo);

									if (currentClientInfo.getPhotoLink() != null && !currentClientInfo.getPhotoLink().isEmpty()){
										ImageHelper.loadAvatarByLoader(this, currentClientInfo.getPhotoLink(), new ImageView(this));
									}
									if (currentClientInfo.getName() != null) {
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_NAME, currentClientInfo.getName());
										MenuHelper.getInstance().setProfileName(ProfileActivity.this, currentClientInfo.getName());
									}
									if (currentClientInfo.getEmail() != null && !currentClientInfo.getEmail().isEmpty()) {
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_EMAIL, currentClientInfo.getEmail());
									}
									if (currentClientInfo.getBonus() != null && !currentClientInfo.getBonus().isEmpty()) {
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, currentClientInfo.getBonus());
										MenuHelper.getInstance().setRewards(this, currentClientInfo.getBonus());
									}
									if (currentClientInfo.getFbId() != null)
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID, currentClientInfo.getFbId());
									if (currentClientInfo.getVkId() != null)
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_VK_ID, currentClientInfo.getVkId());

									if (currentClientInfo.getBase64Image() != null && !currentClientInfo.getBase64Image().isEmpty()) {
										LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE, currentClientInfo.getBase64Image());
									}

									LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION, currentClientInfo.getSmsNotification());
								}

							} else {
								Toast.makeText(ProfileActivity.this, R.string.alert_update_client_info, Toast.LENGTH_SHORT).show();
							}
						}
					}
				break;
			}
		}
	}

	public Fragment getCurrentFragment() {
		return currentFragment;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onActivityResult : requestCode : " + requestCode
							+ " : resultCode  : " + resultCode + " : data : " + data);

		if (requestCode == CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO) {
			((ProfileFragment)currentFragment).setDefaultClientInfo();
			if (resultCode == Activity.RESULT_OK) {
				((ProfileFragment)currentFragment).getLayoutTop().setVisibility(View.GONE);
			}

		}

		// VK
		if (!VKSdk.onActivityResult(requestCode, resultCode, data, ((ProfileFragment)currentFragment).getVkCallback())) {
			super.onActivityResult(requestCode, resultCode, data);
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onActivityResult  not VK");
		}
		if (((ProfileFragment)currentFragment).getVkHelper() != null && ((ProfileFragment)currentFragment).getVkHelper().isVKLoggedIn()) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onActivityResult VK login");
			((ProfileFragment)currentFragment).getLayoutTop().setVisibility(View.GONE);
			setResult(Activity.RESULT_OK);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		switch (requestCode) {
			case PermissionUtils.PERMISSION_REQUEST_CAMERA:

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
						startActivityForResult(takePictureIntent, CMAppGlobals.REQUEST_CAPTURE_IMAGE);
					}
					return;
				}

				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						View view = findViewById(R.id.container_main_profile);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
					}
				}
				break;

			case PermissionUtils.READ_EXTERNAL_STORAGE:

				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, CMAppGlobals.REQUEST_LOAD_FROM_GALLERY);
					return;
				}

				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						View view = findViewById(R.id.container_main_profile);
						if (view != null) {
							Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.settings), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
						}
						return;
					}
				}
				break;
		}
	}
}
