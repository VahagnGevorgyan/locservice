package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class Comment {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private long date;
    @SerializedName("viewed")
    @Expose
    private int viewed;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_place")
    @Expose
    private String userPlace;
    @SerializedName("user_photo")
    @Expose
    private String userPhoto;
    @SerializedName("byuser")
    @Expose
    private int byuser;

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
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
    public long getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The viewed
     */
    public int getViewed() {
        return viewed;
    }

    /**
     *
     * @param viewed
     * The viewed
     */
    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    /**
     *
     * @return
     * The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The user_name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     * The userPlace
     */
    public String getUserPlace() {
        return userPlace;
    }

    /**
     *
     * @param userPlace
     * The user_place
     */
    public void setUserPlace(String userPlace) {
        this.userPlace = userPlace;
    }

    /**
     *
     * @return
     * The userPhoto
     */
    public String getUserPhoto() {
        return userPhoto;
    }

    /**
     *
     * @param userPhoto
     * The user_photo
     */
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     *
     * @return
     * The byuser
     */
    public int getByuser() {
        return byuser;
    }

    /**
     *
     * @param byuser
     * The byuser
     */
    public void setByuser(int byuser) {
        this.byuser = byuser;
    }
}
