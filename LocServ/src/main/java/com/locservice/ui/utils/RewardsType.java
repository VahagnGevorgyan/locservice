package com.locservice.ui.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vahagn Gevorgyan
 * 11 August 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public enum  RewardsType implements Parcelable {

    FIRST_TRIP,
    SHARE,
    BIND_CARD,
    EVERY_TRIP;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public RewardsType createFromParcel(Parcel in) {
            return RewardsType.values()[in.readInt()];
        }

        public RewardsType[] newArray(int size) {
            return new RewardsType[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ordinal());
    }
}
