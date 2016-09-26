package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Vahagn Gevorgyan
 * 27 June 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DateData implements Parcelable {
    private Date date;
    private String title;
    private boolean isHurry;
    private boolean isHalfHour;


    public DateData() {

    }

    protected DateData(Parcel in) {
        title = in.readString();
        isHurry = in.readByte() != 0;
        isHalfHour = in.readByte() != 0;
    }

    public static final Creator<DateData> CREATOR = new Creator<DateData>() {
        @Override
        public DateData createFromParcel(Parcel in) {
            return new DateData(in);
        }

        @Override
        public DateData[] newArray(int size) {
            return new DateData[size];
        }
    };

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHurry() {
        return isHurry;
    }

    public void setHurry(boolean hurry) {
        isHurry = hurry;
    }

    public boolean isHalfHour() {
        return isHalfHour;
    }

    public void setHalfHour(boolean halfHour) {
        isHalfHour = halfHour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeByte((byte) (isHurry ? 1 : 0));
        dest.writeByte((byte) (isHalfHour ? 1 : 0));
    }
}
