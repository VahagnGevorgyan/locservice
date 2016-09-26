package com.locservice.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.api.request.CheckCardBindRequest;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CardWebUIActivity extends BaseActivity implements ICallBack {

    private static final String TAG = CardWebUIActivity.class.getSimpleName();

    private LinearLayout layoutBack;
    private WebView webViewCardWeb;
    private ProgressBar progressBarLoad;

    private String auth_url;
    private String term_url;
    private String md;
    private String pareq;
    private String id_hash;
    private Timer mTimer;
    private CreditCardManager creditCardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_web_ui);

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
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

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            creditCardManager = new CreditCardManager(this);

            webViewCardWeb = (WebView) findViewById(R.id.webViewCardWeb);
            progressBarLoad = (ProgressBar) findViewById(R.id.progressBarLoad);

            if(getIntent().getExtras()!=null
                    && getIntent().getExtras().containsKey(CMAppGlobals.EXTRA_CARD_AUTH_URL)
                    && getIntent().getExtras().containsKey(CMAppGlobals.EXTRA_CARD_MD)
                    && getIntent().getExtras().containsKey(CMAppGlobals.EXTRA_CARD_PAREQ)
                    && getIntent().getExtras().containsKey(CMAppGlobals.EXTRA_CARD_TERM_URL)
                    && getIntent().getExtras().containsKey(CMAppGlobals.EXTRA_CARD_ID_HASH)) {
                // get from intent extras
                auth_url = (String) getIntent().getExtras().get(CMAppGlobals.EXTRA_CARD_AUTH_URL);
                md = (String) getIntent().getExtras().get(CMAppGlobals.EXTRA_CARD_MD);
                term_url = (String) getIntent().getExtras().get(CMAppGlobals.EXTRA_CARD_TERM_URL);
                pareq = (String) getIntent().getExtras().get(CMAppGlobals.EXTRA_CARD_PAREQ);
                id_hash = (String) getIntent().getExtras().get(CMAppGlobals.EXTRA_CARD_ID_HASH);

                // load web view
                loadWebView();

                // start timer
                startCheckCardTimer();
            }
        }

    }

    /**
     * Method for starting check card timer
     */
    private void startCheckCardTimer() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CardWebUIActivity.startCheckCardTimer ");

        mTimer = new Timer();
        final Handler uiHandler = new Handler();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CardWebUIActivity.startCheckCardTimer : uiHandler.post");
                        CheckCardBindRequest checkCardBindRequest = new CheckCardBindRequest();
                        checkCardBindRequest.setBind_idhash(id_hash);
                        creditCardManager.CheckCardBind(checkCardBindRequest);
                    }
                });
            }
        }, 0L, 2000);

    } // end method startCheckCardTimer

    /**
     * Method for loading web view post
     */
    public void loadWebView() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CardWebUIActivity.loadWebViewUrl : auth_url : " + auth_url +
                            " : term_url : " + term_url + " : md : " + md + " : pareq : " + pareq + " : id_hash : " + id_hash);

        try {
            String mdEnc = URLEncoder.encode(md, "utf-8");
            String md = "MD=" + mdEnc;
            String PaReqEnc = URLEncoder.encode(pareq, "utf-8");
            String PaReq = "&PaReq=" + PaReqEnc;
            String TermUrlEnc = URLEncoder.encode(term_url, "utf-8");
            String TermUrl = "&TermUrl=" + TermUrlEnc;
            String urlStr = md + PaReq + TermUrl;

            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CardWebUIActivity.loadWebView : urlStr : " + urlStr);

            webViewCardWeb.getSettings().setJavaScriptEnabled(true);
            webViewCardWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webViewCardWeb.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, "CardWebUIActivity.loadWebView : Finished loading URL : " + url);
                    if (progressBarLoad.getVisibility() == View.VISIBLE) {
                        progressBarLoad.setVisibility(View.GONE);
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, "CardWebUIActivity.loadWebView : Error : " + description);
                    Toast.makeText(CardWebUIActivity.this, description, Toast.LENGTH_SHORT).show();
                }
            });
            webViewCardWeb.postUrl(auth_url, EncodingUtils.getBytes(urlStr, "base64"));
        } catch (UnsupportedEncodingException | NullPointerException e) {
            e.printStackTrace();
        }

    } // end method loadWebView


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (requestType == RequestTypes.REQUEST_CHECK_CARD_BIND) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CardWebUIActivity.onSuccess : requestType : " + requestType + " : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_CHECK_CARD_BIND:
                    if (response instanceof ResultIntData) {

                        ResultIntData result = (ResultIntData) response;
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CardWebUIActivity.onSuccess : REQUEST_CHECK_CARD_BIND : result : " + result.getResult());

                        if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CHECK_CARD_BIND")) {
                            if(result.getResult() > 0) {
                                finish();
                            }
                        } else {
                            mTimer.cancel();
                            mTimer.purge();
                        }
                    }
                    break;
            }
        }
    }
}

