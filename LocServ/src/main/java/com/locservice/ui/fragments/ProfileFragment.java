package com.locservice.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.manager.ClientManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.social.FBHelper;
import com.locservice.social.VKHelper;
import com.locservice.ui.CreditCardsActivity;
import com.locservice.ui.FavoriteActivity;
import com.locservice.ui.LanguageListActivity;
import com.locservice.ui.ProfileItemsActivity;
import com.locservice.ui.RegisterActivity;
import com.locservice.ui.RewardsActivity;
import com.locservice.ui.SocialActivity;
import com.locservice.ui.StatisticsActivity;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.controls.RoundedImageView;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.LocaleManager;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.ProfileType;
import com.locservice.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static com.locservice.utils.StringHelper.*;

public class ProfileFragment extends Fragment
		implements FBHelper.FBListener, VKHelper.VKListener {

	private static final String TAG = ProfileFragment.class.getSimpleName();
	
	private View rootView;

	private FragmentActivity mContext;

	private CustomTextView textViewName;
	private RoundedImageView imageViewAvatar;
	private CustomTextView textViewBonus;
	private CustomTextView textViewEmail;
	private CustomTextView textViewPhone;
	private CallbackManager callbackManager;
	private ClientInfo currentClientInfo;
	private FBHelper fbHelper;
	private VKCallback vkCallback;
	private VKHelper vkHelper;
	private RelativeLayout layoutTop;
	private SwitchCompat switchCompatSMS;
	private boolean isSwitchChecked;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null)
            return rootView;
		rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		mContext = getActivity();
	
		if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: OrderHistoryFragment.onCreateView ");


		// Checking is user registered
		LinearLayout layoutMainContent = (LinearLayout) rootView.findViewById(R.id.layoutMainContent);
		RelativeLayout layoutRegisterContent = (RelativeLayout) rootView.findViewById(R.id.layoutRegisterContent);
		String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
		if (!auth_token.equals("")) {
			layoutMainContent.setVisibility(View.VISIBLE);
			layoutRegisterContent.setVisibility(View.GONE);
		} else {
			layoutMainContent.setVisibility(View.GONE);
			layoutRegisterContent.setVisibility(View.VISIBLE);
			if (CMApplication.hasNavigationBar(mContext)) {
				layoutRegisterContent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
			}
			RelativeLayout layoutGoToRegister = (RelativeLayout) rootView.findViewById(R.id.layoutGoToRegister);
			layoutGoToRegister.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(mContext, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
				}
			});
		}
		
		// set events
		setEventListeners();
		// Set default client info
		setDefaultClientInfo();

		LinearLayout container1 = (LinearLayout) rootView.findViewById(R.id.container_settings);

		if(CMApplication.hasNavigationBar(mContext)) {
			container1.setPadding(0, 0, 0, CMApplication.dpToPx(48));
		}
		return rootView;
	}
	
	/**
	 * Method for setting all event listeners
	 */
	private void setEventListeners() {

		final LinearLayout layoutBack = (LinearLayout) rootView.findViewById(R.id.layoutBack);
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

		RelativeLayout layoutSocialNetwork = (RelativeLayout) rootView.findViewById(R.id.layoutSocialNetwork);
		layoutSocialNetwork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Open social network
				Intent intent = new Intent(mContext, SocialActivity.class);
				mContext.startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_INFO);
			}
		});

		layoutTop = (RelativeLayout) rootView.findViewById(R.id.layoutTop);
		if (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), "").isEmpty()
				&& LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID.key(), "").isEmpty()) {
			layoutTop.setVisibility(View.VISIBLE);

			// Login in Facebook
			LoginButton facebookLoginButton = (LoginButton) rootView.findViewById(R.id.facebookLoginButton);
			fbHelper = new FBHelper(facebookLoginButton, ProfileFragment.this, ProfileFragment.this);
			callbackManager = fbHelper.getCallbackManagerForLogin();

			// Login in VK
			TextView textViewVK = (TextView) rootView.findViewById(R.id.textViewVK);
			vkHelper = new VKHelper(mContext, ProfileFragment.this);
			vkCallback = vkHelper.getVkCallback();
			textViewVK.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// check network status
					if (Utils.checkNetworkStatus(mContext)) {
						vkHelper.loginVKAccount();
					}
				}
			});

		} else {
			layoutTop.setVisibility(View.GONE);
		}

		RelativeLayout layoutFavorite = (RelativeLayout) rootView.findViewById(R.id.layoutFavorite);
		layoutFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Open favorite address
				Intent intent = new Intent(mContext, FavoriteActivity.class);
				mContext.startActivity(intent);
			}
		});

		RelativeLayout layoutLanguage = (RelativeLayout) rootView.findViewById(R.id.layoutLanguage);
		layoutLanguage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open language list
				startActivity(new Intent(mContext, LanguageListActivity.class));
			}
		});
		CustomTextView textViewLanguage = (CustomTextView) rootView.findViewById(R.id.textViewLanguage);
		textViewLanguage.setText(LocaleManager.getLocaleLang());

		RelativeLayout layoutName = (RelativeLayout) rootView.findViewById(R.id.layoutName);
		layoutName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProfileItemsActivity.class);
				intent.putExtra(CMAppGlobals.EXTRA_CHANGE_PROFILE_ITEM, ProfileType.PROFILE_NAME);
				startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_ITEM);

			}
		});

		RelativeLayout layoutEmail = (RelativeLayout) rootView.findViewById(R.id.layoutEmail);
		layoutEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProfileItemsActivity.class);
				intent.putExtra(CMAppGlobals.EXTRA_CHANGE_PROFILE_ITEM, ProfileType.PROFILE_EMAIL);
				startActivityForResult(intent, CMAppGlobals.REQUEST_CHANGE_PROFILE_ITEM);

			}

		});
		RelativeLayout layoutPhoto = (RelativeLayout) rootView.findViewById(R.id.layoutPhoto);
		layoutPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// check network status
				if (Utils.checkNetworkStatus(mContext)) {

					showChangePhotoDialog();
				}
			}
		});

		RelativeLayout layoutBankCards = (RelativeLayout) rootView.findViewById(R.id.layoutBankCards);
		layoutBankCards.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, CreditCardsActivity.class));
			}
		});

		RelativeLayout layoutStatistics = (RelativeLayout) rootView.findViewById(R.id.layoutStatistics);
		layoutStatistics.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, StatisticsActivity.class));
			}
		});

		RelativeLayout layoutBonus = (RelativeLayout) rootView.findViewById(R.id.layoutBonus);
		layoutBonus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext, RewardsActivity.class), CMAppGlobals.REQUEST_CHANGE_PROFILE_ITEM);
			}
		});

		switchCompatSMS = (SwitchCompat) rootView.findViewById(R.id.switchCompatSMS);
		currentClientInfo = new ClientInfo();
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment : PROFILE_SMS_NOTIFICATION : "
				+ LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0));
		if (LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0) == 1) {
			isSwitchChecked = true;
			switchCompatSMS.setChecked(true);
		} else {
			isSwitchChecked = false;
			switchCompatSMS.setChecked(false);
		}
		switchCompatSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment : switchCompatSMS : onCheckedChanged : isChecked : " + isChecked);
				ClientManager clientManager = new ClientManager((ICallBack) mContext);
				isSwitchChecked = isChecked;
				if (isChecked) {
					currentClientInfo.setSmsNotification(1);
					clientManager.UpdateClientInfo("", "", 1, "", 1);
				} else {
					currentClientInfo.setSmsNotification(0);
					clientManager.UpdateClientInfo("", "", 0, "", 1);
				}
			}
		});

		RelativeLayout layoutSMS = (RelativeLayout) rootView.findViewById(R.id.layoutSMS);
		layoutSMS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment : layoutSMS : onClick : isSwitchChecked : " + isSwitchChecked);
				if (isSwitchChecked) {
					switchCompatSMS.setChecked(false);
				} else {
					switchCompatSMS.setChecked(true);
				}
			}
		});


	} // end method setEventListeners

	/**
	 * Method for setting client info
	 *
	 * @param clientInfo - client information
	 */
	public void setClientInfo(ClientInfo clientInfo) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment.setClientInfo clientInfo : " + clientInfo);

		if (clientInfo != null) {

			textViewName = (CustomTextView) rootView.findViewById(R.id.textViewName);
			if (clientInfo.getName() != null && !clientInfo.getName().isEmpty()) {
				textViewName.setText(clientInfo.getName());
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_NAME, clientInfo.getName());
			}
			imageViewAvatar = (RoundedImageView) rootView.findViewById(R.id.imageViewAvatar);
			if (clientInfo.getBase64Image() != null && !clientInfo.getBase64Image().isEmpty()) {
				imageViewAvatar.setImageBitmap(CMApplication.decodeBase64ToBitmap(clientInfo.getBase64Image()));
			}
			if (clientInfo.getPhotoLink() != null && !clientInfo.getPhotoLink().isEmpty()) {
				ImageHelper.loadAvatarByLoader(mContext, clientInfo.getPhotoLink(), imageViewAvatar);
			}
			textViewBonus = (CustomTextView) rootView.findViewById(R.id.textViewBonus);
			if (clientInfo.getBonus() != null && !clientInfo.getBonus().isEmpty()) {
				String strBonus = combineStrings(clientInfo.getBonus(), getString(R.string.str_ruble));
				textViewBonus.setText(strBonus);
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, clientInfo.getEmail());
			}
			textViewEmail = (CustomTextView) rootView.findViewById(R.id.textViewEmail);
			if (clientInfo.getEmail() != null && !clientInfo.getEmail().isEmpty()) {
				textViewEmail.setText(clientInfo.getEmail());
				LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_EMAIL, clientInfo.getEmail());
			}
			textViewPhone = (CustomTextView) rootView.findViewById(R.id.textViewPhone);
			String phoneNumber = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_FORMATTED_PHONE_NUMBER.key(), "");
			if (!phoneNumber.isEmpty()) {
				String strPhoneNumber = combineStrings("+", phoneNumber);
				textViewPhone.setText(strPhoneNumber);
			} else {
				String phone = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_PHONE_NUMBER.key(), "");
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileFragment ::: phone : " + phone);
				if (!phone.isEmpty()) {
					// create formatted phone number
					String formattedPhone = CommonHelper.getFormattedPhone(phone);
					if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileFragment ::: formattedPhone : " + formattedPhone);

					String strFormattedPhone = combineStrings("+", formattedPhone);
					textViewPhone.setText(strFormattedPhone);
				}
			}
			// store profile cards existence
			LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_HAS_CARDS, clientInfo.getHasCards());
		}

	} // end method setClientInfo

	/**
	 * Method for setting default client info
	 */
	public void setDefaultClientInfo() {
		textViewName = (CustomTextView) rootView.findViewById(R.id.textViewName);
		String name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
		if (!name.isEmpty()) {
			textViewName.setText(name);
		}
		imageViewAvatar = (RoundedImageView) rootView.findViewById(R.id.imageViewAvatar);
		String base64Image = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), "");
		if (!base64Image.isEmpty()) {
			imageViewAvatar.setImageBitmap(CMApplication.decodeBase64ToBitmap(base64Image));
		}
		textViewBonus = (CustomTextView) rootView.findViewById(R.id.textViewBonus);
		String bonus = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
		if (!bonus.isEmpty()) {
			String strBonus = combineStrings(bonus, getString(R.string.str_ruble));
			textViewBonus.setText(strBonus);
		}
		textViewEmail = (CustomTextView) rootView.findViewById(R.id.textViewEmail);
		String email = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "");
		if (!email.isEmpty()) {
			textViewEmail.setText(email);
		}
		textViewPhone = (CustomTextView) rootView.findViewById(R.id.textViewPhone);
		String phoneNumber = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_FORMATTED_PHONE_NUMBER.key(), "");
		if (!phoneNumber.isEmpty()) {
			String strPhoneNumber = combineStrings("+", phoneNumber);
			textViewPhone.setText(strPhoneNumber);
		} else {
			String phone = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_PHONE_NUMBER.key(), "");
			if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileFragment ::: phone : " + phone);
			if (!phone.isEmpty()) {
				// create formatted phone number
				String formattedPhone = CommonHelper.getFormattedPhone(phone);
				if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileFragment ::: formattedPhone : " + formattedPhone);
				// set formatted phone text
				String strFormattedPhone = combineStrings("+", formattedPhone);
				textViewPhone.setText(strFormattedPhone);
			}
		}

	} // end method setDefaultClientInfo

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment.onActivityResult requestCode : " + requestCode + " resultCode : " + resultCode + " data : " + data);

		if (callbackManager != null) {
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}
		try {
			if (data != null) {
				Bitmap imageBitmap = null;
				if (requestCode == CMAppGlobals.REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {

					ClientManager clientManager = new ClientManager((ICallBack) mContext);
					Bundle extras = data.getExtras();
					imageBitmap = (Bitmap) extras.get("data");
					clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), ""),
							LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), ""),
							LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
							CMApplication.encodeBitmapToBase64(imageBitmap), 0);
					currentClientInfo = new ClientInfo();
					currentClientInfo.setBase64Image(CMApplication.encodeBitmapToBase64(imageBitmap));
				}
				if (requestCode == CMAppGlobals.REQUEST_LOAD_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
					ClientManager clientManager = new ClientManager((ICallBack) mContext);
					final Uri imageUri = data.getData();
					imageBitmap = CMApplication.decodeSampledBitmap(mContext, imageUri, 100, 100);
					clientManager.UpdateClientInfo(LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), ""),
							LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), ""),
							LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
							CMApplication.encodeBitmapToBase64(imageBitmap), 0);
					currentClientInfo = new ClientInfo();
					currentClientInfo.setBase64Image(CMApplication.encodeBitmapToBase64(imageBitmap));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (fbHelper != null && fbHelper.isFBLoggedIn()) {
			if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileFragment.onActivityResult FB login");
			layoutTop.setVisibility(View.GONE);
		}
		if (requestCode == CMAppGlobals.REQUEST_CHANGE_PROFILE_ITEM) {
			setDefaultClientInfo();
			if (resultCode == Activity.RESULT_OK) {
				mContext.onBackPressed();
			}
		}

		if (requestCode == CMAppGlobals.REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
			mContext.setResult(Activity.RESULT_OK);
			mContext.onBackPressed();
		}


	} // end method onActivityResult

	/**
	 * Method for getting current client info
	 * @return currentClientInfo
	 */
	public ClientInfo getCurrentClientInfo() {
		return currentClientInfo;

	} // end method getCurrentClientInfo

	/**
	 * Method for setting current client info
	 * @param currentClientInfo
	 */
	public void setCurrentClientInfo(ClientInfo currentClientInfo) {
		this.currentClientInfo = currentClientInfo;

	} // end method setCurrentClientInfo

	public VKCallback getVkCallback() {
		return vkCallback;
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
					String photoLink = combineStrings("https://graph.facebook.com/", object.getString("id"), "/picture?type=large");
					currentClientInfo.setPhotoLink(photoLink);
					currentClientInfo.setFbId(object.getString("id"));
					ImageHelper.loadProfileAvatarByLoader(mContext, photoLink, imageViewAvatar);
				}
			} catch (JSONException e) {
				if (CMAppGlobals.DEBUG)
					Logger.e(TAG, ":: Facebook JSONException " + e.getMessage());
			}
		}

	} // end method doInFBCallback

	@Override
	public void getFBCurrentAccessToken(AccessToken currentAccessToken) {

	}

	@Override
	public void doInVKCallback(VKAccessToken vkAccessToken, JSONObject vkResponse) {
		if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK doInVKCallback vkAccessToken : " + vkAccessToken + " vkResponse : " + vkResponse);

		try {
			currentClientInfo = new ClientInfo();
			currentClientInfo.setEmail(vkAccessToken.email);
			currentClientInfo.setVkId(vkAccessToken.userId);

			String name = "";
			if (vkResponse.has("first_name") && vkResponse.getString("first_name") != null) {
				name = combineStrings(vkResponse.getString("first_name"), " ");
				currentClientInfo.setName(name);
			}
			if (vkResponse.has("last_name") && vkResponse.getString("last_name") != null) {
				name += vkResponse.getString("last_name");
				currentClientInfo.setName(name);
			}
			if (vkResponse.has("photo_200") && vkResponse.getString("photo_200") != null) {
				currentClientInfo.setPhotoLink(vkResponse.getString("photo_200"));
				ImageHelper.loadProfileAvatarByLoader(mContext, vkResponse.getString("photo_200"), imageViewAvatar);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	} // end method doInVKCallback

	/**
	 * Method for getting fbHelper
	 * @return fbHelper
	 */
	public FBHelper getFbHelper() {
		return fbHelper;

	} // end method getFbHelper

	/**
	 * Method for getting vkHelper
	 * @return vkHelper
	 */
	public VKHelper getVkHelper() {
		return vkHelper;

	} // end method getVkHelper

	/**
	 * Method for getting layoutTop
	 * @return layoutTop
	 */
	public RelativeLayout getLayoutTop() {
		return layoutTop;

	} // end method layoutTop

	/**
	 * Method for
	 */
	private void showChangePhotoDialog () {
		final String[] array = new String[]{ mContext.getString(R.string.str_take_photo), mContext.getString(R.string.str_choose_from_gallery)};
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
		builder.setItems(array, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						if (!PermissionUtils.ensurePermission(getActivity(), Manifest.permission.CAMERA, PermissionUtils.PERMISSION_REQUEST_CAMERA)) {
							return;
						}

						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
							startActivityForResult(takePictureIntent, CMAppGlobals.REQUEST_CAPTURE_IMAGE);
						}
						break;
					case 1:
						if (!PermissionUtils.ensurePermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtils.READ_EXTERNAL_STORAGE)) {
							return;
						}

						Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(intent, CMAppGlobals.REQUEST_LOAD_FROM_GALLERY);
						break;

				}
			}
		});
		builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();

	}


}
