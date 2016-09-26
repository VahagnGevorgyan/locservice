package com.locservice.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.AuthData;
import com.locservice.api.entities.ResultData;
import com.locservice.api.manager.ClientManager;
import com.locservice.api.manager.RegisterManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.PermissionUtils;
import com.locservice.utils.Typefaces;
import com.locservice.utils.Utils;

public class RegisterActivity extends BaseActivity implements OnClickListener, ICallBack {

    static final String TAG = RegisterActivity.class.getSimpleName();
    private CustomTextView textViewTitle;
    private CustomTextView textViewMessage;
    private CustomTextView textViewUnderline;
    private CustomTextView textViewFirstNumber;
    private CustomEditTextView editTextEddField;
    Typeface robotoLight;
    Typeface robotoRegular;

    private boolean isPhone = true;
    private boolean isCode = false;
    private boolean isEmail = false;
    protected RegisterManager registerManager;
    private String phone_number;
    private String mDeviceId;
    private String formatted_phone_number;
    private CustomEditTextView editTextEddFieldHint;
    private TextView textViewOk;
    private float fontScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        robotoLight = Typefaces.get(RegisterActivity.this, "fonts/RobotoLight.ttf");
        robotoRegular = Typefaces.get(RegisterActivity.this, "fonts/RobotoRegular.ttf");
        FrameLayout layoutBack = (FrameLayout) findViewById(R.id.layoutBack);
        FrameLayout layoutClear = (FrameLayout) findViewById(R.id.layoutClear);
        textViewTitle = (CustomTextView) findViewById(R.id.textViewTitle);
        textViewMessage = (CustomTextView) findViewById(R.id.textViewMessage);
        textViewUnderline = (CustomTextView) findViewById(R.id.textViewUnderline);
        textViewFirstNumber = (CustomTextView) findViewById(R.id.textViewFirstNumber);
        editTextEddField = (CustomEditTextView) findViewById(R.id.editTextEddField);
        editTextEddFieldHint = (CustomEditTextView) findViewById(R.id.editTextEddFieldHint);

