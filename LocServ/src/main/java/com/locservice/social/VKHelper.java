package com.locservice.social;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.Logger;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vahagn Gevorgyan
 * 11 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class VKHelper {

    private static final String TAG = VKHelper.class.getSimpleName();

    private Activity mContext;
    private VKAccessToken vkAccessToken;
    private VKListener vkListener;

    public static final String[] vkScopesLogin = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS,
            VKScope.EMAIL,
            VKScope.OFFLINE
    };

    public static final String[] vkScopesShare = new String[]{
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.OFFLINE
    };

    public interface VKListener {
        void doInVKCallback(VKAccessToken vkAccessToken, JSONObject vkResponse);
    }

    public VKHelper(Activity context, VKListener vkListener) {
        this.mContext = context;
        this.vkListener = vkListener;
    }

    private VKCallback vkCallback = new VKCallback<VKAccessToken>() {
        @Override
        public void onResult(VKAccessToken vkAccessToken) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: VK VKCallback onResult res : " + vkAccessToken);
            VKHelper.this.vkAccessToken = vkAccessToken;
            getVKProfileData(vkAccessToken);
        }


        @Override
        public void onError(VKError error) {
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: VK VKCallback onError error : " + error.errorMessage);
        }
    };

    private VKRequest.VKRequestListener vkRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: VK VKRequestListener onComplete response : " + response);
            JSONObject vkJson = response.json;
            try {
                JSONArray jsonArray = vkJson.getJSONArray("response");
                JSONObject vkResponse = jsonArray.getJSONObject(0);
                if (vkListener != null)
                    vkListener.doInVKCallback(vkAccessToken, vkResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            super.attemptFailed(request, attemptNumber, totalAttempts);
            if (CMAppGlobals.DEBUG)
                Logger.i(TAG, ":: VK VKRequestListener onComplete request : " + request);
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            if (CMAppGlobals.DEBUG)
                Logger.e(TAG, ":: VK VKRequestListener onError error : " + error.errorMessage);
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
            super.onProgress(progressType, bytesLoaded, bytesTotal);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK VKRequestListener onProgress");
        }
    };

    /**
     * Mehod for getting vk profile data
     *
     * @param vkAccessToken
     */
    public void getVKProfileData(VKAccessToken vkAccessToken) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: VK getVKProfileData vkAccessToken : " + vkAccessToken);

        VKParameters params = VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,photo_200");
        params.put("client_id", vkAccessToken.userId);
        params.put("client_secret", vkAccessToken.secret);
        VKRequest request = VKApi.users().get(params);
        request.executeWithListener(vkRequestListener);

    } // end method getVKProfileData

    /**
     * Method for login in vk account
     */
    public void loginVKAccount() {
        VKSdk.login(mContext, vkScopesLogin);

    } // end method loginVKAccount

    /**
     * Method for logout form vk account
     */
    public static void logoutVKAccount() {
        if (isVKLoggedIn()) {
            VKSdk.logout();
        }

    } // end method logoutVKAccount

    /**
     * Method for checking is vk logged in
     *
     * @return
     */
    public static boolean isVKLoggedIn() {
        return VKSdk.isLoggedIn();

    } // end method isVKLoggedIn

    /**
     * Method for getting vkListener
     *
     * @return vkListener
     */
    public VKListener getVkListener() {
        return vkListener;

    } // end method getVkListener

    /**
     * Method for setting vkListener
     *
     * @param vkListener
     */
    public void setVkListener(VKListener vkListener) {
        this.vkListener = vkListener;

    } // end method setVkListener

    /**
     * Method for getting vkCallback
     *
     * @return vkCallback
     */
    public VKCallback getVkCallback() {
        return vkCallback;

    }// end method getVkCallback

    /**
     * Method for setting vkCallback
     *
     * @param vkCallback
     */
    public void setVkCallback(VKCallback vkCallback) {
        this.vkCallback = vkCallback;

    } // end method setVkCallback

    public static void shareWithDialog(final Bitmap bitmap, FragmentManager fragmentManager, Activity context) {
        VKPhotoArray photos = new VKPhotoArray();
//        photos.add(new VKApiPhoto("photo-47200925_314622346"));
        VKShareDialogBuilder builder = new VKShareDialogBuilder();
        builder.setText(context.getString(R.string.str_vk_share_text));
//        builder.setUploadedPhotos(photos);
        builder.setAttachmentImages(new VKUploadImage[]{
                new VKUploadImage(bitmap, VKImageParameters.pngImage())
        });
        builder.setAttachmentLink("LocService", "some_link");
        builder.setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
            @Override
            public void onVkShareComplete(int postId) {
                // recycle bitmap if need
            }

            @Override
            public void onVkShareCancel() {
                // recycle bitmap if need
            }

            @Override
            public void onVkShareError(VKError error) {
                // recycle bitmap if need
            }
        });
        builder.show(fragmentManager, "VK_SHARE_DIALOG");
    }
}
