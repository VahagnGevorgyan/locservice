package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 02 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderStatusData extends APIError implements Parcelable {
    @SerializedName("IdOrder")
    @Expose
    private String IdOrder;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("bonus")
    @Expose
    private String bonus;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("trip_time")
    @Expose
    private String tripTime;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("services")
    @Expose
    private List<OrderStatusService> services = new ArrayList<OrderStatusService>();
    @SerializedName("additionalFields")
    @Expose
    private List<OrderStatusAdditionalField> additionalFields = new ArrayList<OrderStatusAdditionalField>();
    @SerializedName("can_pay")
    @Expose
    private int canPay;
    @SerializedName("cameout")
    @Expose
    private int cameout;

    public OrderStatusData () {

    }

    protected OrderStatusData(Parcel in) {
        IdOrder = in.readString();
        status = in.readString();
        bonus = in.readString();
        price = in.readString();
        tripTime = in.readString();
        title = in.readString();
        subTitle = in.readString();
        services = in.createTypedArrayList(OrderStatusService.CREATOR);
        additionalFields = in.createTypedArrayList(OrderStatusAdditionalField.CREATOR);
        canPay = in.readInt();
        cameout = in.readInt();
    }

    public static final Creator<OrderStatusData> CREATOR = new Creator<OrderStatusData>() {
        @Override
        public OrderStatusData createFromParcel(Parcel in) {
            return new OrderStatusData(in);
        }

        @Override
        public OrderStatusData[] newArray(int size) {
            return new OrderStatusData[size];
        }
    };

    /**
     *
     * @return
     * The IdOrder
     */
    public String getIdOrder() {
        return IdOrder;
    }

    /**
     *
     * @param IdOrder
     * The IdOrder
     */
    public void setIdOrder(String IdOrder) {
        this.IdOrder = IdOrder;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
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
     * The price
     */
    public String getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The tripTime
     */
    public String getTripTime() {
        return tripTime;
    }

    /**
     *
     * @param tripTime
     * The trip_time
     */
    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The subTitle
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     *
     * @param subTitle
     * The sub_title
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     *
     * @return
     * The additionalFields
     */
    public List<OrderStatusAdditionalField> getAdditionalFields() {
        return additionalFields;
    }

    /**
     *
     * @param additionalFields
     * The additionalFields
     */
    public void setAdditionalFields(List<OrderStatusAdditionalField> additionalFields) {
        this.additionalFields = additionalFields;
    }

    /**
     *
     * @return
     * The services
     */
    public List<OrderStatusService> getServices() {
        return services;
    }

    /**
     *
     * @param services
     * The services
     */
    public void setServices(List<OrderStatusService> services) {
        this.services = services;
    }

    /**
     *
     * @return
     * The canPay
     */
    public int getCanPay() {
        return canPay;
    }

    /**
     *
     * @param canPay
     * The canPay
     */
    public void setCanPay(int canPay) {
        this.canPay = canPay;
    }

    /**
     *
     * @return
     * The cameout
     */
    public int getCameout() {
        return cameout;
    }

    /**
     *
     * @param cameout
     * The cameout
     */
    public void setCameout(int cameout) {
        this.cameout = cameout;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IdOrder);
        dest.writeString(status);
        dest.writeString(bonus);
        dest.writeString(price);
        dest.writeString(tripTime);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeTypedList(services);
        dest.writeTypedList(additionalFields);
        dest.writeInt(canPay);
        dest.writeInt(cameout);
    }
}
