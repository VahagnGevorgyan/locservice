package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.locservice.ui.helpers.OrderState;

/**
 * Created by Vahagn Gevorgyan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderTest implements Parcelable{

    private String name;
    private OrderState order_state;
    private String description;
    private int rate;
    private long price;
    private String thumbUrl;

    public OrderTest () {

    }

    protected OrderTest(Parcel in) {
        name = in.readString();
        description = in.readString();
        rate = in.readInt();
        price = in.readLong();
        thumbUrl = in.readString();
    }

    public static final Creator<OrderTest> CREATOR = new Creator<OrderTest>() {
        @Override
        public OrderTest createFromParcel(Parcel in) {
            return new OrderTest(in);
        }

        @Override
        public OrderTest[] newArray(int size) {
            return new OrderTest[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderState getOrder_state() {
        return order_state;
    }

    public void setOrder_state(OrderState order_state) {
        this.order_state = order_state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(rate);
        dest.writeLong(price);
        dest.writeString(thumbUrl);
    }
}
