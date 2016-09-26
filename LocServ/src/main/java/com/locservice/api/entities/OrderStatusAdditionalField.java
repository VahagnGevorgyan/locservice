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
public class OrderStatusAdditionalField extends APIError implements Parcelable {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("price")
    @Expose
    private int price;

    public OrderStatusAdditionalField() {

    }


    protected OrderStatusAdditionalField(Parcel in) {
        text = in.readString();
        price = in.readInt();
    }

    public static final Creator<OrderStatusAdditionalField> CREATOR = new Creator<OrderStatusAdditionalField>() {
        @Override
        public OrderStatusAdditionalField createFromParcel(Parcel in) {
            return new OrderStatusAdditionalField(in);
        }

        @Override
        public OrderStatusAdditionalField[] newArray(int size) {
            return new OrderStatusAdditionalField[size];
        }
    };

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
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
        dest.writeString(text);
        dest.writeInt(price);
    }
}
