package com.locservice.api.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vahagn Gevorgyan
 * 23 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class GooglePlace implements Parcelable {
    private String id;
    private List<Integer> placeTypes;
    private String address;
    private Locale locale;
    private String name;
    private LatLng latLng;
    private LatLngBounds latLngBounds;
    private Uri websiteUri;
    private String phoneNumber;
    private float rating;
    private int priceLevel;
    private boolean isAirport;
    private boolean isRailwayStation;
    private String airportTerminalId;
    private boolean isFavorite;
    private boolean isLastAddress;
    private String entrance;
    private String comment;
    private boolean hasPlaceId;

    private String area;

    public GooglePlace() {

    }

    protected GooglePlace(Parcel in) {
        id = in.readString();
        address = in.readString();
        name = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        latLngBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        websiteUri = in.readParcelable(Uri.class.getClassLoader());
        phoneNumber = in.readString();
        rating = in.readFloat();
        priceLevel = in.readInt();
        isAirport = in.readByte() != 0;
        isRailwayStation = in.readByte() != 0;
        airportTerminalId = in.readString();
        isFavorite = in.readByte() != 0;
        isLastAddress = in.readByte() != 0;
        placeTypes = (List<Integer>) in.readSerializable();
        entrance = in.readString();
        comment = in.readString();
        hasPlaceId = in.readByte() != 0;
        area = in.readString();
    }

    public static final Creator<GooglePlace> CREATOR = new Creator<GooglePlace>() {
        @Override
        public GooglePlace createFromParcel(Parcel in) {
            return new GooglePlace(in);
        }

        @Override
        public GooglePlace[] newArray(int size) {
            return new GooglePlace[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public boolean isAirport() {
        return isAirport;
    }

    public void setIsAirport(boolean isAirport) {
        this.isAirport = isAirport;
    }

    public boolean isRailwayStation() {
        return isRailwayStation;
    }

    public void setIsRailwayStation(boolean isRailwayStation) {
        this.isRailwayStation = isRailwayStation;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isLastAddress() {
        return isLastAddress;
    }

    public void setIsLastAddress(boolean isLastAddress) {
        this.isLastAddress = isLastAddress;
    }


    public String getAirportTerminalId() {
        return airportTerminalId;
    }

    public void setAirportTerminalId(String airportTerminalId) {
        this.airportTerminalId = airportTerminalId;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isHasPlaceId() {
        return hasPlaceId;
    }

    public void setHasPlaceId(boolean hasPlaceId) {
        this.hasPlaceId = hasPlaceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(address);
        dest.writeString(name);
        dest.writeParcelable(latLng, flags);
        dest.writeParcelable(latLngBounds, flags);
        dest.writeParcelable(websiteUri, flags);
        dest.writeString(phoneNumber);
        dest.writeFloat(rating);
        dest.writeInt(priceLevel);
        dest.writeByte((byte) (isAirport ? 1 : 0));
        dest.writeByte((byte) (isRailwayStation ? 1 : 0));
        dest.writeString(airportTerminalId);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeByte((byte) (isLastAddress ? 1 : 0));
        dest.writeSerializable((Serializable) placeTypes);
        dest.writeString(entrance);
        dest.writeString(comment);
        dest.writeByte((byte) (hasPlaceId ? 1 : 0));
        dest.writeString(area);
    }
}
