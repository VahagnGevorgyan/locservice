package com.locservice.social;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.Logger;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.Collections;

/**
 * Created by Vahagn Gevorgyan
 * 11 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class FBHelper {

    private static final String TAG = FBHelper.class.getSimpleName();

    private CallbackManager callbackManager;
    private Fragment mFragment;
    private FBListener mFBListener;

    public interface FBListener {
        void doInFBCallback(JSONObject object);

        void getFBCurrentAccessToken(AccessToken currentAccessToken);
    }

    private AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook FBHelper.onCurrentAccessTokenChanged FB currentAccessToken : " + currentAccessToken);

            mFBListener.getFBCurrentAccessToken(currentAccessToken);
        }
    };

    public FBHelper(LoginButton loginButton, Fragment fragment, FBListener fbListener) {
        mFragment = fragment;
        mFBListener = fbListener;
        loginFBAccount(loginButton);
    }

    /**
     * Method for login in facebook
     * @param loginButton - login button control
     */
    private void loginFBAccount(LoginButton loginButton) {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        if (mFragment != null) {
            loginButton.setFragment(mFragment);
        }
        // Other app specific specialization
        loginButton.setReadPermissions(Collections.singletonList("public_profile, user_posts, email, user_birthday"));
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: Facebook onSuccess loginResult : " + loginResult.getAccessToken().getUserId());
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: Facebook onCompleted response : " + response.toString());
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: Facebook onCompleted object : " + object.toString());

                                mFBListener.doInFBCallback(object);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: Facebook onError : " + exception.getMessage());
            }
        });

    } // end method loginFBAccount

    /**
     * Method is used for logout FB account
     */
    public static void logoutFBAccount() {
        if(AccessToken.getCurrentAccessToken()!=null) {
            LoginManager.getInstance().logOut();
        }

    } //end method logoutFBAccount

    /**
     * Method for checking is FB logged in
     * @return - is fb logged in
     */
    public static boolean isFBLoggedIn () {
        return AccessToken.getCurrentAccessToken() != null;
    }

    /**
     * Method for getting callbackManager for login
     * @return callbackManager
     */
    public CallbackManager getCallbackManagerForLogin() {
        return callbackManager;

    } // end method getCallbackManagerForLogin

    /**
     * Method for creating callbackManager
     * @return callbackManager
     */
    public static CallbackManager createCallbackManager() {
        return CallbackManager.Factory.create();

    } // end method createCallbackManager

    /**
     * Method for getting share link content
     * @return - share link content
     */
    public static ShareLinkContent getShareLinkContent(Context context) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {

            return new ShareLinkContent.Builder()
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setImageUrl(
                            Uri.parse(CMAppGlobals.LOC_SERVICE_PICTURE_LINK))
                    .setContentUrl(Uri.parse(CMAppGlobals.LOC_SERVICE_LINK)).build();
        }

        return null;

    } // end method getShareLinkContent

    public interface FBShareCallback {
        void onSuccessFBShare(Sharer.Result result);
        void onCancelFBShare();
        void onErrorFBShare(FacebookException error);
    }

    /**
     * Method for showing sharing dialog
     * @param activity - Activity context
     * @param callbackManager - FB callback Manager
     * @param shareContent - FB sharing content
     */
    public static void showShareDialog(Activity activity, CallbackManager callbackManager, ShareContent shareContent, final FBShareCallback fbShareCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook  shareDialog.onSuccess : result : " + result);
                fbShareCallback.onSuccessFBShare(result);
            }

            @Override
            public void onCancel() {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook  shareDialog.onCancel");
                fbShareCallback.onCancelFBShare();
            }

            @Override
            public void onError(FacebookException error) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: Facebook  shareDialog.onError : error : " + error.getMessage());
                fbShareCallback.onErrorFBShare(error);
            }
        });

        shareDialog.show(shareContent);

    } // end method showShareDialog

    /**
     * Method for init FB
     * @param context - context
     */
    public static void initFb(Context context) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());

    } // end method initFb

}
