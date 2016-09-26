package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 13 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderStatusService extends APIError implements Parcelable {

    @SerializedName("field")
    @Expose
    private String field;
    @SerializedName("price")
    @Expose
    private int price;

    public OrderStatusService() {

    }

    protected OrderStatusService(Parcel in) {
        field = in.readString();
        price = in.readInt();
    }

    public static final Creator<OrderStatusService> CREATOR = new Creator<OrderStatusService>() {
        @Override
        public OrderStatusService createFromParcel(Parcel in) {
            return new OrderStatusService(in);
        }

        @Override
        public OrderStatusService[] newArray(int size) {
            return new OrderStatusService[size];
        }
    };

    /**
     *
     * @return
     * The field
     */
    public String getField() {
        return field;
    }

    /**
     *
     * @param field
     * The field
     */
    public void setField(String field) {
        this.field = field;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(field);
        dest.writeInt(price);
    }
}
