package com.locservice.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 28 July 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class WebViewDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = WebViewDialogFragment.class.getSimpleName();

    private WebView webViewDialog;
    private TextView textViewOk;
    private Context mContext;
    private String dialogUrl;

    /**
     * Creates a new instance of the WebViewDialogFragment
     *
     * @param dialogUrl - url
     * @return - WebView Dialog Fragment
     */
    public static WebViewDialogFragment newInstance(String dialogUrl) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: WebViewDialogFragment : dialogUrl : " + dialogUrl);

        WebViewDialogFragment instance = new WebViewDialogFragment();
        Bundle args = new Bundle();
        args.putString(CMAppGlobals.ARGUMENT_URL, dialogUrl);
        instance.setArguments(args);
        return instance;

    } // end method newInstance

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialog_web_view, container, false);
        mContext = getActivity();

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        webViewDialog = (WebView) rootView.findViewById(R.id.webViewDialog);
        textViewOk = (TextView) rootView.findViewById(R.id.textViewOk);
        textViewOk.setOnClickListener(this);

        // Getting arguments
        Bundle args = getArguments();
        if (args != null) {
            dialogUrl = args.getString(CMAppGlobals.ARGUMENT_URL);
            if (dialogUrl != null) {

                webViewDialog.getSettings().setJavaScriptEnabled(true);
                webViewDialog.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webViewDialog.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: WebViewDialogFragment.shouldOverrideUrlLoading : url : " + url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: WebViewDialogFragment.onPageFinished : url : " + url);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: WebViewDialogFragment.onReceivedError : error : " + error);
                    }

                });

//                String strPost = "https://www.google.com/";
//                webViewDialog.postUrl(dialogUrl, EncodingUtils.getBytes(strPost, "base64"));
                Logger.i(TAG, ":: WebViewDialogFragment.onReceivedError : dialogUrl : " + dialogUrl);
                webViewDialog.loadUrl(dialogUrl);

            }
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewOk:
                dismiss();
                break;
        }
    }

    /**
     * Method for showing WebView dialog
     *
     * @param context - FragmentActivity context
     * @param url     - string url
     */
    public static void showWebVIewDialog(FragmentActivity context, String url) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: WebViewDialogFragment.showWebVIewDialog : context : " + context + " : url : " + url);

        WebViewDialogFragment webViewDialogFragment = WebViewDialogFragment.newInstance(url);
        webViewDialogFragment.show(context.getSupportFragmentManager(), CMAppGlobals.FRAGMENT_TAG_WEB_VIEW_DIALOG);

    } // end method showWebVIewDialog
}
