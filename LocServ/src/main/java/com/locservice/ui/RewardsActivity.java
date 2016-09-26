package com.locservice.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.BonusInfo;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.entities.EnterCouponData;
import com.locservice.api.entities.RepostSocialData;
import com.locservice.api.manager.BonusManager;
import com.locservice.api.manager.ClientManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.social.FBHelper;
import com.locservice.social.VKHelper;
import com.locservice.ui.fragments.RewardsFragment;
import com.locservice.ui.helpers.FragmentHelper;
import com.locservice.ui.utils.FragmentTypes;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

public class RewardsActivity extends BaseActivity implements ICallBack, FBHelper.FBShareCallback {

    private static final String TAG = RewardsActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private VKCallback vkCallback;
    private Fragment currentFragment;
    private BonusManager bonusManager;

    private int current_social_id;

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FBHelper.initFb(RewardsActivity.this);
        callbackManager = FBHelper.createCallbackManager();
        initVk();
        setContentView(R.layout.activity_common);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
            if (!auth_token.isEmpty()) {
                // get bonuses info
                bonusManager = new BonusManager(this);
                bonusManager.GetBonusesInfo();
            }

            // set profile fragment
            currentFragment = FragmentHelper.getInstance().openFragment(this, FragmentTypes.REWARDS, null);
        }

    } // end method onCreate

    /**
     * Method for initializing vk and getting VK Access Token
     */
    private void initVk() {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.initVk");

        vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken vkAccessToken) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK : VKCallback.onResult vkAccessToken : " + vkAccessToken);

                shareVKWithDialog(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                        getSupportFragmentManager(),
                        RewardsActivity.this);
            }

            @Override
            public void onError(VKError error) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK : VKCallback.onResult error : " + error.errorMessage);
            }
        };

    } // end method initVk

    /**
     * Method for sharing post in VK whit dialog
     * @param bitmap - Sharing bitmap
     * @param fragmentManager - Support fragment manager
     * @param context - Activity
     */
    public void shareVKWithDialog(final Bitmap bitmap, FragmentManager fragmentManager, Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.shareVKWithDialog : context : " + context);

        VKShareDialogBuilder builder = new VKShareDialogBuilder();
        builder.setText(context.getResources().getString(R.string.str_vk_share_text));
        builder.setAttachmentImages(new VKUploadImage[]{
                new VKUploadImage(bitmap, VKImageParameters.pngImage())
        });
        builder.setAttachmentLink(context.getResources().getString(R.string.app_name), CMAppGlobals.LOC_SERVICE_LINK);
        builder.setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
            @Override
            public void onVkShareComplete(int postId) {
                // recycle bitmap if need
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK : VKShareDialogListener.onVkShareComplete postId : " + postId);
                String vkID = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), "");
                if (vkID.isEmpty()) {
                    VKHelper.logoutVKAccount();
                }
                // Re-post Social request -- social_id = 1 (vk)
                bonusManager.RepostSocial(1);
                current_social_id = 1;
            }

            @Override
            public void onVkShareCancel() {
                // recycle bitmap if need
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK : VKShareDialogListener.onVkShareCancel");
                String vkID = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), "");
                if (vkID.isEmpty()) {
                    VKHelper.logoutVKAccount();
                }
            }

            @Override
            public void onVkShareError(VKError error) {
                // recycle bitmap if need
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: VK : VKShareDialogListener.onVkShareError error : " + error.errorMessage);
                String vkID = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_VK_ID.key(), "");
                if (vkID.isEmpty()) {
                    VKHelper.logoutVKAccount();
                }
            }
        });
        builder.show(fragmentManager, "VK_SHARE_DIALOG");

    } // end method shareVKWithDialog

    @Override
    public void onBackPressed() {
        ((RewardsFragment)currentFragment).closeKeyboard();
        super.onBackPressed();

    } // end method onBackPressed

    @Override
    public void onFailure(Throwable error, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onFailure : requestType : " + requestType + " : error : " + error.getMessage());

    } // end method onFailure

    @Override
    public void onSuccess(Object response, int requestType) {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.OnSuccess : requestType : " + requestType + " : response : " + response);
        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_ENTER_COUPON:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_ENTER_COUPON")) {
                        if(response instanceof EnterCouponData) {
                            EnterCouponData enterCouponData = (EnterCouponData) response;
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_ENTER_COUPON : enterCouponData" + enterCouponData);
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_ENTER_COUPON : sum : " + enterCouponData.getSum());
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_ENTER_COUPON : msg : " + enterCouponData.getMsg());
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_ENTER_COUPON : status : " + enterCouponData.getStatus());
                            if (CMAppGlobals.DEBUG)
                                Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_ENTER_COUPON : campain :" + enterCouponData.getCampain());

                            if (enterCouponData.getStatus() == 1) {
                                if (enterCouponData.getSum() != 0) {
                                    String bonus = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BONUS.key(), "");
                                    int newBonus = Integer.parseInt(bonus) + enterCouponData.getSum();
                                    String strBonus = String.valueOf(newBonus);
                                    LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BONUS, strBonus);
                                    ((RewardsFragment) currentFragment).getTextViewHeaderTopRewardsSize().setText(strBonus);
                                }
                                ((RewardsFragment) currentFragment).closeKeyboard();
                            }

                            if (enterCouponData.getMsg() != null && !enterCouponData.getMsg().isEmpty()) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardsActivity.this, R.style.AppCompatAlertDialogStyle);
                                builder.setMessage(enterCouponData.getMsg());
                                builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                        }

                    }
                    break;
                case RequestTypes.REQUEST_GET_CLIENT_INFO:
                    if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onSuccess : REQUEST_GET_CLIENT_INFO : response : " + response);
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CLIENT_INFO")) {
                        if(response instanceof ClientInfo) {
                            ClientInfo clientInfo = (ClientInfo) response;
                            CMApplication.saveClientInfo(clientInfo);

                            ((RewardsFragment) currentFragment).notifyAdapter();
                        }
                    }
                    break;
                case RequestTypes.REQUEST_GET_BONUS_INFO:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : response : " + response);
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_BONUS_INFO")) {
                        if(response instanceof BonusInfo) {
                            BonusInfo bonusInfo = (BonusInfo) response;
                            if (bonusInfo.getCardBinding() != null) {
                                if(CMAppGlobals.DEBUG )Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getCardBinding().getValueTitle() : " + bonusInfo.getCardBinding().getValueTitle());
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getCardBinding().getCanGetBonus() : " + bonusInfo.getCardBinding().getCanGetBonus());
                            }
                            if (bonusInfo.getShareFb() != null) {
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareFb().getValueTitle() : " + bonusInfo.getShareFb().getValueTitle());
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareFb().getCanGetBonus() : " + bonusInfo.getShareFb().getCanGetBonus());
                            }
                            if (bonusInfo.getShareOk() != null) {
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareOk().getValueTitle() : " + bonusInfo.getShareOk().getValueTitle());
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareOk().getCanGetBonus() : " + bonusInfo.getShareOk().getCanGetBonus());
                            }
                            if (bonusInfo.getShareVk() != null) {
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareVk().getValueTitle() : " + bonusInfo.getShareVk().getValueTitle());
                                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_GET_BONUS_INFO : bonusInfo.getShareVk().getCanGetBonus() : " + bonusInfo.getShareVk().getCanGetBonus());
                            }

                            ((RewardsFragment) currentFragment).updateRewardsList(bonusInfo);
                            ((RewardsFragment) currentFragment).notifyAdapter();
                        }
                    }
                    break;
                case RequestTypes.REQUEST_REPOST_SOCIAL:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_REPOST_SOCIAL : response : " + response);
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_REPOST_SOCIAL")) {
                        if(response instanceof RepostSocialData) {
                            RepostSocialData repostSocialData = (RepostSocialData) response;

                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_REPOST_SOCIAL : repostSocialData.getIsCanRepeat() : " + repostSocialData.getIsCanRepeat());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_REPOST_SOCIAL : repostSocialData.getClientBonus() : " + repostSocialData.getClientBonus());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_REPOST_SOCIAL : repostSocialData.getRepostBonus() : " + repostSocialData.getRepostBonus());
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RewardsActivity.OnSuccess : REQUEST_REPOST_SOCIAL : repostSocialData.getResult() : " + repostSocialData.getResult());

                            // Refresh Bonus Data & List
                            if(repostSocialData.getIsCanRepeat() < 1) {
                                ((RewardsFragment) currentFragment).setSocialItems(current_social_id, repostSocialData.getIsCanRepeat(), repostSocialData.getClientBonus());
                            }
                        }
                    }
                    break;
            }
        }
    } // end method onSuccess

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onActivityResult" +
                " : requestCode : " + requestCode
                + " : resultCode : " + resultCode
                + " : data : " + data);

        // for FB
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CMAppGlobals.REQUEST_CARD_ADD) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onActivityResult : REQUEST_CARD_ADD");

            ClientManager clientManager = new ClientManager(this);
            clientManager.GetClientInfo();

            BonusManager bonusManager = new BonusManager(this);
            bonusManager.GetBonusesInfo();

        }

        // VK
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback )) {
            super.onActivityResult(requestCode, resultCode, data);
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onActivityResult  not VK");
        }

    } // end method onActivityResult

    @Override
    public void onSuccessFBShare(Sharer.Result result) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onSuccessFBShare : result : " + result);
        // Repost Social request -- social_id = 2 (fb)
        bonusManager.RepostSocial(2);
        current_social_id = 2;

    } // end method onSuccessFBShare

    @Override
    public void onCancelFBShare() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onCancelFBShare");

    } // end method onCancelFBShare

    @Override
    public void onErrorFBShare(FacebookException error) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RewardsActivity.onSuccessFBShare : error : " + error.getMessage());

    } // end method onErrorFBShare

    /**
     * Method for getting FB Callback Manager
     * @return - FB Callback Manager
     */
    public CallbackManager getCallbackManager() {
        return callbackManager;

    } // end method getCallbackManager
}
