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
public class Corporation implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("corporation_status")
    @Expose
    private String corporationStatus;

    public Corporation(){

    }

    protected Corporation(Parcel in) {
        id = in.readString();
        name = in.readString();
        corporationStatus = in.readString();
    }

    public static final Creator<Corporation> CREATOR = new Creator<Corporation>() {
        @Override
        public Corporation createFromParcel(Parcel in) {
            return new Corporation(in);
        }

        @Override
        public Corporation[] newArray(int size) {
            return new Corporation[size];
        }
    };

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
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
     * The corporationStatus
     */
    public String getCorporationStatus() {
        return corporationStatus;
    }

    /**
     *
     * @param corporationStatus
     * The corporation_status
     */
    public void setCorporationStatus(String corporationStatus) {
        this.corporationStatus = corporationStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(corporationStatus);
    }
}
