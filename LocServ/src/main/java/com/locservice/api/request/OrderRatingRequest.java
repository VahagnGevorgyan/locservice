package com.locservice.api.request;

/**
 * Created by Vahagn Gevorgyan
 * 28 March 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class OrderRatingRequest extends WebRequest {
    private String IdOrder;
    private int rating;
    private String comment;

    public OrderRatingRequest(String idOrder, int rating, String comment) {
        super("orderrating");
        IdOrder = idOrder;
        this.rating = rating;
        this.comment = comment;
    }

    public String getIdOrder() {
        return IdOrder;
    }

    public void setIdOrder(String idOrder) {
        IdOrder = idOrder;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
