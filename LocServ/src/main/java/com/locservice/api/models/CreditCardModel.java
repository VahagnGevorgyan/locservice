package com.locservice.api.models;

import com.locservice.CMAppGlobals;
import com.locservice.api.entities.ApiHostType;
import com.locservice.api.entities.BindCardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.request.BindCardRequest;
import com.locservice.api.request.CheckCardBindRequest;
import com.locservice.api.request.UnBindCardRequest;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.CreditCardService;
import com.locservice.api.service.ServiceGenerator;
import com.locservice.utils.Logger;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CreditCardModel {

    private static final String TAG = CreditCardModel.class.getSimpleName();

    private CreditCardService creditCardService;

    public CreditCardModel() {
    }

    /**
     * Method for binding credit card
     * @param body - request body
     * @param cb - callback
     */
    public void BindCard(BindCardRequest body, Callback<BindCardInfo> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardModel.BindCard : body : " + body + " : cb : " + cb);

        this.creditCardService = ServiceGenerator.createService(CreditCardService.class, ApiHostType.API_BASE_URL, "");

        Call<BindCardInfo> bindCardInfoCall = this.creditCardService.BindCard(body);
        bindCardInfoCall.enqueue(cb);

    } //end method BindCard

    /**
     * Method for unbinding card
     * @param body - unbind card request class
     * @param cb - callback
     */
    public void UnbindCard(UnBindCardRequest body, Callback<ResultIntData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardModel.UnbindCard : body : " + body + " : cb : " + cb);

        this.creditCardService = ServiceGenerator.createService(CreditCardService.class, ApiHostType.API_BASE_URL, "");

        Call<ResultIntData> resultIntDataCall = this.creditCardService.UnbindCard(body);
        resultIntDataCall.enqueue(cb);

    } //end method UnbindCard

    /**
     * Method for checking card
     * @param body - request body
     * @param cb - callback
     */
    public void CheckCardBind(CheckCardBindRequest body, Callback<ResultIntData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardModel.CheckCardBind : body : " + body + " : cb : " + cb);

        this.creditCardService = ServiceGenerator.createService(CreditCardService.class, ApiHostType.API_BASE_URL, "");

        Call<ResultIntData> resultIntDataCall = this.creditCardService.CheckCardBind(body);
        resultIntDataCall.enqueue(cb);

    } //end method CheckCardBind

    /**
     * Method for getting credit cards
     * @param cb - callback
     */
    public void GetCreditCards(WebRequest body, Callback<CardsData> cb) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardModel.GetCreditCards : cb : " + cb);

        this.creditCardService = ServiceGenerator.createService(CreditCardService.class, ApiHostType.API_BASE_URL, "");

        Call<CardsData> resultIntDataCall = this.creditCardService.GetCreditCards(body);
        resultIntDataCall.enqueue(cb);

    } //end method GetCreditCards

    /**
     * Method for card auth start
     * @param md - md string
     * @param AUTH_URL - auth url
     * @param termUrl - term url
     * @param paReq - pa req
     */
    public void CardAuthStart(String md, String AUTH_URL, String termUrl, String paReq, Callback<String> cb) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: CreditCardModel.CardAuthStart : auth_url : " + AUTH_URL
                    + " : md : " + md + " : TermUrl : " + termUrl + " : PaReq : " + paReq);

        this.creditCardService = ServiceGenerator.createService(CreditCardService.class, ApiHostType.API_CUSTOM_URL, AUTH_URL);

        Call<String> stringCall = this.creditCardService.CardAuthStart("start.do", md, paReq, termUrl);
        stringCall.enqueue(cb);

    } //end method CardAuthStart

}
