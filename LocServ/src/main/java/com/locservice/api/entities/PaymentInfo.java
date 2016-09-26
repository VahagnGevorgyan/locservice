package com.locservice.api.entities;

/**
 * Created by Vahagn Gevorgyan
 * 13 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class PaymentInfo {

    private String payment_name;
    private PaymentTypes paymentType;
    private String corporationId;
    private String corporationStatus;
    private CardInfo cardInfo;
    private boolean isNoCard;
    private boolean isPromoCode;

    public PaymentInfo(String payment_name, PaymentTypes paymentType) {
        this.payment_name = payment_name;
        this.paymentType = paymentType;
    }

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public PaymentTypes getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypes paymentType) {
        this.paymentType = paymentType;
    }

    public String getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(String corporationId) {
        this.corporationId = corporationId;
    }

    public String getCorporationStatus() {
        return corporationStatus;
    }

    public void setCorporationStatus(String corporationStatus) {
        this.corporationStatus = corporationStatus;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public boolean isNoCard() {
        return isNoCard;
    }

    public void setIsNoCard(boolean isNoCard) {
        this.isNoCard = isNoCard;
    }

    public boolean isPromoCode() {
        return isPromoCode;
    }

    public void setPromoCode(boolean promoCode) {
        isPromoCode = promoCode;
    }


}
