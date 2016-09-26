package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 25 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GetFavoriteData extends APIError implements Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("entrance")
    @Expose
    private String entrance;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("IdLocality")
    @Expose
    private String IdLocality;
    @SerializedName("id")
    @Expose
    private String id;

    public GetFavoriteData () {

    }

    protected GetFavoriteData(Parcel in) {
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        entrance = in.readString();
        comment = in.readString();
        name = in.readString();
        IdLocality = in.readString();
        id = in.readString();
    }

    public static final Creator<GetFavoriteData> CREATOR = new Creator<GetFavoriteData>() {
        @Override
        public GetFavoriteData createFromParcel(Parcel in) {
            return new GetFavoriteData(in);
        }

        @Override
        public GetFavoriteData[] newArray(int size) {
            return new GetFavoriteData[size];
        }
    };

    /**
     *
     * @return
     * The address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The entrance
     */
    public String getEntrance() {
        return entrance;
    }

    /**
     *
     * @param entrance
     * The entrance
     */
    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    /**
     *
     * @return
     * The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment
     * The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     * The IdLocality
     */
    public String getIdLocality() {
        return IdLocality;
    }

    /**
     *
     * @param IdLocality
     * The IdLocality
     */
    public void setIdLocality(String IdLocality) {
        this.IdLocality = IdLocality;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(entrance);
        dest.writeString(comment);
        dest.writeString(name);
        dest.writeString(IdLocality);
        dest.writeString(id);
    }
}
