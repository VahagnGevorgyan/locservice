package com.locservice.api.manager;

import android.content.Context;
import android.widget.Toast;

import com.locservice.CMAppGlobals;
import com.locservice.api.RequestCallback;
import com.locservice.api.RequestTypes;
import com.locservice.api.entities.BindCardInfo;
import com.locservice.api.entities.CardsData;
import com.locservice.api.entities.ResultIntData;
import com.locservice.api.request.BindCardRequest;
import com.locservice.api.request.CheckCardBindRequest;
import com.locservice.api.request.UnBindCardRequest;
import com.locservice.api.request.WebRequest;
import com.locservice.api.service.ServiceLocator;
import com.locservice.protocol.ICallBack;
import com.locservice.utils.Logger;

/**
 * Created by Vahagn Gevorgyan
 * 26 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class CreditCardManager extends WebManager {

    private static final String TAG = CreditCardManager.class.getSimpleName();

    public CreditCardManager(ICallBack context) {
        super(context);
    }

    /**
    * Method for binding card
    * @param bindCardRequest - bind card request class
    */
    public void BindCard(BindCardRequest bindCardRequest) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardManager.BindCard : bindCardRequest : " + bindCardRequest);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : bindCard : {\n"
                            + " pan : " + bindCardRequest.getPan()
                            + ",\n cvc : " + bindCardRequest.getCvc()
                            + ",\n yyyy : " + bindCardRequest.getYyyy()
                            + ",\n mm : " + bindCardRequest.getMm()
                            + ",\n text : " + bindCardRequest.getText()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getCreditCardModel().BindCard(bindCardRequest,
                new RequestCallback<BindCardInfo>(mContext, RequestTypes.REQUEST_BIND_CARD));

    } // end method BindCard

    /**
     * Method for checking card
     * @param body - check card bind request class
     */
    public void CheckCardBind(CheckCardBindRequest body) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardManager.CheckCardBind : body : " + body);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : checkCardBind : {\n"
                            + " bind_idhash : " + body.getBind_idhash()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getCreditCardModel().CheckCardBind(body,
                new RequestCallback<ResultIntData>(mContext, RequestTypes.REQUEST_CHECK_CARD_BIND));

    } // end method CheckCardBind

    /**
     * Method for unbinding card
     * @param body - unbind card request class
     */
    public void UnbindCard(UnBindCardRequest body) {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardManager.UnbindCard : body : " + body);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : unbindcard : {\n"
                            + " id : " + body.getId()
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getCreditCardModel().UnbindCard(body,
                new RequestCallback<ResultIntData>(mContext, RequestTypes.REQUEST_CHECK_UNBIND_CARD));

    } // end method UnbindCard


    /**
     * Method for getting card list
     */
    public void GetCreditCards() {
        if (CMAppGlobals.DEBUG) Logger.i(TAG, ":: CreditCardManager.GetCreditCards : ");

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : getcards : {\n"
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getCreditCardModel().GetCreditCards(new WebRequest("getcards"),
                new RequestCallback<CardsData>(mContext, RequestTypes.REQUEST_GET_CARDS));

    } // end method GetCreditCards

    /**
     * Method for card auth start
     * @param md - md string
     * @param AUTH_URL - auth url
     * @param termUrl - term url
     * @param paReq - pa req
     */
    public void CardAuthStart(String md, String AUTH_URL, String termUrl, String paReq) {
        if (CMAppGlobals.DEBUG)
            Logger.i(TAG, ":: CreditCardModel.CardAuthStart : auth_url : " + AUTH_URL
                    + " : md : " + md + " : TermUrl : " + termUrl + " : PaReq : " + paReq);

        if(CMAppGlobals.API_TOAST)
            Toast.makeText((Context) mContext, "Request : CardAuthStart : {\n"
                            + " md : " + md
                            + ",\n AUTH_URL : " + AUTH_URL
                            + ",\n termUrl : " + termUrl
                            + ",\n paReq : " + paReq
                            + "\n}"
                    , Toast.LENGTH_SHORT).show();

        ServiceLocator.getCreditCardModel().CardAuthStart(md, AUTH_URL, termUrl, paReq,
                new RequestCallback<String>(mContext, RequestTypes.REQUEST_CARD_AUTH_START));

    } // end method CardAuthStart

}
