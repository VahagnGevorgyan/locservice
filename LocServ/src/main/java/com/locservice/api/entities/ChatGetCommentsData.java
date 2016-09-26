package com.locservice.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 18 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class ChatGetCommentsData extends APIError {

    @SerializedName("comments")
    @Expose
    private List<Comment> comments = new ArrayList<Comment>();
    @SerializedName("is_last")
    @Expose
    private int isLast;

    /**
     *
     * @return
     * The comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The isLast
     */
    public int getIsLast() {
        return isLast;
    }

    /**
     *
     * @param isLast
     * The is_last
     */
    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }
}
