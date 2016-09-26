package com.locservice.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.BuildConfig;
import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 23 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Finding views
        findViews();

        // Setting event listeners
        setEventListeners();

    }

    /**
     * Method for finding views & setting texts
     */
    public void findViews() {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AboutActivity.findViews ");

        CustomTextView textViewLink = (CustomTextView) findViewById(R.id.textViewLink);
        if (textViewLink != null) {
            textViewLink.setText(Html.fromHtml("<a href=\"URL\">URL</a>"));
            textViewLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        CustomTextView textViewReview = (CustomTextView) findViewById(R.id.textViewReview);
        if (textViewReview != null) {
            textViewReview.setText(getResources().getString(R.string.str_set_review));
            textViewReview.setOnClickListener(this);
        }

        CustomTextView textViewVersionText = (CustomTextView) findViewById(R.id.textViewVersionText);
        String versionStr = getResources().getString(R.string.str_version) + " " + BuildConfig.VERSION_NAME;
        if (textViewVersionText != null) {
            textViewVersionText.setText(versionStr);
        }

        if(CMApplication.hasNavigationBar(AboutActivity.this)) {
            LinearLayout container_about = (LinearLayout)findViewById(R.id.container_about);
            if (container_about != null) {
                container_about.setPadding(0, 0, 0, CMApplication.dpToPx(48));
            }
        }

        TextView textViewLebedevText = (TextView) findViewById(R.id.textViewLebedevText);
        if (textViewLebedevText != null) {
            textViewLebedevText.setMovementMethod(LinkMovementMethod.getInstance());
        }

    } // end method findViews


    /**
     * Method for setting all event listeners
     */
    private void setEventListeners() {

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
            layoutBack.setOnClickListener(this);
        }

    } // end method setEventListeners

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutBack:
                onBackPressed();
                break;
            case R.id.textViewReview:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + CMAppGlobals.CM_EMAIL));
                try {
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.str_send_email)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
