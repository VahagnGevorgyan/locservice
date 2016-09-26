package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 29 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class BonusInfo {
    @SerializedName("friend_first_trip")
    @Expose
    private BonusInfoItem friendFirstTrip;
    @SerializedName("card_binding")
    @Expose
    private BonusInfoItem cardBinding;
    @SerializedName("order_from_app")
    @Expose
    private BonusInfoItem orderFromApp;
    @SerializedName("share_vk")
    @Expose
    private BonusInfoItem shareVk;
    @SerializedName("share_fb")
    @Expose
    private BonusInfoItem shareFb;
    @SerializedName("share_ok")
    @Expose
    private BonusInfoItem shareOk;

    /**
     *
     * @return
     * The friendFirstTrip
     */
    public BonusInfoItem getFriendFirstTrip() {
        return friendFirstTrip;
    }

    /**
     *
     * @param friendFirstTrip
     * The friend_first_trip
     */
    public void setFriendFirstTrip(BonusInfoItem friendFirstTrip) {
        this.friendFirstTrip = friendFirstTrip;
    }

    /**
     *
     * @return
     * The cardBinding
     */
    public BonusInfoItem getCardBinding() {
        return cardBinding;
    }

    /**
     *
     * @param cardBinding
     * The card_binding
     */
    public void setCardBinding(BonusInfoItem cardBinding) {
        this.cardBinding = cardBinding;
    }

    /**
     *
     * @return
     * The orderFromApp
     */
    public BonusInfoItem getOrderFromApp() {
        return orderFromApp;
    }

    /**
     *
     * @param orderFromApp
     * The order_from_app
     */
    public void setOrderFromApp(BonusInfoItem orderFromApp) {
        this.orderFromApp = orderFromApp;
    }

    /**
     *
     * @return
     * The shareVk
     */
    public BonusInfoItem getShareVk() {
        return shareVk;
    }

    /**
     *
     * @param shareVk
     * The share_vk
     */
    public void setShareVk(BonusInfoItem shareVk) {
        this.shareVk = shareVk;
    }

    /**
     *
     * @return
     * The shareFb
     */
    public BonusInfoItem getShareFb() {
        return shareFb;
    }

    /**
     *
     * @param shareFb
     * The share_fb
     */
    public void setShareFb(BonusInfoItem shareFb) {
        this.shareFb = shareFb;
    }

    /**
     *
     * @return
     * The shareOk
     */
    public BonusInfoItem getShareOk() {
        return shareOk;
    }

    /**
     *
     * @param shareOk
     * The share_ok
     */
    public void setShareOk(BonusInfoItem shareOk) {
        this.shareOk = shareOk;
    }

}