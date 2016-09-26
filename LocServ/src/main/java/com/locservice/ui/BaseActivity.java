package com.locservice.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.locservice.CMAppGlobals;
import com.locservice.application.LocServicePreferences;
import com.locservice.messages.WebViewDialogMessage;
import com.locservice.service.DetachableResultsReceiver;
import com.locservice.service.DetachableResultsReceiver.OnReceiveResultListener;
import com.locservice.ui.fragments.WebViewDialogFragment;
import com.locservice.utils.Logger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;


public abstract class BaseActivity extends AppCompatActivity implements OnReceiveResultListener {

    static final String TAG = BaseActivity.class.getSimpleName();

    protected final DetachableResultsReceiver serviceReceiver = new DetachableResultsReceiver(new Handler());

    protected LinkedList<OnDataRetrievedListener> dataListeners = new LinkedList<OnDataRetrievedListener>();

    protected ProgressDialog progressDialog = null;

    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * Interface used to notify activity's data fragments the data have been
     * retrieved
     */
    public interface OnDataRetrievedListener {
        public void OnDataRetrieved();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onActivityResult ");
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            addUiChangeListener(decorView);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//            addUiChangeListener(decorView);
//        }

        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onCreate ");
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onDestroy ");
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.dismissProgressDialog();
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onPause ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onResume ");
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onReceiveResult ");
    }

    /**
     * Method for hiding progress bar dialog
     */
    public void dismissProgressDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // set receiver
        this.serviceReceiver.setListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        // clear receiver
        this.serviceReceiver.clearListener();
    }

    public void addUiChangeListener(final View decorView) {
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        decorView.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebViewDialogEvent(WebViewDialogMessage event) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onMessageEvent : event : " + event.getStrUrl());

        if (event.getStrUrl() != null && !event.getStrUrl().isEmpty()) {
            String url = event.getStrUrl() + "?t=" + LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), "");
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: BaseActivity.onMessageEvent : url : " + url);
            WebViewDialogFragment.showWebVIewDialog(this, url);
        }
    }
}
