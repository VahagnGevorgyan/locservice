package com.locservice.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.api.entities.ClientInfo;
import com.locservice.api.manager.ClientManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.ProfileActivity;
import com.locservice.ui.SocialActivity;
import com.locservice.ui.fragments.ProfileFragment;
import com.locservice.ui.fragments.SocialNetworkFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ImageHelper {

    private static final String TAG = ImageHelper.class.getSimpleName();

    public static MemCache memCache = new MemCache();

    /**
     * LoadImagiesByLoader
     *
     * @param urlDisplay
     * @param imgViewItem2
     */
    public static void loadImagesByLoader(final Context context, String urlDisplay, final ImageView imgViewItem2) {
        ImageLoader.getInstance().displayImage(urlDisplay, imgViewItem2, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {	}

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {	}

            @Override
            public void onLoadingComplete(String arg0, final View view, final Bitmap bitmap) {

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ImageHelper.onLoadingComplete : url : " + arg0);

                memCache.addBitmapToMemoryCache(arg0, bitmap);

                if(view != null && context != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ((ImageView)view).setImageBitmap(bitmap);
                        }
                    });
                }
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }

        });

    } // end method loadImagesByLoader

    /**
     * LoadImagiesByLoader
     *
     * @param urlDisplay
     * @param imgViewItem2
     */
    public static void loadProfileAvatarByLoader(final Context context, String urlDisplay, final ImageView imgViewItem2) {
        ImageLoader.getInstance().displayImage(urlDisplay, imgViewItem2, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {	}

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {	}

            @Override
            public void onLoadingComplete(String arg0, final View view, final Bitmap bitmap) {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ImageHelper.onLoadingComplete : url : " + arg0);

                ClientManager clientManager = new ClientManager((ICallBack) context);
                ProfileFragment profileFragment = (ProfileFragment) ((ProfileActivity) context).getCurrentFragment();
                ClientInfo clientInfo = profileFragment.getCurrentClientInfo();
                String imageBase64 = CMApplication.encodeBitmapToBase64(bitmap);
                clientManager.UpdateClientInfo(clientInfo.getName(),
                        clientInfo.getEmail(),
                        LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
                        imageBase64, 0);
                clientInfo.setBase64Image(imageBase64);
                profileFragment.setCurrentClientInfo(clientInfo);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }

        });

    } // end method loadProfileAvatarByLoader

    /**
     * LoadImagiesByLoader
     *
     * @param urlDisplay
     * @param imgViewItem2
     */
    public static void loadSocialBitmapByLoader(final Context context, String urlDisplay, final ImageView imgViewItem2) {
        ImageLoader.getInstance().displayImage(urlDisplay, imgViewItem2, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {	}

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {	}

            @Override
            public void onLoadingComplete(String arg0, final View view, final Bitmap bitmap) {
                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ImageHelper.onLoadingComplete : url : " + arg0);

                ClientManager clientManager = new ClientManager((ICallBack) context);
                SocialNetworkFragment socialNetworkFragment = (SocialNetworkFragment) ((SocialActivity) context).getCurrentFragment();
                ClientInfo clientInfo = socialNetworkFragment.getCurrentClientInfo();
                String imageBase64 = CMApplication.encodeBitmapToBase64(bitmap);
                clientManager.UpdateClientInfo(clientInfo.getName(),
                        clientInfo.getEmail(),
                        LocServicePreferences.getAppSettings().getInt(LocServicePreferences.Settings.PROFILE_SMS_NOTIFICATION.key(), 0),
                        imageBase64, 0);
                clientInfo.setBase64Image(imageBase64);
                socialNetworkFragment.setCurrentClientInfo(clientInfo);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }

        });

    } // end method loadProfileAvatarByLoader

    /**
     * Load Avatar By Loader
     *
     * @param urlDisplay
     * @param imgViewItem2
     */
    public static void loadAvatarByLoader(final Context context, String urlDisplay, final ImageView imgViewItem2) {
        ImageLoader.getInstance().displayImage(urlDisplay, imgViewItem2, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {	}

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {	}

            @Override
            public void onLoadingComplete(String arg0, final View view, final Bitmap bitmap) {

                if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ImageHelper.onLoadingComplete : url : " + arg0);

//                memCache.addBitmapToMemoryCache(arg0, bitmap);

                if(view != null && context != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ((ImageView)view).setImageBitmap(bitmap);
                            LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE, CMApplication.encodeBitmapToBase64(bitmap));
                        }
                    });
                }
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub

            }

        });

    } // end method loadAvatarByLoader
}
