package com.locservice.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.ResultData;
import com.locservice.api.manager.ClientManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.listeners.ActivityListener;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.ProfileType;
import com.locservice.utils.Utils;

/**
 * Created by Vahagn Gevorgyan
 * 25 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ProfileItemsActivity extends Activity implements ICallBack,
        View.OnClickListener,
        View.OnTouchListener,
        ActivityListener {

    private static final String TAG = ProfileItemsActivity.class.getSimpleName();
    private ImageView imageViewAccept;
    private ImageView imageViewBack;
    private CustomTextView textViewTopTitle;
    private CustomEditTextView editTextField;
    private int profileItemType = -1;
    private ClientManager clientManager;

    private CMApplication mCMApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_items);

        mCMApplication = (CMApplication)this.getApplicationContext();

        textViewTopTitle = (CustomTextView) findViewById(R.id.textViewTopTitle);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            // Setting event listeners
            setEventListeners();
            // Setting profile item content
            setProfileItemContentByType(getProfileType());
        }
    }

    /**
     * Method for setting event listeners
     */
    private void setEventListeners() {
        LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        layoutBack.setOnTouchListener(this);
        layoutBack.setOnClickListener(this);

        imageViewAccept = (ImageView) findViewById(R.id.imageViewAccept);
        imageViewAccept.setOnTouchListener(this);
        imageViewAccept.setOnClickListener(this);

        editTextField = (CustomEditTextView) findViewById(R.id.editTextField);
        editTextField.setOnEditorActionListener(enterKey);

    } // end method setEventListeners

    TextView.OnEditorActionListener enterKey = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            updateProfileByType(profileItemType);
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.layoutBack:
                return CommonHelper.setOnTouchImage(imageViewBack, event);
            case R.id.imageViewAccept:
                return CommonHelper.setOnTouchImage(imageViewAccept, event);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(ProfileItemsActivity.this)) {

            switch (v.getId()) {
                case R.id.layoutBack:
                    closeKeyboard();
                    onBackPressed();
                    break;
                case R.id.imageViewAccept:
                    updateProfileByType(profileItemType);
                    break;
            }
        }

    }

    /**
     * Method for getting profile type
     * @return
     */
    public int getProfileType () {
        if (getIntent().getIntExtra(CMAppGlobals.EXTRA_CHANGE_PROFILE_ITEM, -1)  != -1)
            return getIntent().getIntExtra(CMAppGlobals.EXTRA_CHANGE_PROFILE_ITEM, -1);

        return -1;

    } // end method

    /**
     * Method for setting profile item content
     * @param profileItem
     */
    public void setProfileItemContentByType(int profileItem) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileItemsActivity.setProfileItemContentByType : profileItem : " + profileItem);
        profileItemType = profileItem;

        switch (profileItem) {
            case ProfileType.PROFILE_NAME:
                setNameContent();
                break;
            case ProfileType.PROFILE_EMAIL:
                setEmailContent();
                break;
        }

    } // end method setProfileItemContentByType

    /**
     * Method for setting name content
     */
    private void setNameContent() {
        textViewTopTitle.setText(R.string.str_name);
        editTextField.setHint(R.string.str_new_name);
        String name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
        editTextField.setText(name);
        editTextField.setSelection(name.length());

    } // end method setNameContent

    /**
     * Method for setting email content
     */
    private void setEmailContent() {
        textViewTopTitle.setText(R.string.str_email);
        editTextField.setHint(R.string.str_new_email);
        String email = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "");
        editTextField.setText(email);
        editTextField.setSelection(email.length());

    } // end method setEmailContent

    /**
     * Method for closing keyboard
     */
    private void closeKeyboard() {
        View view = ProfileItemsActivity.this.getCurrentFocus();
        if (view != null) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    } // end method closeKeyboard

    /**
     * Method for updating profile by type
     * @param profileItemType
     */
    public void updateProfileByType(int profileItemType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileItemsActivity.setProfileItemContentByType : profileItemType : " + profileItemType);

        clientManager = new ClientManager(this);
        String name = "";
        String email = "";
        String photo = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_BASE_64_IMAGE.key(), "");
        switch (profileItemType) {
            case ProfileType.PROFILE_NAME:
                String currentName = editTextField.getText().toString();
                if (inNotEmptyString(currentName)) {
                    name = currentName;
                    email = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_EMAIL.key(), "");
                } else {
                    Toast.makeText(ProfileItemsActivity.this, R.string.alert_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                clientManager.UpdateClientInfo(name, email, 0, photo, 0);
                break;
            case ProfileType.PROFILE_EMAIL:
                String currentEmail = editTextField.getText().toString();
                if (inNotEmptyString(currentEmail)) {
                    email = currentEmail;
                    name = LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.PROFILE_NAME.key(), "");
                } else {
                    Toast.makeText(ProfileItemsActivity.this, R.string.alert_email_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                clientManager.UpdateClientInfo(name, email, 0, photo, 0);
                break;
        }

    } // end method updateProfileByType

    /**
     * Method for checking is string not empty
     * @param str - string for checking
     * @return - true - string is empty
     */
    public boolean inNotEmptyString(String str) {
        str = str.trim();
        return !str.isEmpty();

    }

    @Override
    public void onFailure(Throwable error, int requestType) {

    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileItemsActivity.onSuccess : response : " + response + " : requestType : " + requestType);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
                    if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_UPDATE_CLIENT_INFO")) {
                        if(response instanceof ResultData) {
                            ResultData resultData = (ResultData) response;
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileItemsActivity.onSuccess : result : " + resultData.getResult());

                            if (resultData.getResult() != null && resultData.getResult().equals("1")) {
                                switch (profileItemType) {
                                    case ProfileType.PROFILE_NAME:
                                        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_NAME, editTextField.getText().toString());
                                        break;
                                    case ProfileType.PROFILE_EMAIL:
                                        LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_EMAIL, editTextField.getText().toString());
                                        break;
                                }
                                closeKeyboard();
                                try {
                                    if(getApplicationContext() != null && ((CMApplication)getApplicationContext()).getCurrentActivity() instanceof ProfileItemsActivity) {
                                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: ProfileItemsActivity.onSuccess : onBackPressed");
                                        finish();
                                    }
                                } catch (Exception e) {
                                    if (CMAppGlobals.DEBUG)
                                        Logger.e(TAG, ":: ProfileItemsActivity.onSuccess : REQUEST_UPDATE_CLIENT_INFO : Exception : " + e.getMessage());
                                }
                            }
                        }
                    }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileItemsActivity.onResume ");
        super.onResume();
        // set current activity
        setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileItemsActivity.onPause ");
        clearCurrentActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileItemsActivity.onDestroy ");
        clearCurrentActivity(this);
        super.onDestroy();
    }

    @Override
    public void setCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileItemsActivity.setCurrentActivity : context : " + context);

        // set current activity
        mCMApplication.setCurrentActivity(context);
    }

    @Override
    public void clearCurrentActivity(Activity context) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: ProfileItemsActivity.clearCurrentActivity : context : " + context);

        Activity currActivity = mCMApplication.getCurrentActivity();
        if (this.equals(currActivity))
            mCMApplication.setCurrentActivity(null);
    }
}
