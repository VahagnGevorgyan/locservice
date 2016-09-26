package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 12 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverInfoOrder implements Parcelable {

    @SerializedName("IdOrder")
    @Expose
    private String IdOrder;
    @SerializedName("CollDate")
    @Expose
    private String CollDate;
    @SerializedName("CollAddrTypeMenu")
    @Expose
    private int CollAddrTypeMenu;
    @SerializedName("CollAddressText")
    @Expose
    private String CollAddressText;
    @SerializedName("tariffName")
    @Expose
    private String tariffName;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("Rate")
    @Expose
    private int Rate;

    public DriverInfoOrder() {

    }

    protected DriverInfoOrder(Parcel in) {
        IdOrder = in.readString();
        CollDate = in.readString();
        CollAddrTypeMenu = in.readInt();
        CollAddressText = in.readString();
        tariffName = in.readString();
        price = in.readInt();
        Rate = in.readInt();
    }

    public static final Creator<DriverInfoOrder> CREATOR = new Creator<DriverInfoOrder>() {
        @Override
        public DriverInfoOrder createFromParcel(Parcel in) {
            return new DriverInfoOrder(in);
        }

        @Override
        public DriverInfoOrder[] newArray(int size) {
            return new DriverInfoOrder[size];
        }
    };

    /**
     *
     * @return
     * The CollDate
     */
    public String getIdOrder() {
        return IdOrder;
    }

    /**
     *
     * @param idOrder
     * The CollDate
     */
    public void setIdOrder(String idOrder) {
        this.IdOrder = IdOrder;
    }

    /**
     *
     * @return
     * The CollDate
     */
    public String getCollDate() {
        return CollDate;
    }

    /**
     *
     * @param CollDate
     * The CollDate
     */
    public void setCollDate(String CollDate) {
        this.CollDate = CollDate;
    }

    /**
     *
     * @return
     * The CollAddrTypeMenu
     */
    public int getCollAddrTypeMenu() {
        return CollAddrTypeMenu;
    }

    /**
     *
     * @param CollAddrTypeMenu
     * The CollAddrTypeMenu
     */
    public void setCollAddrTypeMenu(int CollAddrTypeMenu) {
        this.CollAddrTypeMenu = CollAddrTypeMenu;
    }

    /**
     *
     * @return
     * The CollAddressText
     */
    public String getCollAddressText() {
        return CollAddressText;
    }

    /**
     *
     * @param CollAddressText
     * The CollAddressText
     */
    public void setCollAddressText(String CollAddressText) {
        this.CollAddressText = CollAddressText;
    }

    /**
     *
     * @return
     * The tariffName
     */
    public String getTariffName() {
        return tariffName;
    }

    /**
     *
     * @param tariffName
     * The tariffName
     */
    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    /**
     *
     * @return
     * The price
     */
    public int getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The Rate
     */
    public int getRate() {
        return Rate;
    }

    /**
     *
     * @param Rate
     * The Rate
     */
    public void setRate(int Rate) {
        this.Rate = Rate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IdOrder);
        dest.writeString(CollDate);
        dest.writeInt(CollAddrTypeMenu);
        dest.writeString(CollAddressText);
        dest.writeString(tariffName);
        dest.writeInt(price);
        dest.writeInt(Rate);
    }
}
