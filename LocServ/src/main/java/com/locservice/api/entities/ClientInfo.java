package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ClientInfo extends APIError implements Parcelable {

    @SerializedName("bonus")
    @Expose
    private String bonus;
    @SerializedName("min_bonus_sum")
    @Expose
    private int minBonusSum;
    @SerializedName("can_use_bonus")
    @Expose
    private int canUseBonus;
    @SerializedName("has_cards")
    @Expose
    private int hasCards;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("sms_notification")
    @Expose
    private int smsNotification;
    @SerializedName("referral")
    @Expose
    private String referral;
    @SerializedName("corporation")
    @Expose
    private List<Corporation> corporation = new ArrayList<Corporation>();
    @SerializedName("photo_link")
    @Expose
    private String photoLink;
    @SerializedName("sale")
    @Expose
    private String sale;
    @SerializedName("can_use_nocash")
    @Expose
    private int can_use_nocash;

    private String fbId;
    private String vkId;
    private String base64Image;

    public ClientInfo () {

    }

    protected ClientInfo(Parcel in) {
        bonus = in.readString();
        minBonusSum = in.readInt();
        canUseBonus = in.readInt();
        hasCards = in.readInt();
        name = in.readString();
        email = in.readString();
        smsNotification = in.readInt();
        referral = in.readString();
        corporation = in.createTypedArrayList(Corporation.CREATOR);
        photoLink = in.readString();
        fbId = in.readString();
        vkId = in.readString();
        base64Image = in.readString();
        sale = in.readString();
        can_use_nocash = in.readInt();
    }

    public static final Creator<ClientInfo> CREATOR = new Creator<ClientInfo>() {
        @Override
        public ClientInfo createFromParcel(Parcel in) {
            return new ClientInfo(in);
        }

        @Override
        public ClientInfo[] newArray(int size) {
            return new ClientInfo[size];
        }
    };

    /**
     *
     * @return
     * The base64Image
     */
    public String getBase64Image() {
        return base64Image;
    }

    /**
     *
     * @param base64Image
     * The bonus
     */
    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    /**
     *
     * @return
     * The fbId
     */
    public String getFbId() {
        return fbId;
    }

    /**
     *
     * @param fbId
     * The bonus
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     *
     * @return
     * The vkId
     */
    public String getVkId() {
        return vkId;
    }

    /**
     *
     * @param vkId
     * The bonus
     */
    public void setVkId(String vkId) {
        this.vkId = vkId;
    }

    /**
     *
     * @return
     * The bonus
     */
    public String getBonus() {
        return bonus;
    }

    /**
     *
     * @param bonus
     * The bonus
     */
    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    /**
     *
     * @return
     * The minBonusSum
     */
    public int getMinBonusSum() {
        return minBonusSum;
    }

    /**
     *
     * @param minBonusSum
     * The min_bonus_sum
     */
    public void setMinBonusSum(int minBonusSum) {
        this.minBonusSum = minBonusSum;
    }

    /**
     *
     * @return
     * The canUseBonus
     */
    public int getCanUseBonus() {
        return canUseBonus;
    }

    /**
     *
     * @param canUseBonus
     * The can_use_bonus
     */
    public void setCanUseBonus(int canUseBonus) {
        this.canUseBonus = canUseBonus;
    }

    /**
     *
     * @return
     * The hasCards
     */
    public int getHasCards() {
        return hasCards;
    }

    /**
     *
     * @param hasCards
     * The has_cards
     */
    public void setHasCards(int hasCards) {
        this.hasCards = hasCards;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The smsNotification
     */
    public int getSmsNotification() {
        return smsNotification;
    }

    /**
     *
     * @param smsNotification
     * The sms_notification
     */
    public void setSmsNotification(int smsNotification) {
        this.smsNotification = smsNotification;
    }

    /**
     *
     * @return
     * The referral
     */
    public String getReferral() {
        return referral;
    }

    /**
     *
     * @param referral
     * The referral
     */
    public void setReferral(String referral) {
        this.referral = referral;
    }

    /**
     *
     * @return
     * The corporation
     */
    public List<Corporation> getCorporation() {
        return corporation;
    }

    /**
     *
     * @param corporation
     * The corporation
     */
    public void setCorporation(List<Corporation> corporation) {
        this.corporation = corporation;
    }

    /**
     *
     * @return
     * The photoLink
     */
    public String getPhotoLink() {
        return photoLink;
    }

    /**
     *
     * @param photoLink
     * The photo_link
     */
    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    /**
     *
     * @return
     * The photoLink
     */
    public String getSale() {
        return sale;
    }

    /**
     *
     * @param sale
     * The photo_link
     */
    public void setSale(String sale) {
        this.sale = sale;
    }

    /**
     *
     * @return
     * The photoLink
     */
    public int getCan_use_nocash() {
        return can_use_nocash;
    }

    /**
     *
     * @param can_use_nocash
     * The photo_link
     */
    public void setCan_use_nocash(int can_use_nocash) {
        this.can_use_nocash = can_use_nocash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bonus);
        dest.writeInt(minBonusSum);
        dest.writeInt(canUseBonus);
        dest.writeInt(hasCards);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeInt(smsNotification);
        dest.writeString(referral);
        dest.writeTypedList(corporation);
        dest.writeString(photoLink);
        dest.writeString(fbId);
        dest.writeString(vkId);
        dest.writeString(base64Image);
        dest.writeString(sale);
        dest.writeInt(can_use_nocash);
    }
}
