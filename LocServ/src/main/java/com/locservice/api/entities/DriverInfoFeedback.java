package com.locservice.api.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 12 April 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class DriverInfoFeedback implements Parcelable {

    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private String date;

    public DriverInfoFeedback() {

    }

    protected DriverInfoFeedback(Parcel in) {
        rating = in.readString();
        comment = in.readString();
        date = in.readString();
    }

    public static final Creator<DriverInfoFeedback> CREATOR = new Creator<DriverInfoFeedback>() {
        @Override
        public DriverInfoFeedback createFromParcel(Parcel in) {
            return new DriverInfoFeedback(in);
        }

        @Override
        public DriverInfoFeedback[] newArray(int size) {
            return new DriverInfoFeedback[size];
        }
    };

    /**
     *
     * @return
     * The rating
     */
    public String getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    public void setRating(String rating) {
        this.rating = rating;
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
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rating);
        dest.writeString(comment);
        dest.writeString(date);
    }
}
