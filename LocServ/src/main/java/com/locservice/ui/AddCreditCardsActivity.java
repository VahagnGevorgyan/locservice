package com.locservice.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.locservice.CMAppGlobals;
import com.locservice.R;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.BindCardInfo;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.api.request.BindCardRequest;
import com.locservice.protocol.ICallBack;
import com.locservice.ui.controls.CustomEditTextView;
import com.locservice.ui.controls.CustomTextView;
import com.locservice.ui.utils.CardBrand;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.Logger;
import com.locservice.utils.StringHelper;
import com.locservice.utils.Utils;

import java.util.Calendar;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class AddCreditCardsActivity extends BaseActivity implements View.OnClickListener, ICallBack, View.OnFocusChangeListener {

    private static final String TAG = AddCreditCardsActivity.class.getSimpleName();
    private static final int SCAN_REQUEST_CODE = 1;

    private static final int CREDIT_CARD_NUMBER_SPACE_POSITION_FIRST = 3;
    private static final int CREDIT_CARD_NUMBER_SPACE_POSITION_SECOND = 9;
    private static final int CREDIT_CARD_NUMBER_SPACE_POSITION_THIRD = 14;
    private static final int CREDIT_CARD_NUMBER_SPACE_POSITION_FOURTH = 19;

    private static final int CREDIT_CARD_NUMBER_SYMBOLS = 23;
    private static final int CREDIT_CARD_DATE_SYMBOLS = 5;

    private static final int CREDIT_CARD_DATE_SLASH_POSITION = 1;

    private CustomEditTextView editTextCardNumber;
    private CustomEditTextView editTextDate;
    private CustomEditTextView editTextCVV;
    private CustomTextView textViewRecognizeByPhoto;
    private ImageView imageViewMasterCard;
    private ImageView imageViewVisa;
    private CustomTextView textViewCVVInfo;
    private LinearLayout layoutCreditCard;
    private CreditCardManager creditCardManager;
    private EditText editTextOwnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_card);

        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onCreate ");

        // check network status
        if (Utils.checkNetworkStatus(this)) {

            creditCardManager = new CreditCardManager(this);

            // Setting event listeners
            setEventListeners();
        }

    }

    /**
     * Method for setting all event listeners
     */
    private void setEventListeners() {
        layoutCreditCard = (LinearLayout) findViewById(R.id.layoutCreditCard);

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

        editTextCardNumber = (CustomEditTextView) findViewById(R.id.editTextCardNumber);
        if (editTextCardNumber != null) {
            editTextCardNumber.setOnFocusChangeListener(this);
            editTextCardNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextCardNumber : onTextChanged :  start : " + start
                                + " : before : " + before + " : count : " + count + " : s : " + s);
                    String cardNumber = editTextCardNumber.getText().toString();
                    if (before != 1) {
                        if (start == CREDIT_CARD_NUMBER_SPACE_POSITION_FIRST
                                || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_SECOND
                                || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_THIRD
                                || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_FOURTH) {
                            cardNumber = cardNumber + " ";
                            editTextCardNumber.setText(cardNumber);
                            editTextCardNumber.setSelection(cardNumber.length());
                        }
                    } else if (start == CREDIT_CARD_NUMBER_SPACE_POSITION_FIRST
                            || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_SECOND
                            || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_THIRD
                            || cardNumber.length() == CREDIT_CARD_NUMBER_SPACE_POSITION_FOURTH) {
                        cardNumber = cardNumber.substring(0, start - 1);
                        editTextCardNumber.setText(cardNumber);
                        editTextCardNumber.setSelection(cardNumber.length());
                    }

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextCardNumber : cardNumber.length : " + cardNumber.length());

                    cardNumber = cardNumber.replaceAll(" ", "");
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextCardNumber : cardNumber : " + cardNumber);
                    setCreditCardBackgroundByType(CardBrand.from(cardNumber));


                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextCardNumber : LENGTH : " + editTextCardNumber.getText().toString().length() );
                    if(editTextCardNumber.getText().toString().length() == CREDIT_CARD_NUMBER_SYMBOLS)
                    {
                        editTextOwnerName.requestFocus();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        ImageView imageViewClear = (ImageView) findViewById(R.id.imageViewClear);
        if (imageViewClear != null) {
            imageViewClear.setOnClickListener(this);
        }

        editTextOwnerName = (EditText)findViewById(R.id.editTextOwnerName);
        editTextDate = (CustomEditTextView) findViewById(R.id.editTextDate);
        if (editTextDate != null) {
            editTextDate.setOnFocusChangeListener(this);
            editTextDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextDate : onTextChanged :  start : " + start
                                + " : before : " + before + " : count : " + count + " : s : " + s);
                    String date = editTextDate.getText().toString();
                    if (before != 1) {
                        if (start == CREDIT_CARD_DATE_SLASH_POSITION && s.length() < 4) {
                            date = StringHelper.combineStrings(date, "/");
                            editTextDate.setText(date);
                            editTextDate.setSelection(date.length());
                        }
                    } else if (start == CREDIT_CARD_DATE_SLASH_POSITION + 1) {
                        date = date.substring(0, start - 1);
                        editTextDate.setText(date);
                        editTextDate.setSelection(date.length());
                    }

                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextDate : LENGTH : " + editTextDate.getText().toString().length() );
                    if(editTextDate.getText().toString().length() == CREDIT_CARD_DATE_SYMBOLS)
                    {
                        editTextCVV.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        editTextCVV = (CustomEditTextView) findViewById(R.id.editTextCVV);
        if (editTextCVV != null) {
            editTextCVV.setOnFocusChangeListener(this);
            editTextCVV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (CMAppGlobals.DEBUG)
                        Logger.i(TAG, ":: AddCreditCardsActivity : editTextCVV : onEditorAction : actionId : " + actionId);
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        closeKeyboard();
                        return true;
                    }
                    return false;
                }
            });
        }

        ImageView imageViewAccept = (ImageView) findViewById(R.id.imageViewAccept);
        if (imageViewAccept != null) {
            imageViewAccept.setOnClickListener(this);
        }

        textViewRecognizeByPhoto = (CustomTextView) findViewById(R.id.textViewRecognizeByPhoto);
        if (textViewRecognizeByPhoto != null) {
            textViewRecognizeByPhoto.setOnClickListener(this);
        }

        textViewCVVInfo = (CustomTextView) findViewById(R.id.textViewCVVInfo);

        imageViewMasterCard = (ImageView) findViewById(R.id.imageViewMasterCard);
        imageViewVisa = (ImageView) findViewById(R.id.imageViewVisa);

    } // end method setEventListeners

    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(AddCreditCardsActivity.this)) {

            switch (v.getId()) {
                case R.id.layoutBack:
                    closeKeyboard();
                    onBackPressed();
                    break;
                case R.id.imageViewClear:
                    clearTexts();
                    break;
                case R.id.imageViewAccept:
                    if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onClick : Accept ");
                    // Checking validation
                    if (isValid()) {
                        // Bind Card
                        BindCardRequest bindCardRequest = new BindCardRequest();
                        // number
                        String card_number = editTextCardNumber.getText().toString();
                        card_number = card_number.replace(" ", "");
                        bindCardRequest.setPan(card_number);
                        // cvv
                        String cvv = editTextCVV.getText().toString();
                        bindCardRequest.setCvc(cvv);
                        // month/year
                        String date = editTextDate.getText().toString();
                        String dateArray[] = date.split("/");
                        bindCardRequest.setMm(dateArray[0]);
                        bindCardRequest.setYyyy("20" + dateArray[1]);
                        // name
                        String name = editTextOwnerName.getText().toString();
                        bindCardRequest.setText(name);
                        // bind credit card
                        creditCardManager.BindCard(bindCardRequest);
                    }
                    break;
                case R.id.textViewRecognizeByPhoto:
                    onScanPress(v);
                    break;
            }
        }

    } // end method onClick

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra("debug_autoAcceptResult", true); // default: false

        // matches the theme of your application
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false

        // hides the manual entry button
        // if set, developers should provide their own manual entry mechanism in the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }

    /**
     * Method for clearing card's texts
     */
    private void clearTexts() {
        editTextCardNumber.setText("");
        editTextDate.setText("");
        editTextCVV.setText("");
        editTextOwnerName.setText("");
        textViewRecognizeByPhoto.setVisibility(View.VISIBLE);
        textViewCVVInfo.setVisibility(View.GONE);

    } // end method clearTexts

    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onFailure : requestType : " + requestType + " : error : " + error.getMessage());
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : requestType : " + requestType + " : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_BIND_CARD:
                    if (response instanceof  BindCardInfo) {

                        BindCardInfo bindCardInfo = (BindCardInfo) response;

                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_BIND_CARD : bindCardInfo.getLink() : " + bindCardInfo.getLink());
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_BIND_CARD : bindCardInfo.getTermUrl() : " + bindCardInfo.getTermUrl());
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_BIND_CARD : bindCardInfo.getMD() : " + bindCardInfo.getMD());
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_BIND_CARD : bindCardInfo.getPaReq() : " + bindCardInfo.getPaReq());
                        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_BIND_CARD : bindCardInfo.getBind_idhash() : " + bindCardInfo.getBind_idhash());

                        // REDIRECT TO CARD WEB UI
                        Intent webIntent = new Intent(AddCreditCardsActivity.this, CardWebUIActivity.class);
                        webIntent.putExtra(CMAppGlobals.EXTRA_CARD_AUTH_URL, bindCardInfo.getLink());
                        webIntent.putExtra(CMAppGlobals.EXTRA_CARD_MD, bindCardInfo.getMD());
                        webIntent.putExtra(CMAppGlobals.EXTRA_CARD_PAREQ, bindCardInfo.getPaReq());
                        webIntent.putExtra(CMAppGlobals.EXTRA_CARD_TERM_URL, bindCardInfo.getTermUrl());
                        webIntent.putExtra(CMAppGlobals.EXTRA_CARD_ID_HASH, bindCardInfo.getBind_idhash());
                        startActivityForResult(webIntent, CMAppGlobals.REQUEST_CARD_WEB);
                    }

                    break;
                case RequestTypes.REQUEST_CARD_AUTH_START:
                    if (response instanceof String) {

                        String html = (String) response;
                        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onSuccess : REQUEST_CARD_AUTH_START : html : " + html);

                        Intent intent = new Intent(AddCreditCardsActivity.this, CardWebUIActivity.class);
                        intent.putExtra(CMAppGlobals.EXTRA_CARD_HTML, html);
                        startActivityForResult(intent, CMAppGlobals.REQUEST_CARD_WEB);
                    }

                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : requestCode : " + requestCode);

        if(requestCode == CMAppGlobals.REQUEST_CARD_WEB) {
            finish();
        }

        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : cardNumber : " + scanResult.cardNumber);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : cardholderName : " + scanResult.cardholderName);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : cardholderName : " + scanResult.cvv);
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : expiryMonth : " + scanResult.expiryMonth + " : expiryYear : " + scanResult.expiryYear);

            // SET CARD NUMBER
            String card_text = StringHelper.combineStrings(scanResult.cardNumber.substring(0, 4), " ",
                    scanResult.cardNumber.substring(4, 8), " ",
                    scanResult.cardNumber.substring(8, 12), " ",
                    scanResult.cardNumber.substring(12, scanResult.cardNumber.length()));
            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: AddCreditCardsActivity.onActivityResult : card_text : " + card_text);
            editTextCardNumber.setText(card_text);

            if (scanResult.expiryMonth != 0 && scanResult.expiryYear != 0) {
                String dateStr = StringHelper.combineStrings(String.valueOf(scanResult.expiryMonth), "/", String.valueOf(scanResult.expiryYear));
                // SET CARD MONTH/YEAR
                editTextDate.setText(dateStr);
            }
            // SET CARD CVV
            editTextCVV.setText(scanResult.cvv);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.editTextCardNumber:
            case R.id.editTextDate:
                if (hasFocus) {
                    textViewRecognizeByPhoto.setVisibility(View.VISIBLE);
                    textViewCVVInfo.setVisibility(View.GONE);
                }
                break;
            case R.id.editTextCVV:
                if (hasFocus) {
                    textViewRecognizeByPhoto.setVisibility(View.GONE);
                    textViewCVVInfo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    /**
     * Method for setting Credit Card Background By Type
     * @param cardBrand - credit card brand
     */
    public void setCreditCardBackgroundByType(CardBrand cardBrand) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.setCreditCardBackgroundByType : cardType : " + cardBrand);

        switch (cardBrand) {
            case Visa:
                imageViewMasterCard.setVisibility(View.INVISIBLE);
                imageViewVisa.setVisibility(View.VISIBLE);
                break;
            case MasterCard:
                imageViewMasterCard.setVisibility(View.VISIBLE);
                imageViewVisa.setVisibility(View.INVISIBLE);
                break;
            case AmericanExpress:
                break;
            case DinersClub:
                break;
            case Discover:
                break;
            case JCB:
                break;
            case Unknown:
                layoutCreditCard.setBackgroundDrawable(ContextCompat.getDrawable(AddCreditCardsActivity.this, R.drawable.bg_credit_card));
                imageViewMasterCard.setVisibility(View.INVISIBLE);
                imageViewVisa.setVisibility(View.INVISIBLE);
                break;
            default:
                layoutCreditCard.setBackgroundDrawable(ContextCompat.getDrawable(AddCreditCardsActivity.this, R.drawable.bg_credit_card));
                imageViewMasterCard.setVisibility(View.INVISIBLE);
                imageViewVisa.setVisibility(View.INVISIBLE);
                break;
        }

    } // end method setCreditCardBackgroundByType

    /**
     * Method for closing keyboard
     */
    private void closeKeyboard() {
        View view = AddCreditCardsActivity.this.getCurrentFocus();
        if (view != null) {
            if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: view != null");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    } // end method closeKeyboard

    /**
     * Method for checking credit card validation
     * @return - true - valid, false - not valid
     */
    public boolean isValid() {
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.isValid");
        boolean isValid = true;
        // Card number validation
        String curdNumber = editTextCardNumber.getText().toString();
        curdNumber = curdNumber.replaceAll(" ", "");
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.isValid : curdNumber : " + curdNumber);
        if (curdNumber.length() != 12
                && curdNumber.length() != 13
                && curdNumber.length() != 16
                && curdNumber.length() != 19) {
            isValid = false;
            editTextCardNumber.setError(getResources().getString(R.string.alert_card_number));
        }

        // Date validation
        String dateText = editTextDate.getText().toString();
        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.isValid : dateText : " + dateText);
        if (dateText.length() != 5) {
            isValid = false;
            editTextDate.setError(getResources().getString(R.string.alert_credit_card_date));
        } else  {
            String strMonth = dateText.substring(0, 2);
            int month = Integer.valueOf(strMonth);
            if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.isValid : strMonth : " + strMonth + " : month : " + month);
            if (month > 12) {
                isValid = false;
                editTextDate.setError(getResources().getString(R.string.alert_credit_card_date));
            } else {
                String strYear = dateText.substring(3);
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: AddCreditCardsActivity.isValid : strYear : " + strYear + " : year : " + year);
                if (Integer.valueOf(strYear) < (year % 1000)) {
                    isValid = false;
                    editTextDate.setError(getResources().getString(R.string.alert_credit_card_date));
                }
            }
        }

        // CVV validation
        if (editTextCVV.getText().length() < 3) {
            isValid = false;
            editTextCVV.setError(getResources().getString(R.string.alert_credit_card_cvv));
        }

        // Name validation
        if (editTextOwnerName.getText().toString().isEmpty()) {
            isValid = false;
            editTextOwnerName.setError(getResources().getString(R.string.alert_credit_card_name));
        }

        return isValid;

    } // end method isValidated

}

