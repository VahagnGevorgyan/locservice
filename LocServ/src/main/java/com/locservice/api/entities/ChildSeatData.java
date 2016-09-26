package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 10 May 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChildSeatData implements Parcelable {

    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("age")
    @Expose
    private int age;

    public ChildSeatData() {

    }

    protected ChildSeatData(Parcel in) {
        weight = in.readString();
        age = in.readInt();
    }

    public static final Creator<ChildSeatData> CREATOR = new Creator<ChildSeatData>() {
        @Override
        public ChildSeatData createFromParcel(Parcel in) {
            return new ChildSeatData(in);
        }

        @Override
        public ChildSeatData[] newArray(int size) {
            return new ChildSeatData[size];
        }
    };

    /**
     *
     * @return
     * The weight
     */
    public String getWeight() {
        return weight;
    }

    /**
     *
     * @param weight
     * The weight
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     *
     * @return
     * The age
     */
    public int getAge() {
        return age;
    }

    /**
     *
     * @param age
     * The age
     */
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(weight);
        dest.writeInt(age);
    }
}