        fontScale = getResources().getConfiguration().fontScale;
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onCreate : fontScale : " + fontScale);
        float textSize = editTextEddField.getTextSize() / fontScale;
        editTextEddField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32 / fontScale);
        editTextEddFieldHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32 / fontScale);
        textViewFirstNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32 / fontScale);

        if (layoutBack != null) {
            layoutBack.setOnClickListener(this);
        }
        if (layoutClear != null) {
            layoutClear.setOnClickListener(this);
        }
        textViewFirstNumber.setOnClickListener(this);

        textViewOk = (TextView) findViewById(R.id.textViewOk);
        if (textViewOk != null) {
            textViewOk.setOnClickListener(this);
        }

        editTextEddField.setSelection(editTextEddField.getText().toString().length());
        editTextEddField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPhone) {
                    String textField = editTextEddField.getText().toString();
                    if (before != 1) {
                        if (textField.length() == 4) {
                            editTextEddField.setText(String.format("%s ", textField));
                            editTextEddField.setSelection(textField.length() + 1);
                        } else if (textField.length() == 8 || textField.length() == 11) {
                            editTextEddField.setText(String.format("%s-", textField));
                            editTextEddField.setSelection(textField.length() + 1);
                        } else if (start == 4) {
                            String newName = textField.substring(0, start);
                            String newName2 = textField.substring(start);
                            editTextEddField.setText(String.format("%s %s", newName, newName2));
                            if (textField.length() < 14)
                                editTextEddField.setSelection(textField.length() + 1);
                        } else if (start == 8 || start == 11) {
                            String newName = textField.substring(0, start);
                            String newName2 = textField.substring(start);
                            editTextEddField.setText(String.format("%s-%s", newName, newName2));
                            if (textField.length() < 14)
                                editTextEddField.setSelection(textField.length() + 1);

                        }
                    }
                    setHintText(editTextEddField.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            TextView.OnEditorActionListener enterKey = new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView view1, int actionId, KeyEvent event) {
                        boolean isDonePressed = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onKey ");
                            if (isEmail) {
                                if (!isValidEmail(editTextEddField.getText().toString())) {
                                    Toast.makeText(RegisterActivity.this, R.string.str_email_validation_toast, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : EMAIL ");
                                View view = RegisterActivity.this.getCurrentFocus();
                                if (view != null) {
                                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                // update email information
                                ClientManager clientManager = new ClientManager(RegisterActivity.this);
                                clientManager.UpdateClientInfo("", editTextEddField.getText().toString(), 0, "", 1);

                                finish();
                            }
                            if (isCode) {
                                String code = editTextEddField.getText().toString();
                                if (code.length() != 4) {
                                    Toast.makeText(RegisterActivity.this, R.string.str_code_validation_toast, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : CODE : " + code + " phone_number : " + phone_number);

                                // REGISTER CONFIRM
                                if (mDeviceId != null) {
                                    registerManager.CheckPhone(phone_number, code, mDeviceId);
                                } else {
                                    getDeviceId();
                                    registerManager.CheckPhone(phone_number, code, mDeviceId);
                                }
                            }
                            if (isPhone) {
                                phone_number = "";
                                if (editTextEddField.getText().toString().length() != 14) {
                                    Toast.makeText(RegisterActivity.this, R.string.str_phone_validation_toast, Toast.LENGTH_LONG).show();
                                    return true;
                                } else {
                                    phone_number = editTextEddField.getText().toString();
                                    if (!isValidPhone()) {
                                        Toast.makeText(RegisterActivity.this, R.string.str_phone_validation_toast, Toast.LENGTH_LONG).show();
                                        return true;
                                    }
                                }
                                isPhone = false;
                                isCode = true;
                                isEmail = false;

                                if (CMAppGlobals.DEBUG)
                                    Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : PHONE : " + phone_number);

                                // REGISTER REQUEST
                                registerManager = new RegisterManager(RegisterActivity.this);

                                if (mDeviceId != null) {
                                    registerManager.AddNewClient(phone_number, 1, mDeviceId);
                                } else {
                                    getDeviceId();
                                    registerManager.AddNewClient(phone_number, 1, mDeviceId);
                                }

                                formatted_phone_number = editTextEddField.getText().toString();
                                setCodeScreen();
                            }
                            isDonePressed = true;
                        }
                    if (isDonePressed) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActivity.editTextEddField : isDonePressed");
                        return true;
                    } else {
                        return false;
                    }
                    }
                };
            editTextEddField.setOnEditorActionListener(enterKey);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    editTextEddField.setEnabled(false);
                    editTextEddFieldHint.setEnabled(false);
                }
            }
        }

    }

    /**
     * Method for checking email address
     *
     * @param target - email address text
     * @return       - is email valid
     */
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Method for setting hint text
     *
     * @param text - hint text
     */
    public void setHintText(String text) {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RegisterActivity.setHintText : text : " + text);

        String hint = "XXXX XXX-XX-XX";
        hint = hint.substring(text.length());
        hint = text + hint;
        editTextEddFieldHint.setHint(hint);

    } // end method setHintText

    /**
     * Method for checking is phone number is valid
     *
     * @return - is valid phone number
     */
    public boolean isValidPhone() {
        boolean isValid;
        phone_number = phone_number.replace("-", "");
        phone_number = phone_number.replace(" ", "");
        try {
            Long.parseLong(phone_number);
            isValid = true;
        } catch (NumberFormatException nfe) {
            isValid = false;
        }
        return isValid;

    } // end method isValidPhone


    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(RegisterActivity.this)) {

            switch (v.getId()) {
                case R.id.layoutBack:
                    if (isPhone) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActvitiy.OnBackClick : PHONE ");
                        View view = this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        finish();
                    }
                    if (isCode) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActvitiy.OnBackClick : CODE ");
                        isPhone = true;
                        isCode = false;
                        isEmail = false;
                        setPhoneScreen();
                    }
                    if (isEmail) {
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActvitiy.OnBackClick : EMAIL ");
                        isPhone = false;
                        isCode = true;
                        isEmail = false;
                        setCodeScreen();
                    }
                    break;
                case R.id.layoutClear:
                    if (isPhone) {
                        editTextEddField.setText("");
                    } else {
                        editTextEddField.setText("");
                    }
                    break;
                case R.id.textViewUnderline:
                    // Sand again
                    Toast.makeText(RegisterActivity.this, "Sand again", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.textViewOk:
                    if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onKey ");
                    if (isEmail) {
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : EMAIL ");

                        View view = RegisterActivity.this.getCurrentFocus();
                        if (view != null) {
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        // update email information
                        ClientManager clientManager = new ClientManager(RegisterActivity.this);
                        clientManager.UpdateClientInfo("", editTextEddField.getText().toString(), 0, "", 1);

                        finish();
                    }
                    if (isCode) {
                        String code = editTextEddField.getText().toString();
                        if (code.length() != 4) {
                            Toast.makeText(RegisterActivity.this, R.string.str_code_validation_toast, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : CODE : " + code + " phone_number : " + phone_number);

                        // REGISTER CONFIRM
                        if (mDeviceId != null) {
                            registerManager.CheckPhone(phone_number, code, mDeviceId);
                        } else {
                            getDeviceId();
                            registerManager.CheckPhone(phone_number, code, mDeviceId);
                        }

                    }
                    if (isPhone) {
                        phone_number = "";
                        if (editTextEddField.getText().toString().length() != 14) {
                            Toast.makeText(RegisterActivity.this, R.string.str_phone_validation_toast, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            phone_number = editTextEddField.getText().toString();
                            if (!isValidPhone()) {
                                Toast.makeText(RegisterActivity.this, R.string.str_phone_validation_toast, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        isPhone = false;
                        isCode = true;
                        isEmail = false;

                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.editTextEddField : setOnKeyListener : PHONE : " + phone_number);

                        // REGISTER REQUEST
                        registerManager = new RegisterManager(RegisterActivity.this);

                        if (mDeviceId != null) {
                            registerManager.AddNewClient(phone_number, 1, mDeviceId);
                        } else {
                            getDeviceId();
                            registerManager.AddNewClient(phone_number, 1, mDeviceId);
                        }

                        formatted_phone_number = editTextEddField.getText().toString();
                        setCodeScreen();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PermissionUtils.ensurePermission(this, android.Manifest.permission.READ_PHONE_STATE, PermissionUtils.READ_PHONE_STATE)){
            editTextEddField.setEnabled(true);
            editTextEddFieldHint.setEnabled(true);
        }
    }

    /**
     * Method for getting device id
     */
    private void getDeviceId() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RegisterActivity.getDeviceId ");

        // get device id
        if (!PermissionUtils.ensurePermission(this, android.Manifest.permission.READ_PHONE_STATE, PermissionUtils.READ_PHONE_STATE)) {
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mDeviceId = (telephonyManager.getDeviceId() == null) ? Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                : telephonyManager.getDeviceId();

    } // end method getDeviceId

    /**
     * Method for setting phone screen
     */
    public void setPhoneScreen() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RegisterActivity.setPhoneScreen ");

        textViewTitle.setText(R.string.str_without_telephone);
        textViewMessage.setText(R.string.str_telephone_message);
        textViewUnderline.setVisibility(View.GONE);
        textViewFirstNumber.setVisibility(View.VISIBLE);
        editTextEddField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
        editTextEddField.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextEddField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32 / fontScale);
        editTextEddField.setText("7");
        editTextEddField.setSelection(editTextEddField.getText().toString().length());
        editTextEddField.setHint("");
        editTextEddFieldHint.setVisibility(View.VISIBLE);
        editTextEddFieldHint.setHint("XXXX XXX-XX-XX");
        setHintText(editTextEddField.getText().toString());

        int phoneSize = getResources().getInteger(R.integer.int_register_phone_size);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(CMApplication.dpToPx(phoneSize), ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextEddField.setLayoutParams(layoutParams);

    } // end method setPhoneScreen

    /**
     * Method for setting code screen
     */
    public void setCodeScreen() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RegisterActivity.setCodeScreen ");

        textViewTitle.setText(R.string.str_now_code);
        textViewMessage.setText(R.string.str_code_message);
        textViewUnderline.setText(R.string.str_send_code_again);
        textViewUnderline.setVisibility(View.VISIBLE);
        textViewFirstNumber.setVisibility(View.GONE);
        editTextEddField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        editTextEddField.setText("");
        editTextEddField.setHint(R.string.str_hint_code);
        editTextEddField.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextEddField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32 / fontScale);
        editTextEddFieldHint.setVisibility(View.GONE);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextEddField.setLayoutParams(layoutParams);

    } // end method setCodeScreen

    /**
     * Method for setting email screen
     */
    public void setEmailScreen() {
        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: RegisterActivity.setEmailScreen ");

        textViewTitle.setText(R.string.str_email_address);
        textViewMessage.setText(R.string.str_email_message);
        textViewUnderline.setVisibility(View.GONE);
        textViewFirstNumber.setVisibility(View.GONE);
        editTextEddField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        editTextEddField.setText("");
        editTextEddField.setHint(R.string.str_email_long);
        editTextEddField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        editTextEddField.setTextSize(24f);
        editTextEddField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24 / fontScale);
        textViewOk.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextEddField.setLayoutParams(layoutParams);

    } // end method setEmailScreen

    @Override
    public void onBackPressed() {
        if (isPhone) {
            View view = this.getCurrentFocus();
            if (view != null) {
                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            super.onBackPressed();
        } else if (isCode) {
            isPhone = true;
            isCode = false;
            isEmail = false;
            setPhoneScreen();
        } else if (isEmail) {
            isPhone = false;
            isCode = true;
            isEmail = false;
            textViewOk.setVisibility(View.GONE);
            setCodeScreen();
        }

    }

    /**
     * Method for getting response for permissions request
     *
     * @param requestCode   - request code
     * @param permissions   - list of permissions
     * @param grantResults  - grant results list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PermissionUtils.READ_PHONE_STATE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    editTextEddField.setEnabled(true);
                    editTextEddFieldHint.setEnabled(true);
                    getDeviceId();
                    return;
                }

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        View view = findViewById(R.id.container_main_register);
                        if (view != null) {
                            Snackbar.make(view, getString(R.string.str_error_activate_permission), Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.settings), new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                            settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(settingsIntent);
                                        }
                                    })
                                    .show();
                        }
                        return;
                    }
                }
                break;
        }

    } // end method onRequestPermissionsResult

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.e(TAG, ":: RegisterActivity.onFailure : " + requestType);
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        switch (requestType) {
            case RequestTypes.REQUEST_ADD_NEW_CLIENT:
                if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_ADD_NEW_CLIENT")) {
                    ResultData resultData;
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_ADD_NEW_CLIENT : response : " + response);
                    if (response != null && response instanceof ResultData) {
                        resultData = (ResultData) response;
                        if (CMAppGlobals.DEBUG)
                            Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_ADD_NEW_CLIENT : result : " + resultData.getResult());
                    }
                }
                break;
            case RequestTypes.REQUEST_CHECK_PHONE:
                if (CMAppGlobals.DEBUG)
                    Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_CHECK_PHONE : response : " + response);
                if (response != null) {
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CHECK_PHONE")) {
                        if(response instanceof AuthData) {
                            AuthData authData = (AuthData) response;
                            int result = authData.getResult();
                            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_CHECK_PHONE : result : " + result);
                            if (authData.getResult() > 0) {
                                isPhone = false;
                                isCode = false;
                                isEmail = true;
                                setEmailScreen();

                                CMApplication.setAuthToken(authData.getAuth_token());
                                setResult(Activity.RESULT_OK);

                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_CHECK_PHONE : AUTH_TOKEN : " + authData.getAuth_token());
                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_CHECK_PHONE : PROFILE_FORMATTED_PHONE_NUMBER : " + formatted_phone_number);
                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onSuccess : REQUEST_CHECK_PHONE : PHONE_NUMBER : " + phone_number);

                                // STORE AUTH_TOKEN in SharedPreferences
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.AUTH_TOKEN, authData.getAuth_token());
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PROFILE_FORMATTED_PHONE_NUMBER, formatted_phone_number);
                                LocServicePreferences.getAppSettings().setSetting(LocServicePreferences.Settings.PHONE_NUMBER, phone_number);

                            } else {
                                Toast.makeText(RegisterActivity.this, "Wrong code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                break;
            case RequestTypes.REQUEST_UPDATE_CLIENT_INFO:
                if(!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_UPDATE_CLIENT_INFO")) {
                    if(response instanceof ResultData) {
                        ResultData resultData = (ResultData) response;
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: RegisterActivity.onSuccess : result : " + resultData.getResult());
                    }
                }
                break;
            default:
                break;
        }
    }

}
