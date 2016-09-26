package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hayk Yegoryan
 * 03 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Review implements Parcelable {
    private String date;
    private String description;
    private int rate;

    public Review () {

    }
    protected Review(Parcel in) {
        date = in.readString();
        description = in.readString();
        rate = in.readInt();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(description);
        dest.writeInt(rate);
    }
}
