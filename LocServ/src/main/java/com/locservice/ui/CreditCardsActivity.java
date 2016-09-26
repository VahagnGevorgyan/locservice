package com.locservice.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.CMApplication;
import com.locservice.R;
import com.locservice.adapters.CreditCardsRVAdapter;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.CardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.manager.CreditCardManager;
import com.locservice.application.LocServicePreferences;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.CommonHelper;
import com.locservice.utils.ErrorUtils;
import com.locservice.utils.Logger;
import com.locservice.utils.Utils;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.List;



public class CreditCardsActivity extends BaseActivity implements ICallBack, View.OnClickListener {

    private static final String TAG = CreditCardsActivity.class.getSimpleName();

    private LinearLayout layoutBack;

    private CreditCardManager creditCardManager;
    private ImageView imageViewAdd;
    private RecyclerView recyclerViewCreditCards;
    private List<CardInfo> cardInfoList = new ArrayList<>();
    private CreditCardsRVAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_cards);

        if(CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.onCreate ");

        recyclerViewCreditCards = (RecyclerView) findViewById(R.id.recyclerViewCreditCards);

        // check network status
        if (Utils.checkNetworkStatus(this)) {
            RelativeLayout layoutRegisterContent = (RelativeLayout) findViewById(R.id.layoutRegisterContent);
            String auth_token = (LocServicePreferences.getAppSettings().getString(LocServicePreferences.Settings.AUTH_TOKEN.key(), ""));
            RelativeLayout layoutHeader = (RelativeLayout) findViewById(R.id.layoutHeader);
            if (auth_token.isEmpty()) {
                if (layoutHeader != null) {
                    layoutHeader.setVisibility(View.GONE);
                }
                if (layoutRegisterContent != null) {
                    layoutRegisterContent.setVisibility(View.VISIBLE);
                    if (CMApplication.hasNavigationBar(CreditCardsActivity.this)) {
                        layoutRegisterContent.setPadding(0, 0, 0, CMApplication.dpToPx(48));
                    }
                }
                RelativeLayout layoutGoToRegister = (RelativeLayout) findViewById(R.id.layoutGoToRegister);
                if (layoutGoToRegister != null) {
                    layoutGoToRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(CreditCardsActivity.this, RegisterActivity.class), CMAppGlobals.REQUEST_REGISTER);
                        }
                    });
                }
            } else {
                if (layoutHeader != null) {
                    layoutHeader.setVisibility(View.VISIBLE);
                }
                if (layoutRegisterContent != null) {
                    layoutRegisterContent.setVisibility(View.GONE);
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerViewCreditCards.setLayoutManager(layoutManager);

                creditCardManager = new CreditCardManager(this);
                creditCardManager.GetCreditCards();
            }

            // Setting event listeners
            setEventListeners();
        }

        if(CMApplication.hasNavigationBar(CreditCardsActivity.this)) {
            if (imageViewAdd == null)
                imageViewAdd = (ImageView) findViewById(R.id.imageViewAdd);
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (50 * scale + 0.5f);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewAdd.getLayoutParams();
            params.setMargins(0,0,0,padding_in_px);
            imageViewAdd.setLayoutParams(params);
            recyclerViewCreditCards.setPadding(0,0,0,padding_in_px);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CreditCardsActivity.onActivityResult : requestCode : " + requestCode);

        if(requestCode == CMAppGlobals.REQUEST_CARD_ADD) {
            creditCardManager.GetCreditCards();
        }

        if (requestCode == CMAppGlobals.REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
    }

    /**
     * Method for setting all event listeners
     */
    private void setEventListeners() {

        layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
        layoutBack.setOnTouchListener(new View.OnTouchListener() {
            ImageView imageViewBack = (ImageView) layoutBack.findViewById(R.id.imageViewBack);

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CommonHelper.setOnTouchImage(imageViewBack, event);
            }
        });
        layoutBack.setOnClickListener(this);
        imageViewAdd = (ImageView) findViewById(R.id.imageViewAdd);
        if (imageViewAdd != null) {
            imageViewAdd.setOnClickListener(this);
        }

    } // end method setEventListeners

    @Override
    public void onClick(View v) {
        // check network status
        if (Utils.checkNetworkStatus(CreditCardsActivity.this)) {

            switch (v.getId()) {
                case R.id.layoutBack:
                    onBackPressed();
                    break;
                case R.id.imageViewAdd:
                    // OPEN ADD CARD ACTIVITY
                    startActivityForResult(new Intent(CreditCardsActivity.this, AddCreditCardsActivity.class), CMAppGlobals.REQUEST_CARD_ADD);
                    break;
            }
        }
    }

    /**
     * Method for notifying adapter
     * @param cardInfos - Credit card info
     */
    public void notifyAdapter(List<CardInfo> cardInfos) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.notifyAdapter : cardInfos : " + cardInfos);
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.notifyAdapter : size : " + cardInfos.size());

        this.cardInfoList.clear();
        this.cardInfoList.addAll(cardInfos);

        if (adapter == null) {
            adapter = new CreditCardsRVAdapter(CreditCardsActivity.this, this.cardInfoList);
            adapter.setMode(Attributes.Mode.Single);
            recyclerViewCreditCards.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }


    } // end method notifyAdapter

    /**
     * Method for notifying adapter
     * @param cardInfo - card information
     */
    public void notifyAdapter(CardInfo cardInfo) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.notifyAdapter : cardInfo : " + cardInfo);

        this.cardInfoList.clear();
        this.cardInfoList.add(cardInfo);

        if (adapter == null) {
            adapter = new CreditCardsRVAdapter(CreditCardsActivity.this, this.cardInfoList);
            adapter.setMode(Attributes.Mode.Single);
            recyclerViewCreditCards.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }


    } // end method notifyAdapter

    /**
     * Method for removing card info from list
     */
    public void removeCardInfo() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.removeFavoriteAddress");

        int position = adapter.getRemovedPosition();
        if (position != -1) {
            if(cardInfoList != null && cardInfoList.size() > 0) {
                cardInfoList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, cardInfoList.size());
            }
        }

    } // end method removeCardInfo


    @Override
    public void onFailure(Throwable error, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.onFailure : requestType : " + requestType + " : error : " + error.getMessage());
    }

    @Override
    public void onSuccess(Object response, int requestType) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.onSuccess : requestType : " + requestType + " : response : " + response);

        if (response != null) {
            switch (requestType) {
                case RequestTypes.REQUEST_GET_CARDS:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_GET_CARDS")) {
                        if (response instanceof CardsData) {

                            CardsData cardsData = (CardsData) response;
                            // DRAW CARDS
                            if (cardsData.getCards() != null) {
                                if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardsActivity.onSuccess : REQUEST_GET_CARDS : cardsData.getCards().size() : " + cardsData.getCards().size());

                                for (CardInfo item :
                                        cardsData.getCards()) {
                                    if (CMAppGlobals.DEBUG)Logger.i(TAG, ":: CreditCardsActivity.onSuccess : REQUEST_GET_CARDS : CARD_ID : " + item.getId());
                                }
                                // notify adapter
                                notifyAdapter(cardsData.getCards());
                            }
                        }
                    }
                break;
                case RequestTypes.REQUEST_CHECK_UNBIND_CARD:
                    if (!ErrorUtils.hasError(getApplicationContext(), response, "REQUEST_CHECK_UNBIND_CARD")) {
                        if (response instanceof ResultIntData) {

                            ResultIntData resultData = (ResultIntData) response;
                            if(CMAppGlobals.DEBUG)Logger.i(TAG, ":: CreditCardsActivity.onSuccess : REQUEST_CHECK_UNBIND_CARD : resultData.getResult() : " + resultData.getResult());

                            if (resultData.getResult() > 0) {
                                removeCardInfo();
                            } else {
                                Toast.makeText(CreditCardsActivity.this, R.string.alert_unbind_card, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
            }
        }
    }


}
