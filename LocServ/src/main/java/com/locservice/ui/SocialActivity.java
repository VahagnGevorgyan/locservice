package com.locservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.ResultData;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.fragments.SocialNetworkFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.helpers.MenuHelper;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ImageHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.vk.sdk.VKSdk;



public class SocialActivity extends BaseActivity implements ICallBack {

    private static final String TAG = SocialActivity.class.getSimpleName();

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

//        String[] fingerprint = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: fingerprint : " + fingerprint[0]);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // set profile fragment
            currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.SOCIAL_NETWORK, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, ((SocialNetworkFragment) currentFragment).getVkCallback())) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (((SocialNetworkFragment)currentFragment).getVkHelper() != null && ((SocialNetworkFragment)currentFragment).getVkHelper().isVKLoggedIn()) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialActivity.onActivityResult VK login");
            setResult(Activity.RESULT_OK);
        } else if (((SocialNetworkFragment)currentFragment).getVkHelper() != null && !((SocialNetworkFragment)currentFragment).getVkHelper().isVKLoggedIn()) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: SocialActivity.onActivityResult VK logout");
            LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_VK_ID, null);
            ((SocialNetworkFragment)currentFragment).getTextViewVKName().setText("");

        }
    }

    @Override
    public void onFailure(Throwable error, int requestType) {

    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
                    if(response instanceof ResultData) {
                        ResultData resultData = (ResultData) response;
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess REQUEST_UPDATE_CLIENT_INFO resultData : " + resultData);
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileActivity.onSuccess REQUEST_UPDATE_CLIENT_INFO result : " + resultData.getResult());

                        if (resultData.getResult() != null && resultData.getResult().equals("1")) {
                            ClientInfo currentClientInfo = ((SocialNetworkFragment) currentFragment).getCurrentClientInfo();
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: MainActivity.onSuccess REQUEST_GET_CLIENT_INFO " +
                                    ": clientInfo : " + currentClientInfo +
                                    ":  name : " + currentClientInfo.getName() +
                                    ": email : " + currentClientInfo.getEmail() +
                                    ": photoLink : " + currentClientInfo.getPhotoLink());

                            if (currentClientInfo.getPhotoLink() != null && !currentClientInfo.getPhotoLink().isEmpty()){
                                ImageHelper.loadAvatarByLoader(this, currentClientInfo.getPhotoLink(), new ImageView(this));
                            }
                            if (currentClientInfo.getName() != null && !currentClientInfo.getName().isEmpty()) {
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_NAME, currentClientInfo.getName());
                                MenuHelper.getInstance().setProfileName(SocialActivity.this, currentClientInfo.getName());
                            }
                            if (currentClientInfo.getEmail() != null && !currentClientInfo.getEmail().isEmpty()) {
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_EMAIL, currentClientInfo.getEmail());
                            }
                            if (currentClientInfo.getBonus() != null && !currentClientInfo.getBonus().isEmpty()) {
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, currentClientInfo.getBonus());
                                MenuHelper.getInstance().setRewards(this, currentClientInfo.getBonus());
                            }
                            if (currentClientInfo.getFbId() != null) {
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_FACEBOOK_ID, currentClientInfo.getFbId());
                                ((SocialNetworkFragment) currentFragment).setFBClientInfo(currentClientInfo);
                            }
                            if (currentClientInfo.getVkId() != null) {
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_VK_ID, currentClientInfo.getVkId());
                                ((SocialNetworkFragment) currentFragment).setVKClientInfo(currentClientInfo);

                            }
                            setResult(RESULT_OK);

                        } else {
                            Toast.makeText(SocialActivity.this, R.string.alert_update_client_info, Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
            }
        }
    }

    /**
     * Method for getting currentFragment
     * @return
     */
    public Fragment getCurrentFragment() {
        return currentFragment;

    } // end method getCurrentFragment
}
