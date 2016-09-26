package com.locservice.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.application.LocServicePreferences;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

/**
 * Created by Vahagn Gevorgyan
 * 23 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
@SuppressLint("SetJavaScriptEnabled")
public class TariffActivity extends BaseActivity {

    private static final String TAG = TariffActivity.class.getSimpleName();
    private ProgressBar progressBarLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariff);

        final LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        if (layoutBack != null) {
            layoutBack.setOnTouchListener(new View.OnTouchListener() {
                ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return CommonHelper.setOnTouchImage(imageViewBack, event);
                }
            });
            layoutBack.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // LOAD URL WebView
            loadWebViewUrl();
        }

        RelativeLayout layoutParent = (RelativeLayout) findViewById(R.id.layoutParent);
        if (CMApplication.hasNavigationBar(TariffActivity.this) && layoutParent != null) {
            layoutParent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
        }

    }

    /**
     * Method for loading web view url
     */
    public void loadWebViewUrl() {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: TariffActivity.loadWebViewUrl ");

        WebView webViewTariff = (WebView) findViewById(R.id.webViewTariff);
        progressBarLoad = (ProgressBar) findViewById(R.id.progressBarLoad);
        if (webViewTariff != null) {
            WebSettings settings = webViewTariff.getSettings();
            settings.setJavaScriptEnabled(true);
            webViewTariff.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            String tariffURL = generateTariffURL();
            webViewTariff.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, "TariffActivity.loadWebViewUrl : Processing webview url click ");
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, "TariffActivity.loadWebViewUrl : Finished loading URL : " + url);
                    if (progressBarLoad.getVisibility() == View.VISIBLE) {
                        progressBarLoad.setVisibility(View.GONE);
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, "TariffActivity.loadWebViewUrl : Error : " + description);
                    Toast.makeText(TariffActivity.this, description, Toast.LENGTH_SHORT).show();
                }
            });
            webViewTariff.loadUrl(tariffURL);
        }


    } // end method loadWebViewUrl

    /**
     * Method for generating tariff url
     * @return - tariff url
     */
    public String generateTariffURL() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: TariffActivity.generateTariffURL : city_id : " +
                LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), ""));
        String url = "URL";
        url += LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.CURRENT_CITY_ID.key(), "");
        url += "/";
        url += "ru";
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: TariffActivity.generateTariffURL : URL : " + url);
        return url;

    } // end method generateTariffURL



}
