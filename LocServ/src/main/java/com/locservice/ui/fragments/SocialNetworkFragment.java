package com.locservice.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.entities.ClientInfo;
import com.locservice.application.LocServicePreferences;
import com.locservice.social.FBHelper;
import com.locservice.social.VKHelper;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class SocialNetworkFragment extends Fragment
		implements FBHelper.FBListener, VKHelper.VKListener
{
	
	private static final String TAG = SocialNetworkFragment.class.getSimpleName();
	
	private View rootView;

	private LinearLayout layoutBack;

	private RelativeLayout layoutFacebook;
	private RelativeLayout layoutVK;


	private LoginButton facebookLoginButton;
	private CallbackManager mCallbackManager;

	private VKCallback vkCallback;

	private FragmentActivity mContext;
	private CustomTextView textViewFacebookName;
	private CustomTextView textViewVKName;
	private FBHelper fbHelper;
	private VKHelper vkHelper;
	private ClientInfo currentClientInfo;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_social_network, container, false);
		mContext = getActivity();
	
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: SocialNetworkFragment.onCreateView ");
		// set events
		setEventListeners();
		//set client info
		setFBClientInfo(null);
		setVKClientInfo(null);
		
		return rootView;
	}

	/**
	 * Method for setting event listeners
	 */
	private void setEventListeners() {
		// FB login
		facebookLoginButton = (LoginButton) rootView.findViewById(R.id.facebookLoginButton);
		fbHelper = new FBHelper(facebookLoginButton, SocialNetworkFragment.this, SocialNetworkFragment.this);
		mCallbackManager = fbHelper.getCallbackManagerForLogin();


		layoutFacebook = (RelativeLayout) rootView.findViewById(R.id.layoutFacebook);

		fbHelper = new FBHelper(facebookLoginButton, this, this);
		mCallbackManager = fbHelper.getCallbackManagerForLogin();
		AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
				if (currentAccessToken != null) {
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook SocialNetworkFragment.onCurrentAccessTokenChanged FB currentAccessToken : " + currentAccessToken);
				} else {
					if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook SocialNetworkFragment.onCurrentAccessTokenChanged FB currentAccessToken : " + currentAccessToken);
				}
			}
		};

		// VK login
		layoutVK = (RelativeLayout) rootView.findViewById(R.id.layoutVK);
		vkHelper = new VKHelper(mContext, SocialNetworkFragment.this);
		vkCallback = vkHelper.getVkCallback();
		layoutVK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (vkHelper.isVKLoggedIn()) {
					vkHelper.logoutVKAccount();
					textViewVKName.setText("");
				} else {
					vkHelper.loginVKAccount();
					fbHelper.logoutFBAccount();
					textViewFacebookName.setText("");
					LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID, null);

				}

			}
		});

		layoutBack = (LinearLayout) rootView.findViewById(R.id.layoutBack);
		layoutBack.setOnTouchListener(new OnTouchListener() {
			ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return CommonHelper.setOnTouchImage(imageViewBack, event);
			}
		});
		layoutBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext != null)
					mContext.onBackPressed();
			}
		});

	} // end method setEventListeners
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);

		if (fbHelper != null && !fbHelper.isFBLoggedIn()) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialNetworkFragment.onActivityResult FB logout");
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID, null);
			textViewFacebookName.setText("");
		} if (fbHelper != null && fbHelper.isFBLoggedIn()) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialNetworkFragment.onActivityResult FB login");
			vkHelper.logoutVKAccount();
			textViewVKName.setText("");
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_VK_ID, null);
			mContext.setResult(Activity.RESULT_OK);
		}
	}


	@Override
	public void doInFBCallback(JSONObject object) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook doInFBCallback object : " + object);

		if (object != null) {
			currentClientInfo = new ClientInfo();
			try {
				if (object.has("name") && object.getString("name") != null) {
					currentClientInfo.setName(object.getString("name"));
				}
				if (object.has("email") && object.getString("email") != null) {
					currentClientInfo.setEmail(object.getString("email"));
				}
				if (object.has("id") && object.getString("id") != null) {
					String photoLink = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
					currentClientInfo.setPhotoLink(photoLink);
					currentClientInfo.setFbId(object.getString("id"));
					ImageHelper.loadSocialBitmapByLoader(mContext, photoLink, new ImageView(mContext));
				}
			} catch (JSONException e) {
				if (CMAppGlobals.DEBUG)
					Logger.e(TAG, ":: Facebook JSONException " + e.getMessage());
			}
		}

	} // end method doInFBCallback

	@Override
	public void getFBCurrentAccessToken(AccessToken currentAccessToken) {
		if (currentAccessToken == null) {
			textViewFacebookName.setText("");
		}
	} // end method getFBCurrentAccessToken

	@Override
	public void doInVKCallback(VKAccessToken vkAccessToken, JSONObject vkResponse) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK doInVKCallback vkAccessToken : " + vkAccessToken + " vkResponse : " + vkResponse);

		try {
			currentClientInfo = new ClientInfo();
			currentClientInfo.setEmail(vkAccessToken.email);
			currentClientInfo.setVkId(vkAccessToken.userId);

			String name = "";
			if (vkResponse.has("first_name") && vkResponse.getString("first_name") != null) {
				name = vkResponse.getString("first_name") + " ";
				currentClientInfo.setName(name);
			}
			if (vkResponse.has("last_name") && vkResponse.getString("last_name") != null) {
				name += vkResponse.getString("last_name");
				currentClientInfo.setName(name);
			}
			if (vkResponse.has("photo_200") && vkResponse.getString("photo_200") != null) {
				currentClientInfo.setPhotoLink(vkResponse.getString("photo_200"));
				ImageHelper.loadSocialBitmapByLoader(mContext, vkResponse.getString("photo_200"), new ImageView(mContext));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	} // end method doInVKCallback

	/**
	 * Method for getting vkCallback
	 * @return vkCallback
	 */
	public VKCallback getVkCallback() {
		return vkCallback;

	} // end method getVkCallback

	/**
	 * Method for getting vkHelper
	 * @return vkHelper
	 */
	public VKHelper getVkHelper() {
		return vkHelper;

	} // end method getVkHelper

	/**
	 *  Method for getting textViewVKName
	 * @return textViewVKName
	 */
	public CustomTextView getTextViewVKName() {
		return textViewVKName;

	} // end method getTextViewVKName

	/**
	 * Method for setting textViewVKName
	 * @param textViewVKName
	 */
	public void setTextViewVKName(CustomTextView textViewVKName) {
		this.textViewVKName = textViewVKName;

	} // end method setTextViewVKName

	/**
	 * Method for getting currentClientInfo
	 * @return
	 */
	public ClientInfo getCurrentClientInfo() {
		return currentClientInfo;

	}  // end method getCurrentClientInfo

	/**
	 * Method for setting currentClientInfo
	 * @param currentClientInfo
	 */
	public void setCurrentClientInfo(ClientInfo currentClientInfo) {
		this.currentClientInfo = currentClientInfo;

	} // end method setCurrentClientInfo

	/**
	 * Method for setting FB client info
	 * @param clientInfo
	 */
	public void setFBClientInfo(ClientInfo clientInfo) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialNetworkFragment.setFBClientInfo clientInfo : " + clientInfo);


		textViewFacebookName = (CustomTextView) rootView.findViewById(R.id.textViewFacebookName);

		if (clientInfo != null && clientInfo.getName()!= null) {
			textViewFacebookName.setText(clientInfo.getName());
		} else {
			if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID.key(), null) != null) {
				textViewFacebookName.setText(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), ""));
			} else {
				textViewFacebookName.setText("");
			}

		}

	} // end method setClientInfo

	/**
	 * Method for setting VK client info
	 * @param clientInfo
	 */
	public void setVKClientInfo(ClientInfo clientInfo) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialNetworkFragment.setVKClientInfo clientInfo : " + clientInfo);

		textViewVKName = (CustomTextView) rootView.findViewById(R.id.textViewVKName);
		if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), null) != null) {
			if (clientInfo != null) {
				textViewVKName.setText(clientInfo.getName());
			} else {
				textViewVKName.setText(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), ""));
			}
		} else {
			textViewVKName.setText("");
		}

	} // end method setClientInfo

	/**
	 * Method for getting fbHelper
	 * @return fbHelper
	 */
	public FBHelper getFbHelper() {
		return fbHelper;

	} // end method fbHelper

}
